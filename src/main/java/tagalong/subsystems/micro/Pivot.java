/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */
package tagalong.subsystems.micro;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.sim.CANcoderSimState;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.IterativeRobotBase;
import edu.wpi.first.wpilibj.simulation.SingleJointedArmSim;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color8Bit;
import tagalong.TagalongConfiguration;
import tagalong.math.AlgebraicUtils;
import tagalong.measurements.Angle;
import tagalong.subsystems.micro.confs.PivotConf;

/**
 * Pivot microsystem
 */
public class Pivot extends Microsystem {
  /**
   * Configuration for the pivot
   */
  public final PivotConf _pivotConf;

  /* -------- Control: states and constants -------- */
  /**
   * Gear ratio from the motor to encoder (conversion g.t. 1 represents a reduction)
   */
  protected double _motorToEncoderRatio;
  /**
   * Gear ratio from the encoder to pivot mechanism (conversion g.t. 1 represents a reduction)
   */
  protected double _encoderToPivotRatio;
  /**
   * Default lower tolerance of the pivot in rotations,
   * Default upper tolerance of the pivot in rotations
   */
  public final double _defaultPivotLowerToleranceRot, _defaultPivotUpperToleranceRot;
  /**
   * Absolute range of pivot movement in rotations
   */
  public final double _absoluteRangeRot;
  /**
   * Minimum position of the pivot in rotations,
   * Maximum position of the pivot in rotations
   */
  public final double _minPositionRot, _maxPositionRot;
  /**
   * Maximum velocity of the pivot in rotations per second,
   * Maximum acceleration of the pivot in rotations per second squared
   */
  public final double _maxVelocityRPS, _maxAccelerationRPS2;
  /**
   * Offset value for the target position
   */
  public final double _profileTargetOffset;

  /* -------- Control: controller and utilities -------- */
  /**
   * Feedforward model for the pivot
   */
  protected ArmFeedforward _pivotFF;
  /**
   * Offset value for the target position
   */
  public final double _ffCenterOfMassOffsetRad;
  /**
   * Positional values for safe clamp logic
   */
  private double[] _values;
  /**
   * IDs for safe clamp switch case
   */
  private int[] _ids;

  /* -------- Sim -------- */
  /**
   * Single jointed arm simulation for the pivot
   */
  protected SingleJointedArmSim _pivotSim;
  /**
   * CANcoder simulation
   */
  protected CANcoderSimState _pivotCancoderSim;
  /**
   * Position of the simulated pivot in rotations
   */
  protected double _simRotations;
  /**
   * Velocity of the simulated pivot in rotations per second
   */
  protected double _simVeloRPS;
  /**
   * Acceleration of the simulated pivot in rotations per second squared
   */
  protected double _simAccelRPS2;
  /**
   * Simulated arm of the pivot
   */
  protected MechanismLigament2d _pivotLigament;

  protected double _scopeOffset = 0.0;

  /**
   * Constructs a pivot microsystem with the below configurations
   *
   * @param conf Configurations for the pivot
   */
  public Pivot(PivotConf conf) {
    super(conf);
    _pivotConf = conf;

    if (_configuredMicrosystemDisable) {
      _defaultPivotLowerToleranceRot = 0.0;
      _defaultPivotUpperToleranceRot = 0.0;
      _absoluteRangeRot = 0.0;
      _minPositionRot = 0.0;
      _maxPositionRot = 0.0;
      _maxVelocityRPS = 0.0;
      _maxAccelerationRPS2 = 0.0;
      _profileTargetOffset = 0.0;
      _ffCenterOfMassOffsetRad = 0.0;
      return;
    }
    _pivotFF = _pivotConf.feedForward;
    _defaultPivotLowerToleranceRot = _pivotConf.defaultLowerTolerance;
    _defaultPivotUpperToleranceRot = _pivotConf.defaultUpperTolerance;
    // NOTE: This (temporarily) resolves an issue with an absolute encoder that boots out of range
    // TODO: generalize this logic to better handle the CTRE encoder boot location and how it tends
    // to play jump rope with 0 and is seemingly unpredictable
    // Needs to deal with > 360 range and booting where the min AND max can never contain the
    // current position
    double min = _pivotConf.rotationalMin;
    double max = _pivotConf.rotationalMax;

    while (min + _scopeOffset >= getPivotPosition()) {
      _scopeOffset -= 1.0;
    }
    while (max + _scopeOffset <= getPivotPosition()) {
      _scopeOffset += 1.0;
    }
    _minPositionRot = min + _scopeOffset;
    _maxPositionRot = max + _scopeOffset;

    _absoluteRangeRot = _maxPositionRot - _minPositionRot;
    _maxVelocityRPS = _pivotConf.trapezoidalLimitsVelocity;
    _maxAccelerationRPS2 = _pivotConf.trapezoidalLimitsAcceleration;
    _profileTargetOffset = _pivotConf.profileOffsetValue;
    _ffCenterOfMassOffsetRad = _pivotConf.ffOffsetValue;

    _trapProfile = new TrapezoidProfile(_pivotConf.trapezoidalLimits);
    _curState.position = getPivotPosition();

    _motorToEncoderRatio = _pivotConf.motorToEncoderRatio;
    _encoderToPivotRatio = _pivotConf.encoderToPivotRatio;

    double minAbs = AlgebraicUtils.cppMod(_minPositionRot, 1.0);
    double maxAbs = AlgebraicUtils.cppMod(_maxPositionRot, 1.0);
    double halfUnusedRange = (1.0 - _absoluteRangeRot) / 2.0;
    double midUnused = maxAbs + halfUnusedRange;

    if (midUnused > 1.0) {
      _values = new double[] {midUnused - 1.0, minAbs, maxAbs, 1.0};
      _ids = new int[] {2, 0, 1, 2};
    } else if (_minPositionRot > 0.0) {
      _values = new double[] {minAbs, maxAbs, midUnused, 1.0};
      _ids = new int[] {0, 1, 2, 0};
    } else {
      _values = new double[] {maxAbs, midUnused, minAbs, 1.0};
      _ids = new int[] {1, 2, 0, 1};
    }
    if (IterativeRobotBase.isReal()) {
      int count = 0;
      // while (!_primaryMotor.isAlive() && count <= 1000) {
      // System.out.println(_pivotConf.name + " not alive " + (count++));
      // }
      if (count >= 1000) {
        System.out.println(_pivotConf.name + " failed to initialize!");
      }
    }
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
   * Periodic update function
   */
  public void periodic() {
    if (_isMicrosystemDisabled) {
      return;
    } else if (motorResetConfig()) {
      setPivotProfile(getPivotPosition(), 0.0);
      _primaryMotor.set(0.0);
    } else if (_isFFTuningMicro && _trapProfile.isFinished(_profileTimer.get())) {
      _primaryMotor.setControl(_requestedPositionVoltage.withFeedForward(
          _pivotFF.getKs() + _pivotFF.getKg() * Math.cos(getFFPositionRad())
      ));
    }

    if (_followProfile) {
      followLastProfile();
    }
  }

  /**
   * Gets the primary motor of the pivot
   *
   * @return primary motor
   */
  public TalonFX getMotor() {
    return _primaryMotor;
  }

  /**
   * Executes logic for when the robot is first enabled
   */
  @Override
  public void onEnable() {
    if (_isMicrosystemDisabled) {
      return;
    }
    super.onEnable();

    if (_isFFTuningMicro) {
      _pivotFF = new ArmFeedforward(
          _KSEntry.getDouble(_pivotFF.getKs()),
          _KGEntry.getDouble(_pivotFF.getKg()),
          _KVEntry.getDouble(_pivotFF.getKv()),
          _KAEntry.getDouble(_pivotFF.getKa())
      );
      _primaryMotor.setControl(_requestedPositionVoltage.withFeedForward(
          _pivotFF.getKs() + _pivotFF.getKg() * Math.cos(getFFPositionRad())
      ));
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
   * Updates shuffleboard with the pivot's current position and velocity
   */
  @Override
  public void updateShuffleboard() {
    if (_isMicrosystemDisabled) {
      return;
    }
    if (_isShuffleboardMicro) {
      _currentPositionEntry.setDouble(getPivotPosition());
      _currentVelocityEntry.setDouble(getPivotVelocity());
    }
  }

  /**
   * Converts motor rotations to rotations of the pivot mechanism
   *
   * @param motorRot position of the motor in rotations
   * @return position of the pivot in rotations
   */
  public double motorToPivotRot(double motorRot) {
    if (_isMicrosystemDisabled) {
      return 0.0;
    }
    // FUTURE DEV: modify to allow for unfused or not 1:1 with pivot
    return motorRot / (_encoderToPivotRatio * _motorToEncoderRatio);
  }

  /**
   * Converts rotations of the pivot mechanism to motor rotations
   *
   * @param pivotRot position of the pivot in rotations
   * @return position of the motor in rotations
   */
  public double pivotRotToMotor(double pivotRot) {
    if (_isMicrosystemDisabled) {
      return 0.0;
    }
    // FUTURE DEV: modify to allow for unfused or not 1:1 with pivot
    return pivotRot * (_encoderToPivotRatio * _motorToEncoderRatio);
  }

  /**
   * Configure shuffleboard for the pivot
   */
  @Override
  public void configShuffleboard() {
    if (_isMicrosystemDisabled) {
      return;
    }
    super.configShuffleboard();
  }

  /**
   * Calculates the next state according to the trapezoidal profile and requests the pivot motor(s)
   * to arrive at the next position with feedforward
   */
  public void followLastProfile() {
    if (_isMicrosystemDisabled) {
      return;
    }
  }

  /**
   * Gets the position offset for feedforward (to account for a shifted center of
   * mass) in rotations
   *
   * @return offset position in rotations
   */
  public double getFFPositionRad() {
    return 0.0;
  }

  /**
   * Sets the power of the primary motor
   *
   * @param power desired power
   */
  public void setPivotPower(double power) {
    setPrimaryPower(power);
  }

  /**
   * Gets the power of the primary motor
   *
   * @return the current power
   */
  public double getPivotPower() {
    return getPrimaryMotorPower();
  }

  /**
   * Sets the velocity of the pivot in RPS
   *
   * @param rps Desired velocity in rotations per second
   * @param withFF with feedforward
   */
  public void setPivotVelocity(double rps, boolean withFF) {}

  /**
   * Gets the current position of the pivot in rotations
   *
   * @return the position of the primary motor which is the same as the position of the pivot when
   *     they are fused
   */
  public double getPivotPosition() {
    return 0.0;
  }

  /**
   * Gets the current velocity of the pivot in rotations per second
   *
   * @return the velocity of the primary motor which is the same as the velocity of the pivot when
   *     they are fused
   */
  public double getPivotVelocity() {
    return 0.0;
  }

  /**
   * Returns the new pivot angle in the closest scope of the reference pivot angle
   *
   * @param scopeReferenceRot reference angle in rotations
   * @param newAngleRot       angle to be scoped in rotations
   * @return scoped newAngleRot
   */
  public double placePivotInClosestRot(double scopeReferenceRot, double newAngleRot) {
    return AlgebraicUtils.placeInScopeRot(scopeReferenceRot, newAngleRot);
  }

  /**
   * Creates a new trapezoidal profile for the pivot to follow
   *
   * @param goalPosition     goal position in rotations
   */
  public void setPivotProfile(Angle goalPosition) {
    setPivotProfile(goalPosition.getRotations());
  }

  /**
   * Creates a new trapezoidal profile for the pivot to follow
   *
   * @param goalPosition     goal position in rotations
   * @param goalVelocityRPS     goal velocity in rotations per second
   */
  public void setPivotProfile(Angle goalPosition, double goalVelocityRPS) {
    setPivotProfile(goalPosition.getRotations(), goalVelocityRPS);
  }

  /**
   * Creates a new trapezoidal profile for the pivot to follow
   *
   * @param goalPosition     goal position in rotations
   * @param goalVelocityRPS     goal velocity in rotations per second
   * @param maxVelocityRPS      maximum velocity in rotations per second
   */
  public void setPivotProfile(Angle goalPosition, double goalVelocityRPS, double maxVelocityRPS) {
    setPivotProfile(goalPosition.getRotations(), goalVelocityRPS, maxVelocityRPS);
  }

  /**
   * Creates a new trapezoidal profile for the pivot to follow
   *
   * @param goalPosition     goal position in rotations
   * @param goalVelocityRPS     goal velocity in rotations per second
   * @param maxVelocityRPS      maximum velocity in rotations per second
   * @param maxAccelerationRPS2 maximum acceleration in rotations per second squared
   */
  public void setPivotProfile(
      Angle goalPosition, double goalVelocityRPS, double maxVelocityRPS, double maxAccelerationRPS2
  ) {
    setPivotProfile(
        goalPosition.getRotations(), goalVelocityRPS, maxVelocityRPS, maxAccelerationRPS2
    );
  }

  /**
   * Creates a new trapezoidal profile for the pivot to follow
   *
   * @param goalPosition     goal position in rotations
   * @param goalVelocityRPS     goal velocity in rotations per second
   * @param maxVelocityRPS      maximum velocity in rotations per second
   * @param maxAccelerationRPS2 maximum acceleration in rotations per second squared
   * @param setCurrentState     True if the profiles current state should base itself off sensor
   *     values rather than continue from the existing state
   */
  public void setPivotProfile(
      Angle goalPosition,
      double goalVelocityRPS,
      double maxVelocityRPS,
      double maxAccelerationRPS2,
      boolean setCurrentState
  ) {
    setPivotProfile(
        goalPosition.getRotations(),
        goalVelocityRPS,
        maxVelocityRPS,
        maxAccelerationRPS2,
        setCurrentState
    );
  }

  /**
   * Creates a new trapezoidal profile for the pivot to follow
   *
   * @param goalPositionRot goal position in rotations
   */
  public void setPivotProfile(double goalPositionRot) {
    setPivotProfile(goalPositionRot, 0.0);
  }

  /**
   * Creates a new trapezoidal profile for the pivot to follow
   *
   * @param goalPositionRot goal position in rotations
   * @param goalVelocityRPS goal velocity in rotations per second
   */
  public void setPivotProfile(double goalPositionRot, double goalVelocityRPS) {
    setPivotProfile(goalPositionRot, goalVelocityRPS, _maxVelocityRPS);
  }

  /**
   * Creates a new trapezoidal profile for the pivot to follow
   *
   * @param goalPositionRot goal position in rotations
   * @param goalVelocityRPS goal velocity in rotations per second
   * @param maxVelocityRPS  maximum velocity in rotations per second
   */
  public void setPivotProfile(
      double goalPositionRot, double goalVelocityRPS, double maxVelocityRPS
  ) {
    setPivotProfile(goalPositionRot, goalVelocityRPS, maxVelocityRPS, _maxAccelerationRPS2);
  }

  /**
   * Creates a new trapezoidal profile for the pivot to follow
   *
   * @param goalPositionRot     goal position in rotations
   * @param goalVelocityRPS     goal velocity in rotations per second
   * @param maxVelocityRPS      maximum velocity in rotations per second
   * @param maxAccelerationRPS2 maximum acceleration in rotations per second squared
   */
  public void setPivotProfile(
      double goalPositionRot,
      double goalVelocityRPS,
      double maxVelocityRPS,
      double maxAccelerationRPS2
  ) {
    setPivotProfile(goalPositionRot, goalVelocityRPS, maxVelocityRPS, maxAccelerationRPS2, true);
  }

  /**
   * Creates a new trapezoidal profile for the pivot to follow
   *
   * @param goalPositionRot     goal position in rotations
   * @param goalVelocityRPS     goal velocity in rotations per second
   * @param maxVelocityRPS      maximum velocity in rotations per second
   * @param maxAccelerationRPS2 maximum acceleration in rotations per second squared
   * @param setCurrentState     True if the profiles current state should base itself off sensor
   *     values rather than continue from the existing state
   */
  public void setPivotProfile(
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
      _curState.position = getPivotPosition();
      _curState.velocity = getPivotVelocity();
    }

    _goalState.velocity = goalVelocityRPS;
    _goalState.position = clampPivotPosition(goalPositionRot);
    // NOTE + TODO: code removed due to compensating an already compensated value
    // _goalState.position = (_absoluteRangeRot < 1.0 ? absoluteClamp(goalPositionRot +
    // _scopeOffset)
    //                                                : clampPivotPosition(goalPositionRot +
    //                                                _scopeOffset))
    //     + _profileTargetOffset;

    _trapProfile = new TrapezoidProfile(
        (maxVelocityRPS >= _maxVelocityRPS || maxAccelerationRPS2 >= _maxAccelerationRPS2)
            ? _pivotConf.trapezoidalLimits
            : new TrapezoidProfile.Constraints(maxVelocityRPS, maxAccelerationRPS2)
    );

    _profileTimer.restart();
  }

  /**
   * Configures the motor according to specified configuration
   */
  protected void configMotor() {
    for (int i = 0; i < _conf.numMotors; i++) {
      _allMotors[i].getConfigurator().apply(_conf.motorConfig[i]);
    }
  }

  /**
   * Clamps the goal pivot position to be within rotational limits
   *
   * @param target goal position in rotations
   * @return clamped goal position in rotations
   */
  public double clampPivotPosition(double target) {
    return AlgebraicUtils.clamp(target, _minPositionRot, _maxPositionRot);
  }

  /**
   * Resets devices if resets have occurred
   */
  @Override
  public boolean motorResetConfig() {
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

    _pivotSim = new SingleJointedArmSim(
        _pivotConf.motorTypes[0].simSupplier.apply(_pivotConf.numMotors),
        _motorToEncoderRatio * _encoderToPivotRatio,
        _pivotConf.pivotMOI,
        _pivotConf.pivotLengthM,
        Units.rotationsToRadians(_minPositionRot),
        Units.rotationsToRadians(_maxPositionRot),
        true,
        Units.rotationsToRadians(0)
    );

    _mechanism = new Mechanism2d(_pivotConf.mech2dDim, _pivotConf.mech2dDim);
    SmartDashboard.putData("SIM: " + _pivotConf.name, _mechanism);

    _root = _mechanism.getRoot(_pivotConf.name, _pivotConf.rootX, _pivotConf.rootY);
    _pivotLigament = new MechanismLigament2d(_pivotConf.name, _pivotConf.pivotLengthM, 0.0);
    _root.append(_pivotLigament);
    _pivotLigament.setColor(new Color8Bit(255, 255, 255));

    _primaryMotorSim = _primaryMotor.getSimState();
  }

  /**
   * Runs the pivot simulation--sets motor and cancoder simulation fields and updates the visual
   */
  @Override
  public void simulationPeriodic() {}

  /**
   * Checks whether or not it's safe for the pivot to move
   *
   * @return whether or not it's safe to move
   */
  public boolean isSafeToMove() {
    if (_isMicrosystemDisabled) {
      return true;
    }
    return true;
  }

  /**
   * Directs the pivot to its maximum position, current goal position, or minimum position
   *
   * @param value goal position
   * @return redirected goal position
   */
  public double absoluteClamp(double value) {
    double abs = AlgebraicUtils.cppMod(value, 1.0);
    int i = 0;
    while (abs >= _values[i] && i < _values.length) {
      i++;
    }
    switch (_ids[i]) {
      case 2:
        return _maxPositionRot;
      case 1:
        return placePivotInClosestRot(getPivotPosition(), abs);
      case 0:
      default:
        return _minPositionRot;
    }
  }

  /**
   * Bounds checking function that uses the current pivot position
   *
   * @param lowerBound minimum of acceptable range
   * @param upperBound maximum of acceptable range
   * @return if the current position is greater than or equal to the lower bound and less than or
   *     equal to the upper bound
   */
  public boolean isPivotInTolerance(double lowerBound, double upperBound) {
    if (_isMicrosystemDisabled) {
      return true;
    }
    return AlgebraicUtils.inTolerance(getPivotPosition(), lowerBound, upperBound);
  }

  /**
   * Bounds checking function that uses the absolute current pivot position (modulo one rotation)
   *
   * @param lowerBound minimum of acceptable range
   * @param upperBound maximum of acceptable range
   * @return if the absolute current position is in absolute acceptable range
   */
  public boolean isPivotInAbsoluteTolerance(double lowerBound, double upperBound) {
    double position = AlgebraicUtils.cppMod(getPivotPosition(), 1.0);
    lowerBound = AlgebraicUtils.cppMod(lowerBound, 1.0);
    upperBound = AlgebraicUtils.cppMod(upperBound, 1.0);
    return _isMicrosystemDisabled || position >= 0
        ? AlgebraicUtils.inTolerance(position, lowerBound, upperBound)
        : AlgebraicUtils.inTolerance(position + 1.0, lowerBound, upperBound);
  }

  @Override
  public void holdCurrentPosition() {
    setPivotProfile(getPivotPosition(), 0.0);
    setFollowProfile(true);
  }

  /**
   * Retrieves ligament attached to pivot Mechanism2d
   *
   * @return pivot ligament
   */
  public MechanismLigament2d getPivotLigament() {
    return _pivotLigament;
  }

  public double getScopeOffset() {
    return _scopeOffset;
  }
}
