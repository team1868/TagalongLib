/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.subsystems.micro;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.Slot1Configs;
import com.ctre.phoenix6.configs.Slot2Configs;
import com.ctre.phoenix6.controls.PositionVoltage;
import com.ctre.phoenix6.controls.StrictFollower;
import com.ctre.phoenix6.controls.TorqueCurrentFOC;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.sim.TalonFXSimState;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.networktables.GenericPublisher;
import edu.wpi.first.networktables.GenericSubscriber;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobotBase;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import tagalong.TagalongConfiguration;
import tagalong.controls.PIDSGVAConstants;
import tagalong.subsystems.micro.confs.MicrosystemConf;

/**
 * Base class for all Tagalong Microsystems
 */
public class Microsystem {
  /**
   * Configuration common to all microsystems
   */
  public final MicrosystemConf _conf;

  /**
   * True if the microsystem is configured to be disabled by a null in the conf
   * files
   */
  public final boolean _configuredMicrosystemDisable;

  /**
   * Disablement state, for dynamic disablement caused by hardware disconnects or
   * on the fly disablement by robot code
   */
  protected boolean _isMicrosystemDisabled = true;

  /**
   * Microsystem variable for tiling the shuffleboard entries
   */
  public static int _tuningTabCounter = 0;

  /**
   * True if the microsystem is configured for shuffleboard based logging, also
   * true if in PID or FF
   * tuning modes
   */
  protected boolean _isShuffleboardMicro = false;

  /**
   * True if the microsystem is configured for shuffleboard based PID tuning
   */
  protected boolean _isPIDTuningMicro = false;

  /**
   * True if the microsystem is configured for shuffleboard based FeedForward
   * tuning
   */
  protected boolean _isFFTuningMicro = false;

  /* --- Shuffleboard Entries --- */
  /**
   * PID tuning entries that read PID from shuffleboard
   */
  protected GenericSubscriber _slot0PFactorEntry, _slot0IFactorEntry, _slot0DFactorEntry,
      _slot1PFactorEntry, _slot1IFactorEntry, _slot1DFactorEntry, _slot2PFactorEntry,
      _slot2IFactorEntry, _slot2DFactorEntry;
  /**
   * FeedForward tuning entries that read FeedForward constants from shuffleboard
   */
  protected GenericSubscriber _KSEntry, _KGEntry, _KVEntry, _KAEntry;
  /**
   * Logging entries that write robot status values to shuffleboard
   */
  protected GenericPublisher _targetPositionEntry, _targetVelocityEntry, _currentPositionEntry,
      _currentVelocityEntry;

  /* -------- Hardware: motors and sensors -------- */
  /**
   * The primary motor for the microsystem, all others will follow this motor
   */
  protected TalonFX _primaryMotor;
  /**
   * Array of all microsystem motors, the first in the array
   */
  protected TalonFX[] _allMotors;

  /* -------- Control: controllers and utilities -------- */
  /**
   * Shared motor positional voltage request, we put positional PIDSGVA constants
   * into slot 0
   */
  protected PositionVoltage _requestedPositionVoltage = new PositionVoltage(0.0).withSlot(0);
  /**
   * Shared motor velocity voltage request, we put velocity PIDSGVA constants into
   * slot 1
   */
  protected VelocityVoltage _requestedVelocityVoltage = new VelocityVoltage(0.0).withSlot(1);
  /**
   * Shared motor torque current request
   */
  protected TorqueCurrentFOC _requestedTorqueCurrent = new TorqueCurrentFOC(0.0);
  /**
   * True if the microsystem should follow the a profile during each periodic loop
   */
  protected boolean _followProfile = false;

  /* -------- Control: states and constants -------- */
  /**
   * Trapezoidal profile follower helper states that track current and goal state
   */
  protected TrapezoidProfile.State _curState = new TrapezoidProfile.State(),
                                   _goalState = new TrapezoidProfile.State();
  /**
   * Trapezoidal profile currently being followed
   */
  protected TrapezoidProfile _trapProfile;
  /**
   * Timer used for trapezoidal state timing and tracking
   */
  protected Timer _profileTimer = new Timer();
  /**
   * Timer used for tracking how long a system is in tolerance
   */
  protected Timer _toleranceTimer = new Timer();

  /* ------ SIM ------ */
  /**
   * Simulation for the primary motor
   */
  protected TalonFXSimState _primaryMotorSim;
  /**
   * The root of the simulation animation
   */
  protected MechanismRoot2d _root;
  /**
   * Visual representation of the elevator
   */
  protected Mechanism2d _mechanism;

  // null parser is a configured disablement
  /**
   * Constructs a microsystem with the below configurations
   *
   * @param conf Configuration for the microsystem
   */
  public Microsystem(MicrosystemConf conf) {
    _configuredMicrosystemDisable = conf == null;
    // In the future we need a more discrete function configuring this
    _isMicrosystemDisabled = _configuredMicrosystemDisable;
    _conf = conf;
    configTuningModes();

    if (_configuredMicrosystemDisable) {
      _allMotors = new TalonFX[0];
      _primaryMotor = null;
      return;
    }

    // Initialize motors
    _allMotors = new TalonFX[conf.numMotors];
    for (int i = 0; i < conf.numMotors; i++) {
      _allMotors[i] = new TalonFX(conf.motorDeviceIDs[i], conf.motorCanBus[i]);
    }
    _primaryMotor = _allMotors[0];
    // FUTURE DEV: Inject this here rather than robot builder
    // configShuffleboard();
    waitForInitialization();
  }

  /**
   * Configures devices
   */
  public void configAllDevices() {
    for (int i = 0; i < _conf.numMotors; i++) {
      _allMotors[i].getConfigurator().apply(_conf.motorConfig[i]);
    }

    for (int i = 1; i < _conf.numMotors; i++) {
      _allMotors[i].setControl(new StrictFollower(_primaryMotor.getDeviceID()));
    }
    for (int i = 0; i < _conf.numMotors; i++) {
      _allMotors[i].setNeutralMode(
          DriverStation.isDisabled() ? _conf.motorDisabledBrakeMode[i]
                                     : _conf.motorEnabledBrakeMode[i]
      );
    }
  }

  /**
   * Sets all motors on enable brake mode
   *
   * @param enabled whether or not the robot is enabled
   */
  public void setBrakeMode(boolean enabled) {
    for (int i = 0; i < _conf.numMotors; i++) {
      _allMotors[i].setNeutralMode(
          enabled ? _conf.motorEnabledBrakeMode[i] : _conf.motorDisabledBrakeMode[i]
      );
    }
  }

  /**
   *
   */
  public void updateAllPIDSGVA() {
    // update all 3 slots from shuffleboard
    double s0p = _conf.motorConfig[0].Slot0.kP;
    double s0i = _conf.motorConfig[0].Slot0.kI;
    double s0d = _conf.motorConfig[0].Slot0.kD;
    double s1p = _conf.motorConfig[0].Slot1.kP;
    double s1i = _conf.motorConfig[0].Slot1.kI;
    double s1d = _conf.motorConfig[0].Slot1.kD;
    double s2p = _conf.motorConfig[0].Slot2.kP;
    double s2i = _conf.motorConfig[0].Slot2.kI;
    double s2d = _conf.motorConfig[0].Slot2.kD;

    if (_isPIDTuningMicro) {
      s0p = _slot0PFactorEntry.getDouble(s0p);
      s0i = _slot0IFactorEntry.getDouble(s0i);
      s0d = _slot0DFactorEntry.getDouble(s0d);
      s1p = _slot1PFactorEntry.getDouble(s1p);
      s1i = _slot1IFactorEntry.getDouble(s1i);
      s1d = _slot1DFactorEntry.getDouble(s1d);
      s2p = _slot2PFactorEntry.getDouble(s2p);
      s2i = _slot2IFactorEntry.getDouble(s2i);
      s2d = _slot2DFactorEntry.getDouble(s2d);
    }

    Slot0Configs s0 =
        new PIDSGVAConstants(s0p, s0i, s0d, 0.0, 0.0, 0.0, 0.0).toCTRESlot0Configuration();
    Slot1Configs s1 =
        new PIDSGVAConstants(s1p, s1i, s1d, 0.0, 0.0, 0.0, 0.0).toCTRESlot1Configuration();
    Slot2Configs s2 =
        new PIDSGVAConstants(s2p, s2i, s2d, 0.0, 0.0, 0.0, 0.0).toCTRESlot2Configuration();

    for (int i = 0; i < _conf.numMotors; i++) {
      // FUTURE DEV: add SGVA to shuffleboard
      _conf.motorConfig[i].Slot0 = s0;
      _conf.motorConfig[i].Slot1 = s1;
      _conf.motorConfig[i].Slot2 = s2;

      var configurator = _allMotors[i].getConfigurator();
      configurator.apply(_conf.motorConfig[i].Slot0);
      configurator.apply(_conf.motorConfig[i].Slot1);
      configurator.apply(_conf.motorConfig[i].Slot2);
    }
  }

  /**
   * Sets break mode for all motors
   * If system is in PID tuning mode is updates all PID related settings
   * If the system is in Feedforward Tuning mode, it sets the primary motor to
   * coast mode.
   */
  public void onEnable() {
    if (_isMicrosystemDisabled) {
      return;
    }

    setBrakeMode(true);

    if (_isPIDTuningMicro) {
      updateAllPIDSGVA();
    }
  }

  /**
   * Checks initialize status
   */
  protected void waitForInitialization() {
    if (IterativeRobotBase.isReal()) {
      int counter = 0;
      // FUTURE DEV: Make the counter threshold configurable
      while (!checkInitStatus() && counter <= 1000) {
        System.out.println(_conf.name + " Check Init Status : " + counter++);
      }
      if (counter >= 1000) {
        System.out.println(_conf.name + " failed to initialize!");
      }
    }
  }

  /**
   * checks to see if micro system is enabled and primary motor is alive
   *
   * @return boolean
   */
  public boolean checkInitStatus() {
    return !_isMicrosystemDisabled && _primaryMotor.isAlive();
  }

  /**
   * @return _primaryMotor(if system is enabled)
   */
  public TalonFX getPrimaryMotor() {
    if (_isMicrosystemDisabled) {
      return new TalonFX(0);
    }
    return _primaryMotor;
  }

  /**
   * @return position of the primary motor, 0.0 if system is disabled
   */
  public double getPrimaryMotorPosition() {
    if (_isMicrosystemDisabled) {
      return 0.0;
    }
    return _primaryMotor.getPosition().getValueAsDouble();
  }

  /**
   * @return velocity of the primary motor, 0.0 if system is disabled
   */
  public double getPrimaryMotorVelocity() {
    if (_isMicrosystemDisabled) {
      return 0.0;
    }
    return _primaryMotor.getVelocity().getValueAsDouble();
  }

  /**
   * exits method if micro system is disabled, if enabled sets primary motor to
   * specified value
   *
   * @param power desired power
   */
  public void setPrimaryPower(double power) {
    if (_isMicrosystemDisabled) {
      return;
    }
    _primaryMotor.set(power);
  }

  /**
   * @return double(0.0 if micro system is disabled or actual power of primary
   *         motor)
   */
  public double getPrimaryMotorPower() {
    return _isMicrosystemDisabled ? 0.0 : _primaryMotor.get();
  }

  /**
   * configures the tuning states of the micro system based on whether it is
   * selected for tuning,
   * and whether it is in PID or feedforward tuning mode
   */
  public void configTuningModes() {
    _isPIDTuningMicro = TagalongConfiguration.pidTuningMicrosystems.contains(_conf.name);
    _isFFTuningMicro = TagalongConfiguration.ffTuningMicrosystems.contains(_conf.name);
    _isShuffleboardMicro = _isFFTuningMicro || _isPIDTuningMicro
        || TagalongConfiguration.shuffleboardMicrosystems.contains(_conf.name);
  }

  /**
   * Configures a user interface on the Shuffleboard for the specified micro
   * system
   */
  public void configShuffleboard() {
    String name = _conf.name;
    ShuffleboardTab tuningTab = Shuffleboard.getTab("Tuning tab");
    ShuffleboardLayout microLayout = tuningTab.getLayout(name, BuiltInLayouts.kGrid)
                                         .withSize(3, 4)
                                         .withPosition(2 * _tuningTabCounter++, 0);
    if (_isShuffleboardMicro) {
      _currentPositionEntry =
          microLayout.add(name + " Current Position", 0.0).withPosition(0, 0).getEntry();
      _targetPositionEntry =
          microLayout.add(name + " Target Position", 0.0).withPosition(0, 1).getEntry();
      _currentVelocityEntry =
          microLayout.add(name + " Current Velocity", 0.0).withPosition(0, 2).getEntry();
      _targetVelocityEntry =
          microLayout.add(name + " Target Velocity", 0.0).withPosition(0, 3).getEntry();
    }

    if (_isPIDTuningMicro) {
      _slot0PFactorEntry = microLayout.add(name + " P Fac", 0.0).withPosition(1, 0).getEntry();
      _slot0IFactorEntry = microLayout.add(name + " I Fac", 0.0).withPosition(1, 1).getEntry();
      _slot0DFactorEntry = microLayout.add(name + " D Fac", 0.0).withPosition(1, 2).getEntry();
    }

    if (_isFFTuningMicro) {
      _KSEntry = microLayout.add(name + " kS", 0.0).withPosition(2, 0).getEntry();
      _KVEntry = microLayout.add(name + " kV", 0.0).withPosition(2, 1).getEntry();
      _KAEntry = microLayout.add(name + " kA", 0.0).withPosition(2, 2).getEntry();
    }
  }

  /**
   * returns nothing if micro system is disabled
   */
  public void onDisable() {
    if (_isMicrosystemDisabled) {
      return;
    }

    setBrakeMode(false);
  }

  /**
   * returns nothing if micro system is disabled else it sets
   * followProfile to false which stops any movement
   */
  public void onTeleopDisable() {
    if (_isMicrosystemDisabled)
      return;
    setFollowProfile(false);
  }

  /**
   * control whether the robot or subsystem should follow a predefined motion path
   *
   * @param followProfile whether or not to follow the trapezoidal profile
   */
  public void setFollowProfile(boolean followProfile) {
    if (!(_isMicrosystemDisabled)) {
      _followProfile = followProfile;
    }
  }

  /**
   * alias for setFollowProfile
   *
   * @param holdPosition whether or not to hold position
   */
  public void setHoldPosition(boolean holdPosition) {
    setFollowProfile(holdPosition);
  }

  /**
   *
   * @return boolean (micro system is enabled and the primary motor has been reset
   *         and if
   *         successful, it reconfigures the devices and returns true)
   */
  public boolean motorResetConfig() {
    if (_isMicrosystemDisabled) {
      return false;
    }
    if (_primaryMotor.hasResetOccurred()) {
      configAllDevices();
      return true;
    }

    return false;
  }

  /**
   * Checks if the trapezoidal profile has reached its goal
   *
   * @return whether or not the profile has finished
   */
  public boolean isProfileFinished() {
    return _isMicrosystemDisabled || _trapProfile.isFinished(_profileTimer.get());
  }

  /**
   * Sets microsystem disablement
   *
   * @param disable whether or not the microsystem should be disabled
   */
  public void disableMicrosystem(boolean disable) {
    _isMicrosystemDisabled = _configuredMicrosystemDisable || disable;
  }

  /**
   * Sets the power of the primary motor to zero
   */
  public void holdCurrentPosition() {
    _primaryMotor.set(0.0);
  }

  /* -------- IO and config functions -------- */
  /**
   * Updates shuffleboard
   */
  public void updateShuffleboard() {
    if (_isMicrosystemDisabled) {
      return;
    }
  }

  /* -------- CORE INTERFACE: THESE MUST BE ADDED TO THE SUBSYSTEM -------- */
  /**
   * Periodic update function
   */
  public void periodic() {
    if (_isMicrosystemDisabled) {
      return;
    }
    updateShuffleboard();
  }

  /**
   * Initializes simulation
   */
  public void simulationInit() {
    if (_isMicrosystemDisabled) {
      return;
    }
  }

  /**
   * Periodic function during simulation
   */
  public void simulationPeriodic() {
    if (_isMicrosystemDisabled) {
      return;
    }
  }

  /**
   * Resets the tolerance timer, must be ran as part of command initialization
   */
  public void resetToleranceTimer() {
    _toleranceTimer.reset();
  }

  /**
   * Ran each time the isFinished command is run when in tolerance duration is
   * required. Resets or starts the timer accordingly and evaluates if required
   * duration has been met
   *
   * @param inTolerance       True if the microsystem is currently in tolerance
   * @param requiredDurationS Require in tolerance duration in seconds
   * @return True if the microsystem has been in tolerance the required duration
   */
  public boolean checkToleranceTime(boolean inTolerance, double requiredDurationS) {
    if (inTolerance) {
      _toleranceTimer.start();
      return _toleranceTimer.hasElapsed(requiredDurationS);
    } else {
      resetToleranceTimer();
      return false;
    }
  }

  /**
   *
   * @return sim root
   */
  public MechanismRoot2d getRoot() {
    return _root;
  }
}
