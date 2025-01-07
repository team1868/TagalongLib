/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.subsystems.micro;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.system.plant.LinearSystemId;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.simulation.BatterySim;
import edu.wpi.first.wpilibj.simulation.FlywheelSim;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color8Bit;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import java.util.ArrayList;
import tagalong.TagalongConfiguration;
import tagalong.math.AlgebraicUtils;
import tagalong.subsystems.micro.confs.RollerConf;

/**
 * Roller microsystem
 */
public class Roller extends Microsystem {
  /**
   * Configuration for the roller
   */
  public final RollerConf _rollerConf;

  /* -------- Hardware: motors and sensors -------- */

  /* -------- Control: states and constants -------- */
  /**
   * Ratio between motor rotations and roller rotations
   */
  private final double _motorToMechRatio;
  /**
   * Default lower tolerance for roller in rotations
   */
  public final double _defaultRollerLowerToleranceRot;
  /**
   * Default upper tolerance for roller in rotations
   */
  public final double _defaultRollerUpperToleranceRot;
  /**
   * Maximum velocity in rotations per second
   */
  public final double _maxVelocityRPS;
  /**
   * Maximum acceleration in rotations per second squared
   */
  public final double _maxAccelerationRPS2;

  /* -------- Control: controllers and utilities -------- */
  /**
   * Feedforward model of the roller
   */
  protected SimpleMotorFeedforward _rollerFF;

  /* -------- Sim -------- */
  /**
   * Flywheel simulation for the roller motor
   */
  protected FlywheelSim _rollerSim;
  /**
   * Simulated number of rotations
   */
  protected double _simRotations;
  /**
   * Simulated velocity in rotations per minute
   */
  protected double _simVeloRPM;
  /**
   * Simulated acceleration in rotations per minute squared
   */
  protected double _simAccelRPM2;
  /**
   * Simulated current angle of the roller
   */
  protected double _curSimAngle;
  /**
   * Roller ligaments
   */
  private ArrayList<MechanismLigament2d> rollerLigament = new ArrayList<MechanismLigament2d>();

  /**
   * Constructs a roller microsystem with the below configurations
   *
   * @param conf Configuration for the roller
   */
  public Roller(RollerConf conf) {
    super(conf);
    _rollerConf = conf;

    if (_configuredMicrosystemDisable) {
      _motorToMechRatio = 1.0;
      _maxVelocityRPS = 0.0;
      _maxAccelerationRPS2 = 0.0;
      _defaultRollerLowerToleranceRot = 0.0;
      _defaultRollerUpperToleranceRot = 0.0;
      return;
    }

    _rollerFF = _rollerConf.feedForward;
    _trapProfile = new TrapezoidProfile(_rollerConf.trapezoidalLimits);
    _motorToMechRatio = _rollerConf.motorToMechRatio;
    _maxVelocityRPS = conf.trapezoidalLimitsVelocity;
    _maxAccelerationRPS2 = conf.trapezoidalLimitsAcceleration;
    _defaultRollerLowerToleranceRot = _rollerConf.defaultLowerTolerance;
    _defaultRollerUpperToleranceRot = _rollerConf.defaultUpperTolerance;

    configAllDevices();
  }

  @Override
  public void onEnable() {
    if (_isMicrosystemDisabled) {
      return;
    }
    super.onEnable();

    if (_isFFTuningMicro) {
      _rollerFF = new SimpleMotorFeedforward(
          _KSEntry.getDouble(_rollerFF.getKs()),
          _KVEntry.getDouble(_rollerFF.getKv()),
          _KAEntry.getDouble(_rollerFF.getKa())
      );
      _primaryMotor.setControl(_requestedPositionVoltage.withFeedForward(_rollerFF.getKs()));
    }
  }

  @Override
  public void onDisable() {
    if (_isMicrosystemDisabled) {
      return;
    }

    super.onDisable();
  }

  /**
   * Periodic update function
   */
  public void periodic() {
    if (_isMicrosystemDisabled) {
      return;
    } else if (motorResetConfig()) {
      setRollerProfile(getRollerPosition(), 0.0);
      _primaryMotor.set(0.0);
    }

    if (_followProfile) {
      followLastProfile();
    }
  }

  @Override
  public void simulationInit() {
    if (_isMicrosystemDisabled) {
      return;
    }
    var dcMotor = _rollerConf.motorTypes[0].simSupplier.apply(_rollerConf.numMotors);
    _rollerSim = new FlywheelSim(
        LinearSystemId.createFlywheelSystem(dcMotor, _rollerConf.rollerMOI, _motorToMechRatio),
        dcMotor,
        null
    );
    _mechanism = new Mechanism2d(50, 50);
    SmartDashboard.putData("SIM: " + _rollerConf.name, _mechanism);

    _root = _mechanism.getRoot(_rollerConf.name, 25, 25);
    for (int i = 1; i <= _rollerConf.simNumLigaments; i++) {
      MechanismLigament2d ligament = new MechanismLigament2d(
          _rollerConf.name + " " + i, 10, i * (360 / _rollerConf.simNumLigaments)
      );
      rollerLigament.add(ligament);
      _root.append(ligament);

      // FUTURE DEV: allow for explicit color configuration
      ligament.setColor(new Color8Bit(
          i * 255 / _rollerConf.simNumLigaments,
          i * 255 / _rollerConf.simNumLigaments,
          i * 255 / _rollerConf.simNumLigaments
      ));
    }
    _primaryMotorSim = _primaryMotor.getSimState();
  }

  @Override
  public void simulationPeriodic() {
    if (_isMicrosystemDisabled) {
      return;
    }
    _rollerSim.setInput(getPrimaryMotorPower() * RobotController.getBatteryVoltage());
    _rollerSim.update(TagalongConfiguration.LOOP_PERIOD_S);

    double prevSimVelo = _simVeloRPM;
    _simVeloRPM = _rollerSim.getAngularVelocityRPM();
    _simRotations += _simVeloRPM * TagalongConfiguration.LOOP_PERIOD_S / 60.0;
    _simAccelRPM2 = (_simVeloRPM - prevSimVelo) * 60.0 / TagalongConfiguration.LOOP_PERIOD_S;

    for (int i = 1; i <= _rollerConf.simNumLigaments; i++) {
      rollerLigament.get(i - 1).setAngle(
          Rotation2d.fromRotations(_simRotations + (i * (1.0 / _rollerConf.simNumLigaments)))
      );
    }

    _primaryMotorSim.setRawRotorPosition(rollerRotToMotor(_simRotations));
    _primaryMotorSim.setRotorVelocity(rollerRotToMotor(_rollerSim.getAngularVelocityRPM() / 60.0));
    _primaryMotorSim.setRotorAcceleration(rollerRotToMotor(_simAccelRPM2 / 3600.0));
    _primaryMotorSim.setSupplyVoltage(RobotController.getBatteryVoltage());

    RoboRioSim.setVInVoltage(
        BatterySim.calculateDefaultBatteryLoadedVoltage(_rollerSim.getCurrentDrawAmps())
    );
  }

  @Override
  public boolean motorResetConfig() {
    if (_isMicrosystemDisabled) {
      return false;
    }
    return super.motorResetConfig();
  }

  @Override
  public void updateShuffleboard() {
    if (_isMicrosystemDisabled) {
      return;
    }
    if (_isShuffleboardMicro) {
      _currentPositionEntry.setDouble(getRollerPosition());
      _currentVelocityEntry.setDouble(getRollerVelocity());
    }
  }

  @Override
  public void configShuffleboard() {
    if (_isMicrosystemDisabled) {
      return;
    }
    super.configShuffleboard();
  }

  /**
   * Converts motor rotations to roller rotations
   *
   * @param motorRot motor rotations
   * @return roller rotations converted from motor rotations
   *
   */
  public double motorToRollerRot(double motorRot) {
    if (_isMicrosystemDisabled) {
      return 0.0;
    }
    return motorRot / _motorToMechRatio;
  }

  /**
   * Converts motor rotations to roller rotations
   *
   * @param rollerRot roller rotations
   * @return motor rotations converted from roller rotations
   */
  public double rollerRotToMotor(double rollerRot) {
    if (_isMicrosystemDisabled) {
      return 0.0;
    }
    return rollerRot * _motorToMechRatio;
  }

  /**
   * Calculates the next state according to the trapezoidal profile and requests the roller motor(s)
   * to arrive at the next position with feedforward
   */
  public void followLastProfile() {
    if (_isMicrosystemDisabled) {
      return;
    }
    TrapezoidProfile.State nextState =
        _trapProfile.calculate(TagalongConfiguration.LOOP_PERIOD_S, _curState, _goalState);

    // Control and FeedForward based on mechanism rotations rather than motor rotations
    _primaryMotor.setControl(_requestedPositionVoltage
                                 .withPosition(rollerRotToMotor(nextState.position))
                                 .withFeedForward(_rollerFF.calculate(nextState.velocity)));

    if (_isShuffleboardMicro) {
      _targetPositionEntry.setDouble(nextState.position);
      _targetVelocityEntry.setDouble(nextState.velocity);
    }

    _curState = nextState;
  }

  /**
   * Creates a new trapezoidal profile for the roller to follow
   *
   * @param goalPositionRot goal position in rotations
   * @param goalVelocityRPS goal velocity in rotations per second
   */
  public void setRollerProfile(double goalPositionRot, double goalVelocityRPS) {
    setRollerProfile(goalPositionRot, goalVelocityRPS, _maxVelocityRPS);
  }

  /**
   * Creates a new trapezoidal profile for the roller to follow
   *
   * @param goalPositionRot goal position in rotations
   * @param goalVelocityRPS goal velocity in rotations per second
   * @param maxVelocityRPS  maximum velocity in rotations per second
   */
  public void setRollerProfile(
      double goalPositionRot, double goalVelocityRPS, double maxVelocityRPS
  ) {
    setRollerProfile(goalPositionRot, goalVelocityRPS, maxVelocityRPS, _maxAccelerationRPS2);
  }
  /**
   * Creates a new trapezoidal profile for the roller to follow
   *
   * @param goalPositionRot     goal position in rotations
   * @param goalVelocityRPS     goal velocity in rotations per second
   * @param maxVelocityRPS      maximum velocity in rotations per second
   * @param maxAccelerationRPS2 maximum acceleration in rotations per second squared
   */
  public void setRollerProfile(
      double goalPositionRot,
      double goalVelocityRPS,
      double maxVelocityRPS,
      double maxAccelerationRPS2
  ) {
    setRollerProfile(goalPositionRot, goalVelocityRPS, maxVelocityRPS, maxAccelerationRPS2, true);
  }

  /**
   * Creates a new trapezoidal profile for the roller to follow
   *
   * @param goalPositionRot     goal position in rotations
   * @param goalVelocityRPS     goal velocity in rotations per second
   * @param maxVelocityRPS      maximum velocity in rotations per second
   * @param maxAccelerationRPS2 maximum acceleration in rotations per second squared
   * @param setCurrentState     True if the profiles current state should base itself off sensor
   *     values rather than continue from the existing state
   */
  public void setRollerProfile(
      double goalPositionRot,
      double goalVelocityRPS,
      double maxVelocityRPS,
      double maxAccelerationRPS2,
      boolean setCurrentState
  ) {
    if (_isMicrosystemDisabled) {
      return;
    }
    setFollowProfile(false);

    if (setCurrentState) {
      _curState.position = getRollerPosition();
      _curState.velocity = getRollerVelocity();
    }

    _goalState.position = goalPositionRot;
    _goalState.velocity = goalVelocityRPS;

    _trapProfile = new TrapezoidProfile(
        (maxVelocityRPS >= _maxVelocityRPS || maxAccelerationRPS2 >= _maxAccelerationRPS2)
            ? _rollerConf.trapezoidalLimits
            : new TrapezoidProfile.Constraints(maxVelocityRPS, maxAccelerationRPS2)
    );

    _profileTimer.restart();
  }

  /**
   * Sets the power of the primary roller motor
   *
   * @param power roller power
   */
  public void setRollerPower(double power) {
    setPrimaryPower(power);
  }

  /**
   * Gets the power of the primary roller motor
   *
   * @return roller power
   */
  public double getRollerPower() {
    return getPrimaryMotorPower();
  }

  /**
   * Gets the position of the roller in rotations
   *
   * @return roller position in rotations
   */
  public double getRollerPosition() {
    return motorToRollerRot(getPrimaryMotorPosition());
  }

  /**
   * Gets the velocity of roller in rotations
   *
   * @return roller velocity
   */
  public double getRollerVelocity() {
    return motorToRollerRot(getPrimaryMotorVelocity());
  }

  /**
   * Sets the velocity of the roller in RPS
   *
   * @param rps    Desired velocity in rotations per second
   * @param withFF with feedforward
   */
  public void setRollerVelocity(double rps, boolean withFF) {
    if (_isMicrosystemDisabled) {
      return;
    }

    setFollowProfile(false);
    _primaryMotor.setControl(_requestedVelocityVoltage.withVelocity(rollerRotToMotor(rps))
                                 .withFeedForward(withFF ? _rollerFF.calculate(rps, 0.0) : 0.0));
  }

  /**
   * Checks if the roller is at the target speed
   *
   * @param targetSpeed target speed in rotations per second
   * @return if roller is at target speed
   */
  public boolean isRollerAtTargetSpeed(double targetSpeed) {
    if (_isMicrosystemDisabled) {
      return true;
    }
    return AlgebraicUtils.inTolerance(
        getRollerVelocity() - targetSpeed,
        -_defaultRollerLowerToleranceRot,
        _defaultRollerUpperToleranceRot
    );
  }

  /**
   * Command that sets the roller power
   *
   * @param power roller power
   * @return instant command to set roller power
   */
  public Command setRollerPowerCmd(double power) {
    return new InstantCommand(() -> setRollerPower(power));
  }

  /**
   * Command that sets the velocity of the roller in rotations per second
   *
   * @param rps rotations per second
   * @return instant command to set roller velocity
   */
  public Command setRollerRPSCmd(double rps) {
    return new InstantCommand(() -> setRollerVelocity(rps, false));
  }

  /**
   * Command that sets the velocity of the roller in rotations per second with feedforward
   *
   * @param rps rotations per second
   * @return instant command to set roller velocity with feedforward
   */
  public Command setRollerRPSWithFFCmd(double rps) {
    return new InstantCommand(() -> setRollerVelocity(rps, true));
  }

  /**
   * Command that sets the power of the primary motor (zero if interrupted)
   *
   * @param power roller power
   * @return start end command to set roller power
   */
  public Command startEndRollerPowerCmd(double power) {
    return (Commands.startEnd(() -> setRollerPower(power), () -> setRollerPower(0.0)));
  }

  /**
   * Command that sets the velocity of the roller in rotations per second (sets zero power if
   * interrupted)
   *
   * @param rps rotations per second
   * @return start end command to set roller velocity
   */
  public Command startEndRollerRPSCmd(double rps) {
    return Commands.startEnd(() -> setRollerVelocity(rps, false), () -> setRollerPower(0.0));
  }

  /**
   * Command that sets the velocity of the roller in rotations per second with feedforward. (sets
   * zero power if interrupted)
   *
   * @param rps rotations per second
   * @return start end command to set roller velocity with feedforward
   */
  public Command startEndRollerRPSWithFFCmd(double rps) {
    return Commands.startEnd(() -> setRollerVelocity(rps, true), () -> setRollerPower(0.0));
  }

  /**
   * Bounds checking function that uses the current roller position
   *
   * @param lowerBound Minimum of acceptable range
   * @param upperBound Maximum of acceptable range
   * @return If the current position is in specified acceptable range
   */
  public boolean isRollerInTolerance(double lowerBound, double upperBound) {
    if (_isMicrosystemDisabled) {
      return true;
    }
    return AlgebraicUtils.inTolerance(getRollerPosition(), lowerBound, upperBound);
  }

  @Override
  public void holdCurrentPosition() {
    setRollerProfile(getRollerPosition(), 0.0);
    setFollowProfile(true);
  }
}
