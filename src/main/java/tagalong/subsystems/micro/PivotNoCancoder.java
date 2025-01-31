/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */
package tagalong.subsystems.micro;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.simulation.BatterySim;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;
import tagalong.TagalongConfiguration;
import tagalong.subsystems.micro.confs.PivotConf;

/**
 * Pivot microsystem
 */
public class PivotNoCancoder extends Pivot {
  /**
   * Constructs a pivot microsystem with the below configurations
   *
   * @param conf Configurations for the pivot
   */
  public PivotNoCancoder(PivotConf conf) {
    super(conf);
    if (_configuredMicrosystemDisable) {
      return;
    }
    configAllDevices();
    configMotor();
  }

  @Override
  public void followLastProfile() {
    if (_isMicrosystemDisabled) {
      return;
    }

    TrapezoidProfile.State nextState =
        _trapProfile.calculate(TagalongConfiguration.LOOP_PERIOD_S, _curState, _goalState);
    // FUTURE DEV: modify to allow for unfused or not 1:1 with pivot, convert to motor units
    _primaryMotor.setControl(
        _requestedPositionVoltage
            .withPosition(pivotRotToMotor(nextState.position))
            // FeedForward must know the pivot rotation and other arguments in radians
            .withFeedForward(
                _pivotFF.calculate(getFFPositionRad(), Units.rotationsToRadians(nextState.velocity))
            )
    );

    if (_isShuffleboardMicro) {
      _targetPositionEntry.setDouble(nextState.position);
      _targetVelocityEntry.setDouble(nextState.velocity);
    }

    _curState = nextState;
  }

  @Override
  public double getFFPositionRad() {
    if (_isMicrosystemDisabled) {
      return 0.0;
    }

    // FUTURE DEV: modify to allow for unfused or not 1:1 with pivot
    return Units.rotationsToRadians(getPivotPosition()) + _ffCenterOfMassOffsetRad;
  }

  @Override
  public void setPivotVelocity(double rps, boolean withFF) {
    if (_isMicrosystemDisabled) {
      return;
    }
    setFollowProfile(false);

    _primaryMotor.setControl(
        _requestedVelocityVoltage
            // FUTURE DEV: modify to allow for unfused or not 1:1 with pivot
            .withVelocity(pivotRotToMotor(rps))
            .withFeedForward(
                withFF ? _pivotFF.calculate(getFFPositionRad(), Units.rotationsToRadians(rps)) : 0.0
            )
    );
  }

  @Override
  public double getPivotPosition() {
    return motorToPivotRot(getPrimaryMotorPosition());
  }

  @Override
  public double getPivotVelocity() {
    return motorToPivotRot(getPrimaryMotorVelocity());
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
  }

  @Override
  public void simulationPeriodic() {
    if (_isMicrosystemDisabled) {
      return;
    }
    _pivotSim.setInputVoltage(_primaryMotor.get() * RobotController.getBatteryVoltage());
    _pivotSim.update(TagalongConfiguration.LOOP_PERIOD_S);

    // FUTURE DEV: modify to allow for unfused or not 1:1 with pivot
    double prevSimVelo = _simVeloRPS;
    _simVeloRPS = Units.radiansToRotations(_pivotSim.getVelocityRadPerSec());
    _simRotations += Units.radiansToRotations(_pivotSim.getAngleRads());
    _simAccelRPS2 = (_simVeloRPS - prevSimVelo) / TagalongConfiguration.LOOP_PERIOD_S;

    _pivotLigament.setAngle(Rotation2d.fromRadians(_pivotSim.getAngleRads()));

    _primaryMotorSim.setRawRotorPosition(pivotRotToMotor(_simRotations));
    _primaryMotorSim.setRotorVelocity(pivotRotToMotor(_simVeloRPS));
    _primaryMotorSim.setRotorAcceleration(pivotRotToMotor(_simAccelRPS2));
    _primaryMotorSim.setSupplyVoltage(RobotController.getBatteryVoltage());

    RoboRioSim.setVInVoltage(
        BatterySim.calculateDefaultBatteryLoadedVoltage(_pivotSim.getCurrentDrawAmps())
    );
  }
}
