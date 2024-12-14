/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.subsystems.micro.confs;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.SensorDirectionValue;
import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.wpilibj.IterativeRobotBase;
import tagalong.controls.FeedforwardConstants;
import tagalong.controls.PIDSGVAConstants;
import tagalong.devices.Encoders;
import tagalong.devices.Motors;
import tagalong.units.AccelerationUnits;
import tagalong.units.DistanceUnits;
import tagalong.units.VelocityUnits;

/**
 * Configuration for the pivot microsystem
 */
public class PivotConf extends MicrosystemConf {
  /**
   * Unit for rotational limit (default: rotations)
   */
  public final DistanceUnits rotationalLimitUnit = DistanceUnits.ROTATION;
  /**
   * Minimum and maximum rotation
   */
  public final double rotationalMin, rotationalMax;
  /**
   * Feedforward model for the arm
   */
  public final ArmFeedforward feedForward;

  /**
   * Encoder type used for the pivot
   */
  public final Encoders encoderType;
  /**
   * Device ID of the encoder
   */
  public final int encoderDeviceID;
  /**
   * CAN bus of the encoder
   */
  public final String encoderCanBus;
  /**
   * Ratio between the motor and encoder
   */
  public double motorToEncoderRatio;
  /**
   * Gear ratio between encoder and pivot
   */
  public double encoderToPivotRatio;
  /**
   * Encoder configuration
   */
  public final CANcoderConfiguration encoderConfig;

  /**
   * Whether the encoder configuration operates in a zero to one range
   */
  public final boolean encoderConfigZeroToOne;
  /**
   * Whether the encoder is configured as clockwise positive
   */
  public final boolean encoderConfigClockwisePositive;
  /**
   * Unit of the encoder magnet offset
   */
  public final DistanceUnits encoderConfigMagnetOffsetUnit = DistanceUnits.ROTATION;
  /**
   * Value of the encoder magnet offset
   */
  public final double encoderConfigMagnetOffsetValue;

  /**
   * Whether there is continuous wrapping in closed loop configurations
   */
  public final boolean closedLoopConfigsContinuousWrap;

  /**
   * Unit of the feedforward offset
   */
  public final DistanceUnits ffOffsetUnit = DistanceUnits.RADIAN;
  /**
   * Value of the feedforward offset
   */
  public final double ffOffsetValue;

  /**
   * Unit of the profile offset
   */
  public final DistanceUnits profileOffsetUnit = DistanceUnits.ROTATION;
  /**
   * Value of the profile offset
   */
  public final double profileOffsetValue;

  /* -------- Simulation Specific Control -------- */
  /**
   * Moment of inertia for the pivot
   */
  public final double pivotMOI;
  /**
   * Length of the pivot in meters
   */
  public final double pivotLengthM;
  /**
   * Control constants
   */
  // public static final FeedforwardConstants simFeedForward =
  // new FeedforwardConstants(0.0, 0.0, 0.0, 0.0);
  // /* -------- rotational -------- */
  // public static final PIDSGVAConstants simSlot0 =
  // new PIDSGVAConstants(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
  // /* -------- Velocity -------- */
  // public static final PIDSGVAConstants simSlot1 =
  // new PIDSGVAConstants(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
  // /* -------- Current -------- */
  // public static final PIDSGVAConstants simSlot2 =
  // new PIDSGVAConstants(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

  /**
   *
   * @param name                            pivot name
   * @param motorTypes                      motor types
   * @param motorDeviceIDs                  motor device ids
   * @param motorCanBus                     motor can bus
   * @param motorDirection                  motor directions
   * @param encoderType                     encoder type used for the pivot
   * @param encoderDeviceID                 device id of the encoder
   * @param encoderCanBus                   can bus of the encoder
   * @param encoderConfigZeroToOne          whether the encoder config operates in a 0 to 1 range
   * @param encoderConfigClockwisePositive  whether the encoder is configured as clockwise positive
   * @param encoderConfigMagnetOffsetUnit   unit of the encoder magnet offset
   * @param encoderConfigMagnetOffsetValue  value of the encoder magnet offset
   * @param motorEnabledBrakeMode           brake mode when motors are enabled
   * @param motorDisabledBrakeMode          brake mode when motors are disabled
   * @param motorToPivotRatio               ratio between the motor and encoder
   * @param encoderToPivotRatio             gear ratio between encoder and pivot
   * @param rotationalLimitsUnit            unit for rotational limit (default: rotations)
   * @param rotationalMin                   minimum rotation
   * @param rotationalMax                   maximum rotation
   * @param trapezoidalLengthUnit           unit of trapezoidal motion length
   * @param trapezoidalVelocityUnit         unit of trapezoidal velocity
   * @param trapezoidalLimitsVelocity       trapezoidal motion velocity limits
   * @param trapezoidalAccelerationUnit     unit of trapezoidal acceleration
   * @param trapezoidalLimitsAcceleration   trapezoidal motion acceleration limits
   * @param defaultTolerancesUnit           unit of default tolerances
   * @param defaultLowerTolerance           default lower tolerance
   * @param defaultUpperTolerance           default upper tolerance
   * @param feedForward                     feedforward model for the arm
   * @param simFeedForward                  feedforward model for simulation
   * @param currentLimitsConfigs            current limit configurations
   * @param slot0                           slot 0 configuration
   * @param slot1                           slot 1 configuration
   * @param slot2                           slot 2 configuration
   * @param simSlot0                        slot 0 configuration for simulation
   * @param simSlot1                        slot 1 configuration for simulation
   * @param simSlot2                        slot 2 configuration for simulation
   * @param closedLoopConfigsContinuousWrap whether continuous wrapping in closed loop configs
   * @param ffOffsetUnit                    unit of the feedforward offset
   * @param ffOffsetValue                   value of the feedforward offset
   * @param profileOffsetUnit               unit of the profile offset
   * @param profileOffsetValue              value of the profile offset
   * @param pivotMOI                        moment of inertia for the pivot
   * @param pivotLengthM                    length of the pivot in meters
   */
  public PivotConf(
      String name,
      Motors[] motorTypes,
      int[] motorDeviceIDs,
      String[] motorCanBus,
      InvertedValue[] motorDirection,
      Encoders encoderType,
      int encoderDeviceID,
      String encoderCanBus,
      boolean encoderConfigZeroToOne,
      boolean encoderConfigClockwisePositive,
      DistanceUnits encoderConfigMagnetOffsetUnit,
      double encoderConfigMagnetOffsetValue,
      NeutralModeValue[] motorEnabledBrakeMode,
      NeutralModeValue[] motorDisabledBrakeMode,
      int[][] motorToPivotRatio,
      int[][] encoderToPivotRatio,
      DistanceUnits rotationalLimitsUnit,
      double rotationalMin,
      double rotationalMax,
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
      PIDSGVAConstants simSlot0,
      PIDSGVAConstants simSlot1,
      PIDSGVAConstants simSlot2,
      boolean closedLoopConfigsContinuousWrap, // remove
      DistanceUnits ffOffsetUnit,
      double ffOffsetValue,
      DistanceUnits profileOffsetUnit,
      double profileOffsetValue,
      double pivotMOI,
      double pivotLengthM
  ) {
    super(
        name,
        motorTypes,
        motorDeviceIDs,
        motorCanBus,
        motorDirection,
        motorEnabledBrakeMode,
        motorDisabledBrakeMode,
        motorToPivotRatio,
        trapezoidalLengthUnit,
        VelocityUnits.ROTATIONS_PER_SECOND,
        trapezoidalVelocityUnit.convertX(
            trapezoidalLimitsVelocity, VelocityUnits.ROTATIONS_PER_SECOND
        ),
        AccelerationUnits.ROTATIONS_PER_SECOND2,
        trapezoidalAccelerationUnit.convertX(
            trapezoidalLimitsAcceleration, AccelerationUnits.ROTATIONS_PER_SECOND2
        ),
        DistanceUnits.ROTATION,
        defaultTolerancesUnit.convertX(defaultLowerTolerance, DistanceUnits.ROTATION),
        defaultTolerancesUnit.convertX(defaultUpperTolerance, DistanceUnits.ROTATION),
        currentLimitsConfigs,
        slot0,
        slot1,
        slot2,
        simSlot0,
        simSlot1,
        simSlot2
    );
    this.encoderType = encoderType;
    this.encoderDeviceID = encoderDeviceID;
    this.encoderCanBus = encoderCanBus;
    this.encoderToPivotRatio = super.calculateGearRatio(encoderToPivotRatio);
    this.motorToEncoderRatio =
        super.calculateGearRatio(motorToPivotRatio) / this.encoderToPivotRatio;
    this.encoderConfigZeroToOne = encoderConfigZeroToOne;
    this.encoderConfigClockwisePositive = encoderConfigClockwisePositive;
    this.encoderConfigMagnetOffsetValue = encoderConfigMagnetOffsetUnit.convertX(
        encoderConfigMagnetOffsetValue, this.encoderConfigMagnetOffsetUnit
    );

    MagnetSensorConfigs magnetSensorConfigs =
        new MagnetSensorConfigs()
            .withAbsoluteSensorRange(
                encoderConfigZeroToOne ? AbsoluteSensorRangeValue.Unsigned_0To1
                                       : AbsoluteSensorRangeValue.Signed_PlusMinusHalf
            )
            .withMagnetOffset(encoderConfigMagnetOffsetValue)
            .withSensorDirection(
                encoderConfigClockwisePositive ? SensorDirectionValue.Clockwise_Positive
                                               : SensorDirectionValue.CounterClockwise_Positive
            );
    this.encoderConfig = new CANcoderConfiguration().withMagnetSensor(magnetSensorConfigs);

    this.rotationalMin = rotationalLimitsUnit.convertX(rotationalMin, this.rotationalLimitUnit);
    this.rotationalMax = rotationalLimitsUnit.convertX(rotationalMax, this.rotationalLimitUnit);
    this.feedForward = IterativeRobotBase.isReal() ? feedForward.getArmFeedforward()
                                                   : simFeedForward.getArmFeedforward();
    this.closedLoopConfigsContinuousWrap = closedLoopConfigsContinuousWrap;
    this.ffOffsetValue = ffOffsetUnit.convertX(ffOffsetValue, this.ffOffsetUnit);
    this.profileOffsetValue =
        profileOffsetUnit.convertX(profileOffsetValue, this.profileOffsetUnit);

    this.pivotMOI = pivotMOI;
    this.pivotLengthM = pivotLengthM;
  }
}
