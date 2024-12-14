/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.subsystems.micro.confs;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.wpilibj.IterativeRobotBase;
import tagalong.controls.FeedforwardConstants;
import tagalong.controls.PIDSGVAConstants;
import tagalong.devices.Motors;
import tagalong.units.AccelerationUnits;
import tagalong.units.DistanceUnits;
import tagalong.units.MassUnits;
import tagalong.units.VelocityUnits;

/**
 * Configuration for the elevator microsystem
 */
public class ElevatorConf extends MicrosystemConf {
  /**
   * Units for the positional limit
   */
  public final DistanceUnits positionalLimitUnit = DistanceUnits.METER;
  /**
   * Values for the positional minimum and maximum
   */
  public final double positionalMin, positionalMax;

  /**
   * Unit for the drum diameter (default: meters)
   */
  public final DistanceUnits drumDiameterUnit = DistanceUnits.METER;
  /**
   * Value of the drumDiameter
   */
  public final double drumDiameter;
  /**
   * Value of the drum circumference
   */
  public final double drumCircumference;
  /**
   * Feedforward model for the elevator
   */
  public final ElevatorFeedforward feedForward;

  /* -------- Simulation Specific Control -------- */
  /**
   * Unit of the carriage mass (default: kilograms)
   */
  public final MassUnits carriageMassUnit = MassUnits.KILOGRAMS;
  /**
   * Value of the carriage mass
   */
  public final double carriageMassValue;
  /**
   * Dimension of the mechanical system
   */
  public final int mech2dDim;
  /**
   * Length of the line
   */
  public final int lineLength;
  /**
   * Angle of the elevator
   */
  public final int angle;
  /**
   *
   */
  public static final FeedforwardConstants simFeedForward =
      new FeedforwardConstants(0.0, 0.0, 0.0, 0.0);

  /* --- Command Specific Controls --- */
  /**
   * Power for zeroing the elevator
   */
  public final double elevatorZeroingPower = 0.0;
  /**
   * Positional tolerance in meters for zeroing the elevator
   */
  public final double elevatorZeroingStallToleranceM = 0.0;
  /**
   * Duration of stalling in seconds necessary for the elevator zero command to finish
   */
  public final double elevatorZeroingDurationS = 0.0;

  /**
   *@param name                          name of the subsystem
   * @param motorTypes                    array of motor types used
   * @param motorDeviceIDs                CAN IDs of the motors
   * @param motorCanBus                   CAN buses which the motors are connected to
   * @param motorDirection                motor inversion settings
   * @param motorEnabledBrakeMode         brake mode when motors are enabled
   * @param motorDisabledBrakeMode        brake mode when motors are disabled
   * @param gearRatio                     gear ratios
   * @param positionalLimitsUnit         units for positional limits on elevator
   * @param positionalMin                 positional minimum on elevator
   * @param positionalMax                 positional maximum on elevator
   * @param trapezoidalLengthUnit         units for trapezoidal motion length
   * @param trapezoidalVelocityUnit       units for trapezoidal motion velocityODO
   * @param trapezoidalLimitsVelocity     velocity limits for trapezoidal motion
   * @param trapezoidalAccelerationUnit   units for trapezoidal motion acceleration
   * @param trapezoidalLimitsAcceleration acceleration limits for trapezoidal motion
   * @param defaultTolerancesUnit         default unit of the tolerance values
   * @param defaultLowerTolerance         default lower tolerance
   * @param defaultUpperTolerance         default upper tolerance
   * @param feedForward                   feedforward constants
   * @param simFeedForward                simulated feedforward constants
   * @param currentLimitsConfigs          current limit configurations
   * @param slot0                         PID slot 0 configuration
   * @param slot1                         PID slot 1 configuration
   * @param slot2                         PID slot 2 configuration
   * @param carriageMassUnit              units for the carriage mass
   * @param carriageMassValue             value of the carriage mass
   * @param mech2dDim                     dimensions of mechanical system
   * @param lineLength                    length of the line
   * @param angle                         angle of the elevator
   * @param simSlot0                      simulated PID slot 0 configuration
   * @param simSlot1                      simulated PID slot 1 configuration
   * @param simSlot2                      simulated PID slot 2 configuration
   * @param drumDiameterUnit              units for the drum diameter
   * @param drumDiameter                  diameter of the drum
   *
   */
  public ElevatorConf(
      String name,
      Motors[] motorTypes,
      int[] motorDeviceIDs,
      String[] motorCanBus,
      InvertedValue[] motorDirection,
      NeutralModeValue[] motorEnabledBrakeMode,
      NeutralModeValue[] motorDisabledBrakeMode,
      int[][] gearRatio,
      DistanceUnits positionalLimitsUnit,
      double positionalMin,
      double positionalMax,
      DistanceUnits trapezoidalLengthUnit,
      VelocityUnits trapezoidalVelocityUnit,
      double trapezoidalLimitsVelocity,
      AccelerationUnits trapezoidalAccelerationUnit,
      double trapezoidalLimitsAcceleration,
      DistanceUnits defaultTolerancesUnit,
      double defaultLowerTolerance,
      double defaultUpperTolerance,
      FeedforwardConstants feedForward,
      FeedforwardConstants simFeedForward,
      CurrentLimitsConfigs currentLimitsConfigs,
      PIDSGVAConstants slot0,
      PIDSGVAConstants slot1,
      PIDSGVAConstants slot2,
      MassUnits carriageMassUnit,
      double carriageMassValue,
      int mech2dDim,
      int lineLength,
      int angle,
      PIDSGVAConstants simSlot0,
      PIDSGVAConstants simSlot1,
      PIDSGVAConstants simSlot2,
      DistanceUnits drumDiameterUnit,
      double drumDiameter
  ) {
    super(
        name,
        motorTypes,
        motorDeviceIDs,
        motorCanBus,
        motorDirection,
        motorEnabledBrakeMode,
        motorDisabledBrakeMode,
        gearRatio,
        trapezoidalLengthUnit,
        VelocityUnits.METERS_PER_SECOND,
        trapezoidalVelocityUnit.convertX(
            trapezoidalLimitsVelocity, VelocityUnits.METERS_PER_SECOND
        ),
        AccelerationUnits.METERS_PER_SECOND2,
        trapezoidalAccelerationUnit.convertX(
            trapezoidalLimitsAcceleration, AccelerationUnits.METERS_PER_SECOND2
        ),
        DistanceUnits.METER,
        defaultTolerancesUnit.convertX(defaultLowerTolerance, DistanceUnits.METER),
        defaultTolerancesUnit.convertX(defaultUpperTolerance, DistanceUnits.METER),
        currentLimitsConfigs,
        slot0,
        slot1,
        slot2,
        simSlot0,
        simSlot1,
        simSlot2
    );

    this.positionalMin = positionalLimitsUnit.convertX(positionalMin, this.positionalLimitUnit);
    this.positionalMax = positionalLimitsUnit.convertX(positionalMax, this.positionalLimitUnit);

    this.drumDiameter = drumDiameterUnit.convertX(drumDiameter, this.drumDiameterUnit);
    drumCircumference = Math.PI * this.drumDiameter;

    this.carriageMassValue = carriageMassUnit.convertX(carriageMassValue, this.carriageMassUnit);
    this.mech2dDim = mech2dDim;
    this.lineLength = lineLength;
    this.angle = angle;

    this.feedForward = IterativeRobotBase.isReal() ? feedForward.getElevatorFeedforward()
                                                   : simFeedForward.getElevatorFeedforward();
  }
}
