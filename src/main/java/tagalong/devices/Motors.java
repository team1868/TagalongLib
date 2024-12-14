/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.devices;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.IterativeRobotBase;
import java.util.function.Function;
import tagalong.units.VelocityUnits;

/**
 * Generic motor identifier enum
 */
public enum Motors implements CanDeviceInterface {
  /**
   * Kraken X60
   */
  KRAKEN_X60(6000, DCMotor::getKrakenX60),
  /**
   * Kraken X60 in FOC mode
   */
  KRAKEN_X60_FOC(5800, DCMotor::getKrakenX60Foc),
  /**
   * Falcon 500
   */
  FALCON500(6380, DCMotor::getFalcon500),
  /**
   * Falcon 500 in FOC mode
   */
  FALCON500_FOC(6080, DCMotor::getFalcon500),
  /**
   * Kraken X44
   * FUTURE DEV: Waiting for motor specifications v2025
   */
  KRAKEN_X44(1, DCMotor::getKrakenX60),
  /**
   * Kraken X44 in FOC mode -- RPM WAITING FOR SPEC
   * FUTURE DEV: Waiting for motor specifications v2025
   */
  KRAKEN_X44_FOC(1, DCMotor::getKrakenX60Foc);

  /**
   * Max free speed RPM and converted RPS of the motor
   */
  public final double maxRPM, maxRPS;
  /**
   * Motor's simulation supplier
   */
  public final Function<Integer, DCMotor> simSupplier;

  /**
   *
   * @param maxRPM
   */
  Motors(double maxRPM, Function<Integer, DCMotor> simSupplier) {
    this.maxRPM = maxRPM;
    maxRPS =
        VelocityUnits.ROTATIONS_PER_MINUTE.convertX(maxRPM, VelocityUnits.ROTATIONS_PER_SECOND);
    this.simSupplier = simSupplier;
  }

  // FUTURE DEV: Make some defaults, especially current limits, specific to each motor
  /**
   * Default Stator and Supply current limits. We highly recommend that all
   * Tagalong users configure these limits to the specific application
   */
  public static final double DEFAULT_STATOR_CURRENT_LIMIT_AMPS = 40.0,
                             DEFAULT_SUPPLY_CURRENT_LIMIT_AMPS = 40.0,
                             DEFAULT_SUPPLY_CURRENT_THRESHOLD_LIMIT_AMPS = 45.0,
                             DEFAULT_SUPPLY_TIME_THRESHOLD = 0.1;

  /**
   *
   * @return TalonFXConfiguration with Tagalong defaults
   */
  public static TalonFXConfiguration getDefaults() {
    final TalonFXConfiguration config = new TalonFXConfiguration();

    /* -------- Configurations that match CTRE Default configs -------- */
    config.MotorOutput.NeutralMode = NeutralModeValue.Coast;
    config.MotorOutput.Inverted = InvertedValue.CounterClockwise_Positive;
    config.MotorOutput.DutyCycleNeutralDeadband = 0.0;

    config.SoftwareLimitSwitch.ForwardSoftLimitEnable = false;
    // config.SoftwareLimitSwitch.ForwardSoftLimitThreshold = 0;
    config.SoftwareLimitSwitch.ReverseSoftLimitEnable = false;
    // config.SoftwareLimitSwitch.ReverseSoftLimitThreshold = 0;

    config.Voltage.SupplyVoltageTimeConstant = 0.0;
    config.Voltage.PeakForwardVoltage = 16.0;
    config.Voltage.PeakReverseVoltage = -16.0;

    config.MotionMagic.MotionMagicCruiseVelocity = 0.0; // RPS
    config.MotionMagic.MotionMagicAcceleration = 0.0; // RPS2
    config.MotionMagic.MotionMagicJerk = 0.0; // RPS3

    /* -------- 1868 Safe defaults that DO NOT match CTRE defaults -------- */
    if (IterativeRobotBase.isReal()) {
      // FUTURE DEV: Figure out why stator current limits break simulation.
      config.CurrentLimits.StatorCurrentLimitEnable = true;
      config.CurrentLimits.StatorCurrentLimit = DEFAULT_STATOR_CURRENT_LIMIT_AMPS;
    }

    config.CurrentLimits.SupplyCurrentLimitEnable = true;
    config.CurrentLimits.SupplyCurrentLimit = DEFAULT_SUPPLY_CURRENT_LIMIT_AMPS;
    config.CurrentLimits.SupplyCurrentThreshold = DEFAULT_SUPPLY_CURRENT_THRESHOLD_LIMIT_AMPS;
    config.CurrentLimits.SupplyTimeThreshold = DEFAULT_SUPPLY_TIME_THRESHOLD; // seconds

    config.TorqueCurrent.PeakForwardTorqueCurrent = DEFAULT_STATOR_CURRENT_LIMIT_AMPS;
    config.TorqueCurrent.PeakReverseTorqueCurrent = -DEFAULT_STATOR_CURRENT_LIMIT_AMPS;
    config.TorqueCurrent.TorqueNeutralDeadband = 0.0;

    return config;
  }
}
