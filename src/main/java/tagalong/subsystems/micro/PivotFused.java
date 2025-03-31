/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */
package tagalong.subsystems.micro;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.simulation.BatterySim;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;
import org.littletonrobotics.junction.Logger;
import tagalong.TagalongConfiguration;
import tagalong.subsystems.micro.confs.PivotConf;

/**
 * Pivot microsystem
 */
public class PivotFused extends Pivot {
  /**
   * CANcoder device
   */
  public CANcoder _pivotCancoder;
  /**
   * Configuration for the CANcoder
   */
  protected CANcoderConfiguration _pivotCancoderConfiguration;
  protected boolean _fusedCancoderSetup = false;
  /**
   * Constructs a pivot microsystem with the below configurations
   *
   * @param conf Configurations for the pivot
   */
  public PivotFused(PivotConf conf) {
    super(conf);
    if (_configuredMicrosystemDisable) {
      return;
    }
    _pivotCancoder = new CANcoder(_pivotConf.encoderDeviceID, _pivotConf.encoderCanBus);
    _pivotCancoderConfiguration = _pivotConf.encoderConfig;
    configCancoder();
    configAllDevices();
    configMotor();
    _pivotCancoder.getAbsolutePosition().waitForUpdate(0.5);
    _primaryMotor.getPosition().waitForUpdate(0.5);
    // int count = 0;
    // while (_primaryMotor.getPosition().getStatus() != StatusCode.OK && count <= 1000) {
    //   System.out.println(_pivotConf.name + " not ok " + count++);
    // }
    // if (count > 1000) {
    //   System.out.println(_pivotConf.name + " failed to initialize!");
    // }
    double pivotPosition = getPivotPosition();
    Logger.recordOutput(_conf.name + " initial pivot position", pivotPosition);
    double average = (_pivotConf.rotationalMin + _pivotConf.rotationalMax) / 2.0;
    double min = average - 0.5;
    double max = average + 0.5;
    while (min + _scopeOffset > pivotPosition) {
      _scopeOffset -= 1.0;
    }
    while (max + _scopeOffset < pivotPosition) {
      _scopeOffset += 1.0;
    }
    _minPositionRot = min + _scopeOffset;
    _maxPositionRot = max + _scopeOffset;

    _absoluteRangeRot = _maxPositionRot - _minPositionRot;
  }

  @Override
  public void followLastProfile() {
    if (_isMicrosystemDisabled) {
      return;
    }

    TrapezoidProfile.State nextState =
        _trapProfile.calculate(TagalongConfiguration.LOOP_PERIOD_S, _curState, _goalState);
    _primaryMotor.setControl(
        _requestedPositionVoltage.withPosition(nextState.position)
            .withFeedForward(
                _pivotFF.calculate(getFFPositionRad(), Units.rotationsToRadians(nextState.velocity))
            )
    );

    if (_isShuffleboardMicro) {
      _targetPositionEntry.setDouble(nextState.position);
      _targetVelocityEntry.setDouble(nextState.velocity);
    }

    _curState = nextState;
    Logger.recordOutput(_conf.name + "pivot next state", nextState.position);
  }

  @Override
  public double getFFPositionRad() {
    if (_isMicrosystemDisabled) {
      return 0.0;
    }

    return Units.rotationsToRadians(_pivotCancoder.getPosition().getValueAsDouble())
        + _ffCenterOfMassOffsetRad;
  }

  @Override
  public void setPivotVelocity(double rps, boolean withFF) {
    if (_isMicrosystemDisabled) {
      return;
    }
    setFollowProfile(false);

    _primaryMotor.setControl(_requestedVelocityVoltage.withVelocity(rps).withFeedForward(
        withFF ? _pivotFF.calculate(getFFPositionRad(), Units.rotationsToRadians(rps)) : 0.0
    ));
  }

  @Override
  public double getPivotPosition() {
    return getPrimaryMotorPosition();
  }

  @Override
  public double getPivotVelocity() {
    return getPrimaryMotorVelocity();
  }

  /**
   * Configures the CANcoder according to specified configuration
   */
  protected void configCancoder() {
    if (_isMicrosystemDisabled) {
      return;
    }

    _pivotCancoder.getConfigurator().apply(_pivotCancoderConfiguration);
  }

  public CANcoder getPivotCancoder() {
    return _pivotCancoder;
  }

  @Override
  protected void configMotor() {
    for (int i = 0; i < _conf.numMotors; i++) {
      _conf.motorConfig[i].withFeedback(
          new FeedbackConfigs()
              .withFeedbackSensorSource(FeedbackSensorSourceValue.FusedCANcoder)
              .withFeedbackRemoteSensorID(_pivotCancoder.getDeviceID())
              .withRotorToSensorRatio(_pivotConf.motorToEncoderRatio)
              .withSensorToMechanismRatio(_pivotConf.encoderToPivotRatio)
      );
      _allMotors[i].getConfigurator().apply(_conf.motorConfig[i]);
    }
  }

  @Override
  public boolean motorResetConfig() {
    if (_isMicrosystemDisabled) {
      return false;
    }
    if (_primaryMotor.hasResetOccurred()) {
      configAllDevices();
      configMotor();
      return true;
    }
    if (_pivotCancoder.hasResetOccurred()) {
      configCancoder();
      return true;
    }
    return false;
  }

  /**
   * Initializes the pivot simulation
   */
  @Override
  public void simulationInit() {
    if (_isMicrosystemDisabled) {
      return;
    }
    super.simulationInit();
    _pivotCancoderSim = _pivotCancoder.getSimState();
  }

  @Override
  public void simulationPeriodic() {
    if (_isMicrosystemDisabled) {
      return;
    }
    _pivotSim.setInputVoltage(_primaryMotor.get() * RobotController.getBatteryVoltage());
    _pivotSim.update(TagalongConfiguration.LOOP_PERIOD_S);

    double prevSimVelo = _simVeloRPS;
    _simVeloRPS = Units.radiansToRotations(_pivotSim.getVelocityRadPerSec());
    _simRotations += Units.radiansToRotations(_pivotSim.getAngleRads());
    _simAccelRPS2 = (_simVeloRPS - prevSimVelo) / TagalongConfiguration.LOOP_PERIOD_S;

    _pivotLigament.setAngle(Rotation2d.fromRadians(_pivotSim.getAngleRads()));

    _pivotCancoderSim.setRawPosition(Units.radiansToRotations(_pivotSim.getAngleRads()));
    _pivotCancoderSim.setVelocity(_simVeloRPS);
    _pivotCancoderSim.setSupplyVoltage(RobotController.getBatteryVoltage());

    _primaryMotorSim.setRawRotorPosition(_simRotations);
    _primaryMotorSim.setRotorVelocity(_simVeloRPS);
    _primaryMotorSim.setRotorAcceleration(_simAccelRPS2);
    _primaryMotorSim.setSupplyVoltage(RobotController.getBatteryVoltage());

    RoboRioSim.setVInVoltage(
        BatterySim.calculateDefaultBatteryLoadedVoltage(_pivotSim.getCurrentDrawAmps())
    );
  }
}
