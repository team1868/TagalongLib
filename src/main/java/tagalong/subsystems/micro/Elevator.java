/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.subsystems.micro;

import com.ctre.phoenix6.signals.InvertedValue;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.simulation.BatterySim;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import tagalong.TagalongConfiguration;
import tagalong.math.AlgebraicUtils;
import tagalong.subsystems.micro.confs.ElevatorConf;

/**
 * Elevator Microsystem
 */
public class Elevator extends Microsystem {
  /**
   * Configuration for the elevator
   */
  public final ElevatorConf _elevatorConf;

  /* -------- Hardware: motors and sensors -------- */

  /* -------- Control: states and constants -------- */
  /**
   * Ratio between motor rotations and drum rotations
   */
  protected final double _motorToMechRatio;
  /**
   * Minimum height of the elevator in meters,
   * Maximum height of the elevator in meters
   */
  public final double _elevatorMinHeightM, _elevatorMaxHeightM;
  /**
   * Maximum velocity of the elevator in meters per second,
   * Maximum acceleration of the elevator in meters per second squared
   */
  public final double _maxVelocityMPS, _maxAccelerationMPS2;
  /**
   * Default lower tolerance of the elevator in meters,
   * Default upper tolerance of the elevator in meters
   */
  public final double _defaultElevatorLowerToleranceM, _defaultElevatorUpperToleranceM;
  /**
   * Power for zeroing the elevator
   */
  public final double _elevatorZeroingPower;
  /**
   * Positional tolerance in meters for zeroing the elevator
   */
  public final double _elevatorZeroingStallToleranceM;
  /**
   * Duration of stalling in seconds necessary for the elevator zero command to finish
   */
  public final double _elevatorZeroingDurationS;

  /* -------- Control: controllers and utilities -------- */
  /**
   * Feedforward model for the elevator
   */
  protected ElevatorFeedforward _elevatorFF;

  /* -------- Sim -------- */
  /**
   * Simulation for the elevator
   */
  protected ElevatorSim _elevatorSim;
  /**
   * Base stage ligament
   */
  protected MechanismLigament2d _elevatorBaseStageLigament;
  /**
   * Stage 1 ligament
   */
  protected MechanismLigament2d _elevatorStage1Ligament;
  /**
   * Velocity of the simulated elevator in meters per second
   */
  protected double _simVelocityMPS;
  /**
   * Whether or not the primary motor is inverted
   */
  protected boolean _primaryMotorInverted;

  /**
   * Constructs an elevator microsystem with the below configurations
   *
   * @param conf Configuration for the elevator
   */
  public Elevator(ElevatorConf conf) {
    super(conf);
    _elevatorConf = conf;

    if (_configuredMicrosystemDisable) {
      _motorToMechRatio = 1.0;
      _elevatorMinHeightM = 0.0;
      _elevatorMaxHeightM = 0.0;
      _maxVelocityMPS = 0.0;
      _maxAccelerationMPS2 = 0.0;
      _defaultElevatorLowerToleranceM = 0.0;
      _defaultElevatorUpperToleranceM = 0.0;
      _elevatorZeroingPower = 0.0;
      _elevatorZeroingStallToleranceM = 0.0;
      _elevatorZeroingDurationS = 0.0;
      return;
    }

    _elevatorFF = _elevatorConf.feedForward;
    _trapProfile = new TrapezoidProfile(_elevatorConf.trapezoidalLimits);
    _curState.position = getElevatorHeightM();
    _motorToMechRatio = _elevatorConf.motorToMechRatio;
    _elevatorMinHeightM = _elevatorConf.positionalMin;
    _elevatorMaxHeightM = _elevatorConf.positionalMax;
    _maxVelocityMPS = _elevatorConf.trapezoidalLimitsVelocity;
    _maxAccelerationMPS2 = _elevatorConf.trapezoidalLimitsAcceleration;
    _defaultElevatorLowerToleranceM = _elevatorConf.defaultLowerTolerance;
    _defaultElevatorUpperToleranceM = _elevatorConf.defaultUpperTolerance;
    _elevatorZeroingPower = -Math.abs(_elevatorConf.elevatorZeroingPower);
    _elevatorZeroingStallToleranceM = _elevatorConf.elevatorZeroingStallToleranceM;
    _elevatorZeroingDurationS = _elevatorConf.elevatorZeroingDurationS;

    configAllDevices();
  }

  // Override to ensure the position config happens after the devices are configured
  @Override
  public void configAllDevices() {
    super.configAllDevices();

    // FUTURE DEV: Look into if all motors or just the leader need their positions set?
    // for (var motor : _allMotors) motor.setPosition(0.0);
    _primaryMotor.setPosition(0.0);
  }

  /**
   * Calculates the next state according to the trapezoidal profile and requests the elevator
   * motor(s) to arrive at the next position with feedforward
   */
  public void followLastProfile() {
    if (_isMicrosystemDisabled) {
      return;
    }
    TrapezoidProfile.State nextState =
        _trapProfile.calculate(TagalongConfiguration.LOOP_PERIOD_S, _curState, _goalState);

    _primaryMotor.setControl(_requestedPositionVoltage
                                 .withPosition(metersToMotor(nextState.position))
                                 // State is in meters, so the FF can handle the units directly
                                 .withFeedForward(_elevatorFF.calculate(nextState.velocity)));

    if (_isShuffleboardMicro) {
      _targetPositionEntry.setDouble(nextState.position);
      _targetVelocityEntry.setDouble(nextState.velocity);
    }

    _curState = nextState;
  }

  /**
   * Creates a new trapezoidal profile for the elevator
   *
   * @param goalPositionM   The goal position to reach, in meters
   * @param goalVelocityMPS The goal velocity to reach, in meters per second
   */
  public void setElevatorProfile(double goalPositionM, double goalVelocityMPS) {
    setElevatorProfile(goalPositionM, goalVelocityMPS, _maxVelocityMPS);
  }

  /**
   * Creates a new trapezoidal profile for the elevator
   *
   * @param goalPositionM   The goal position to reach, in meters
   * @param goalVelocityMPS The goal velocity to reach, in meters per second
   * @param maxVelocityMPS  The maximum velocity, in meters per second
   */
  public void setElevatorProfile(
      double goalPositionM, double goalVelocityMPS, double maxVelocityMPS
  ) {
    setElevatorProfile(goalPositionM, goalVelocityMPS, maxVelocityMPS, _maxAccelerationMPS2);
  }

  /**
   * Creates a new trapezoidal profile for the elevator
   *
   * @param goalPositionM   The goal position to reach, in meters
   * @param goalVelocityMPS The goal velocity to reach, in meters per second
   * @param maxVelocityMPS  The maximum velocity, in meters per second
   * @param maxAccelerationMPS2 The maximum acceleration, in meters per second squared
   */
  public void setElevatorProfile(
      double goalPositionM,
      double goalVelocityMPS,
      double maxVelocityMPS,
      double maxAccelerationMPS2
  ) {
    setElevatorProfile(goalPositionM, goalVelocityMPS, maxVelocityMPS, maxAccelerationMPS2, true);
  }

  /**
   * Creates a new trapezoidal profile for the elevator
   *
   * @param goalPositionM       The goal position to reach, in meters
   * @param goalVelocityMPS     The goal velocity to reach, in meters per second
   * @param maxVelocityMPS      The maximum velocity, in meters per second
   * @param maxAccelerationMPS2 The maximum acceleration, in meters per second squared
   * @param setCurrentState     True if the profiles current state should base itself off sensor
   *     values rather than continue from the existing state
   */
  public void setElevatorProfile(
      double goalPositionM,
      double goalVelocityMPS,
      double maxVelocityMPS,
      double maxAccelerationMPS2,
      boolean setCurrentState
  ) {
    if (_isMicrosystemDisabled) {
      return;
    }
    setFollowProfile(false);

    if (setCurrentState) {
      _curState.position = getElevatorHeightM();
      _curState.velocity = getElevatorVelocityMPS();
    }

    _goalState.position =
        AlgebraicUtils.clamp(goalPositionM, _elevatorMinHeightM, _elevatorMaxHeightM);
    _goalState.velocity = goalVelocityMPS;

    _trapProfile = new TrapezoidProfile(
        (maxVelocityMPS >= _maxVelocityMPS || maxAccelerationMPS2 >= _maxAccelerationMPS2)
            ? _elevatorConf.trapezoidalLimits
            : new TrapezoidProfile.Constraints(maxVelocityMPS, maxAccelerationMPS2)
    );

    _profileTimer.restart();
  }

  /**
   * Gets the height of the elevator in meters
   *
   * @return height in meters
   */
  public double getElevatorHeightM() {
    return motorToMeters(getPrimaryMotorPosition());
  }

  /**
   * Gets the velocity of the elevator in meters per second
   *
   * @return velocity in meters per second
   */
  public double getElevatorVelocityMPS() {
    return motorToMeters(getPrimaryMotorVelocity());
  }

  /**
   * Sets the velocity of the elevator in MPS
   *
   * @param mps    Desired velocity in meters per second
   * @param withFF with feedforward
   */
  public void setElevatorVelocity(double mps, boolean withFF) {
    if (_isMicrosystemDisabled) {
      return;
    }

    setFollowProfile(false);
    _primaryMotor.setControl(_requestedVelocityVoltage.withVelocity(metersToMotor(mps))
                                 .withFeedForward(withFF ? _elevatorFF.calculate(mps) : 0.0));
  }

  /**
   * Clamps the elevator position to be within height limits
   *
   * @param target goal position of the elevator in meters
   * @return clamped goal position in meters
   */
  public double clampElevatorPosition(double target) {
    return AlgebraicUtils.clamp(target, _elevatorMinHeightM, _elevatorMaxHeightM);
  }

  @Override
  public void onEnable() {
    if (_isMicrosystemDisabled) {
      return;
    }
    super.onEnable();

    if (_isFFTuningMicro) {
      _elevatorFF = new ElevatorFeedforward(
          _KSEntry.getDouble(_elevatorFF.getKs()),
          _KGEntry.getDouble(_elevatorFF.getKg()),
          _KVEntry.getDouble(_elevatorFF.getKv()),
          _KAEntry.getDouble(_elevatorFF.getKa())
      );
      _primaryMotor.setControl(_requestedPositionVoltage.withFeedForward(_elevatorFF.getKs()));
    }
  }

  @Override
  public void onDisable() {
    if (_isMicrosystemDisabled) {
      return;
    }

    super.onDisable();
  }

  @Override
  public void periodic() {
    if (_isMicrosystemDisabled) {
      return;
    } else if (motorResetConfig()) {
      setFollowProfile(false);
      setElevatorProfile(getElevatorHeightM(), 0.0);
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
    _elevatorSim = new ElevatorSim(
        _elevatorConf.motorTypes[0].simSupplier.apply(_elevatorConf.numMotors),
        _motorToMechRatio,
        _elevatorConf.carriageMassValue,
        _elevatorConf.drumDiameter / 2.0,
        _elevatorMinHeightM,
        _elevatorMaxHeightM,
        true,
        0
    );
    _primaryMotorSim = _primaryMotor.getSimState();
    _mechanism = new Mechanism2d(_elevatorConf.mech2dDim, _elevatorConf.mech2dDim);
    _root = _mechanism.getRoot(_elevatorConf.rootName, _elevatorConf.rootX, _elevatorConf.rootY);
    _elevatorBaseStageLigament = new MechanismLigament2d(
        "BaseStage",
        _elevatorConf.lineLength,
        _elevatorConf.angle,
        6.0,
        new Color8Bit(Color.kAliceBlue)
    );
    _elevatorStage1Ligament = new MechanismLigament2d(
        "Stage1",
        _elevatorConf.lineLength,
        _elevatorConf.angle,
        6.0,
        new Color8Bit(Color.kLightSalmon)
    );
    _root.append(_elevatorBaseStageLigament);
    _root.append(_elevatorStage1Ligament);
    SmartDashboard.putData("SIM: " + _conf.name, _mechanism);
    _primaryMotorInverted = _conf.motorDirection[0] == InvertedValue.Clockwise_Positive;
  }

  @Override
  public void simulationPeriodic() {
    if (_isMicrosystemDisabled) {
      return;
    }
    _elevatorSim.setInputVoltage(_primaryMotor.getMotorVoltage().getValueAsDouble());
    _elevatorSim.update(TagalongConfiguration.LOOP_PERIOD_S);

    double simAccelMPS2 = (_elevatorSim.getVelocityMetersPerSecond() - _simVelocityMPS)
        / TagalongConfiguration.LOOP_PERIOD_S;
    _simVelocityMPS = _elevatorSim.getVelocityMetersPerSecond();

    _primaryMotorSim.setRawRotorPosition(metersToMotor(
        _primaryMotorInverted ? (-1 * _elevatorSim.getPositionMeters())
                              : _elevatorSim.getPositionMeters()
    ));
    _primaryMotorSim.setRotorVelocity(metersToMotor(
        _primaryMotorInverted ? (-1 * _elevatorSim.getVelocityMetersPerSecond())
                              : _elevatorSim.getVelocityMetersPerSecond()
    ));
    _primaryMotorSim.setRotorAcceleration(
        metersToMotor(_primaryMotorInverted ? (-1 * simAccelMPS2) : simAccelMPS2)
    );
    _elevatorStage1Ligament.setLength(_elevatorSim.getPositionMeters());
    _primaryMotorSim.setSupplyVoltage(RobotController.getBatteryVoltage());
    RoboRioSim.setVInVoltage(
        BatterySim.calculateDefaultBatteryLoadedVoltage(_elevatorSim.getCurrentDrawAmps())
    );
  }

  @Override
  public void updateShuffleboard() {
    if (_isMicrosystemDisabled) {
      return;
    }
    if (_isShuffleboardMicro) {
      _currentPositionEntry.setDouble(getElevatorHeightM());
      _currentVelocityEntry.setDouble(getElevatorVelocityMPS());
    }
  }

  /**
   * Configure shuffleboard for the elevator
   */
  @Override
  public void configShuffleboard() {
    if (_isMicrosystemDisabled) {
      return;
    }
    super.configShuffleboard();
  }

  /**
   * Converts motor rotations to elevator height
   *
   * @param rotation motor position in rotations
   * @return elevator position in meters
   */
  public double motorToMeters(double rotation) {
    return _isMicrosystemDisabled
        ? 0.0
        : (rotation * _elevatorConf.drumCircumference) / _motorToMechRatio;
  }

  /**
   * Converts elevator height to motor rotations
   *
   * @param meter elevator position in meters
   * @return motor position in rotations
   */
  public double metersToMotor(double meter) {
    return _isMicrosystemDisabled ? 0.0
                                  : (meter / _elevatorConf.drumCircumference) * _motorToMechRatio;
  }

  /**
   * Checks whether or not it's safe for the elevator to move
   *
   * @return whether or not it's safe to move
   */
  public boolean isSafeToMove() {
    return true;
  }

  /**
   * Bounds checking function that uses the current elevator position
   *
   * @param lowerBound minimum of acceptable range
   * @param upperBound maximum of acceptable range
   * @return if the current position is in specified acceptable range
   */
  public boolean isElevatorInTolerance(double lowerBound, double upperBound) {
    if (_isMicrosystemDisabled) {
      return true;
    }
    return AlgebraicUtils.inTolerance(getElevatorHeightM(), lowerBound, upperBound);
  }

  /**
   * Sets the position of the elevator in meters
   *
   * @param height goal position in meters
   */
  public void setElevatorHeight(double height) {
    if (_isMicrosystemDisabled) {
      return;
    }
    _primaryMotor.setPosition(metersToMotor(height));
  }

  @Override
  public void holdCurrentPosition() {
    setElevatorProfile(getElevatorHeightM(), 0.0);
    setFollowProfile(true);
  }

  /**
   * Retrieves base stage ligament attached to elevator Mechanism2d
   *
   * @return elevator base stage ligament
   */
  public MechanismLigament2d getElevatorBaseStageLigament() {
    return _elevatorBaseStageLigament;
  }

  /**
   * Retrieves stage 1 ligament attached to elevator Mechanism2d
   *
   * @return elevator stage 1 ligament
   */
  public MechanismLigament2d getElevatorStage1Ligament() {
    return _elevatorStage1Ligament;
  }
}
