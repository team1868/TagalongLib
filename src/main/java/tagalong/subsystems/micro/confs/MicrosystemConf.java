/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.subsystems.micro.confs;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.IterativeRobotBase;
import tagalong.controls.PIDSGVAConstants;
import tagalong.devices.Motors;
import tagalong.units.AccelerationUnits;
import tagalong.units.DistanceUnits;
import tagalong.units.VelocityUnits;

/**
 * Configuration for a microsystem
 */
public class MicrosystemConf {
  // Generalized to every motorized system
  // ff configurations
  // primary motor full configurations
  // follower motor type, ids, canbus, and direction
  // ---- otherwise assume all control is the same as primary
  // mechanism gear ratio
  // trapezoidal limits
  // default tolerances
  // sim motor configs

  /**
   * Full microsystem name, default is subsystem name followed by microsystem name
   */
  public final String name;

  /**
   * Number of motors used by microsystem, also the length of the following arrays
   */
  public final int numMotors;
  /**
   * The type of each motor in the microsystem
   */
  public final Motors[] motorTypes;
  /**
   * The can ids of the motors
   */
  public final int[] motorDeviceIDs;
  /**
   * The can bus of the motors
   */
  public final String[] motorCanBus;
  /**
   * The motor directionality
   */
  public final InvertedValue[] motorDirection;
  /**
   * Motor brake mode while robot is enabled
   */
  public final NeutralModeValue[] motorEnabledBrakeMode;
  /**
   * Motor brake mode while robot is disabled
   */
  public final NeutralModeValue[] motorDisabledBrakeMode;
  /**
   * Net gear ratio of they system motor to mechanism
   */
  public final double motorToMechRatio;

  @SuppressWarnings("unused")
  /**
   * Unit of trapezoidal motion length
   */
  private final DistanceUnits trapezoidalLengthUnit;
  /**
   * Unit of trapezoidal motion velocity
   */
  public final VelocityUnits trapezoidalVelocityUnit;
  /**
   * Unit of trapezoidal motion acceleration
   */
  public final AccelerationUnits trapezoidalAccelerationUnit;
  /**
   * Trapezoidal motion velocity limits
   */
  public final double trapezoidalLimitsVelocity;
  /**
   * Trapezoidal motion acceleration limits
   */
  public final double trapezoidalLimitsAcceleration;
  /**
   * Trapezoidal constraints
   */
  public final TrapezoidProfile.Constraints trapezoidalLimits;
  /**
   * Unit of default tolerances
   */
  public final DistanceUnits defaultTolerancesUnit;
  /**
   * Default lower tolerance
   */
  public final double defaultLowerTolerance;
  /**
   * Default upper tolerance
   */
  public final double defaultUpperTolerance;
  /**
   * Motor configurations
   */
  public final TalonFXConfiguration[] motorConfig;

  /**
   *
   * @param name                          microsystem name
   * @param motorTypes                    motor types
   * @param motorDeviceIDs                motor device IDs
   * @param motorCanBus                   motor can bus
   * @param motorDirection                motor directions
   * @param motorEnabledBrakeMode         enabled brake mode for the motor
   * @param motorDisabledBrakeMode        disabled brake mode for the motor
   * @param gearRatio                     gear ratio
   * @param trapezoidalLengthUnit         unit for trapezoidal length
   * @param trapezoidalVelocityUnit       unit for trapezoidal velocity
   * @param trapezoidalLimitsVelocity     trapezoidal velocity limits
   * @param trapezoidalAccelerationUnit   unit for trapezoidal acceleration
   * @param trapezoidalLimitsAcceleration trapezoidal acceleration limits
   * @param defaultTolerancesUnit         unit for default tolerances
   * @param defaultLowerTolerance         default lower tolerance
   * @param defaultUpperTolerance         default upper tolerance
   * @param currentLimitsConfigs          configurations for current limits
   * @param slot0                         slot 0 configurations
   * @param slot1                         slot 1 configurations
   * @param slot2                         slot 2 configurations
   * @param simSlot0                      simulation slot 0 configurations
   * @param simSlot1                      simulation slot 1 configurations
   * @param simSlot2                      simulation slot 2 configurations
   */
  public MicrosystemConf(
      String name,
      Motors[] motorTypes,
      int[] motorDeviceIDs,
      String[] motorCanBus,
      InvertedValue[] motorDirection,
      NeutralModeValue[] motorEnabledBrakeMode,
      NeutralModeValue[] motorDisabledBrakeMode,
      int[][] gearRatio,
      DistanceUnits trapezoidalLengthUnit,
      VelocityUnits trapezoidalVelocityUnit,
      double trapezoidalLimitsVelocity,
      AccelerationUnits trapezoidalAccelerationUnit,
      double trapezoidalLimitsAcceleration,
      DistanceUnits defaultTolerancesUnit,
      double defaultLowerTolerance,
      double defaultUpperTolerance,
      CurrentLimitsConfigs currentLimitsConfigs,
      PIDSGVAConstants slot0,
      PIDSGVAConstants slot1,
      PIDSGVAConstants slot2,
      PIDSGVAConstants simSlot0,
      PIDSGVAConstants simSlot1,
      PIDSGVAConstants simSlot2
  ) {
    this.name = name;

    this.motorTypes = motorTypes;
    this.motorDeviceIDs = motorDeviceIDs;
    this.motorCanBus = motorCanBus;
    this.motorDirection = motorDirection;
    this.motorEnabledBrakeMode = motorEnabledBrakeMode;
    this.motorDisabledBrakeMode = motorDisabledBrakeMode;

    numMotors = motorTypes.length;
    assert (numMotors == motorDeviceIDs.length);
    assert (numMotors == motorCanBus.length);
    assert (numMotors == motorDirection.length);
    assert (numMotors == motorEnabledBrakeMode.length);
    assert (numMotors == motorDisabledBrakeMode.length);

    this.motorToMechRatio = calculateGearRatio(gearRatio);
    assert (this.motorToMechRatio > 0.0);

    motorConfig = new TalonFXConfiguration[numMotors];
    for (int i = 0; i < numMotors; i++) {
      motorConfig[i] = Motors.getDefaults();
      motorConfig[i].MotorOutput.Inverted = motorDirection[i];
      motorConfig[i].MotorOutput.NeutralMode = motorDisabledBrakeMode[i];
      motorConfig[i].Slot0 = IterativeRobotBase.isReal() ? slot0.toCTRESlot0Configuration()
                                                         : simSlot0.toCTRESlot0Configuration();
      motorConfig[i].Slot1 = IterativeRobotBase.isReal() ? slot1.toCTRESlot1Configuration()
                                                         : simSlot1.toCTRESlot1Configuration();
      motorConfig[i].Slot2 = IterativeRobotBase.isReal() ? slot2.toCTRESlot2Configuration()
                                                         : simSlot2.toCTRESlot2Configuration();
      motorConfig[i].CurrentLimits = currentLimitsConfigs;
    }

    this.trapezoidalLengthUnit = trapezoidalLengthUnit;
    this.trapezoidalVelocityUnit = trapezoidalVelocityUnit;
    this.trapezoidalAccelerationUnit = trapezoidalAccelerationUnit;
    this.trapezoidalLimitsVelocity = trapezoidalLimitsVelocity;
    this.trapezoidalLimitsAcceleration = trapezoidalLimitsAcceleration;
    this.trapezoidalLimits =
        new TrapezoidProfile.Constraints(trapezoidalLimitsVelocity, trapezoidalLimitsAcceleration);

    this.defaultTolerancesUnit = defaultTolerancesUnit;
    this.defaultLowerTolerance = defaultLowerTolerance;
    this.defaultUpperTolerance = defaultUpperTolerance;
  }

  /**
   * @param gearPairs Pairs of gear teeth representing the physical path to the motor. e.g if the
   *     motor is using a 14-tooth pinion to a 60-tooth gear, the input should be [[14, 60]]
   *
   * @return The resulting gear ratio coefficient.
   *     A reduction (target spinning slower than motor) results in a value g.t. 1
   *     An up-duction (target spinning faster than motor) will result in a value l.t. 1
   */
  public static double calculateGearRatio(int[][] gearPairs) {
    double ratio = 1.0;
    for (int i = 0; i < gearPairs.length; i++) {
      assert (gearPairs[i].length == 2);
      ratio = ratio * gearPairs[i][1] / gearPairs[i][0];
    }
    return ratio;
  }
}
