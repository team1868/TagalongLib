/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.subsystems.micro.confs;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.IterativeRobotBase;
import tagalong.controls.FeedforwardConstants;
import tagalong.controls.PIDSGVAConstants;
import tagalong.devices.Motors;
import tagalong.units.AccelerationUnits;
import tagalong.units.DistanceUnits;
import tagalong.units.VelocityUnits;

/**
 * Configuration for the roller microsystem
 */
public class RollerConf extends MicrosystemConf {
  /**
   * Dimension of the mechanical system
   */
  public final double mech2dDim;
  /**
   * Sim root x coordinate
   */
  public final double rootX;
  /**
   * Sim root y coordinate
   */
  public final double rootY;
  /**
   * Number of simulated ligaments used
   */
  public final int simNumLigaments;
  /**
   * Moment of inertia for the roller
   */
  public final double rollerMOI;
  /**
   * Motor feedforward model
   */
  public final SimpleMotorFeedforward feedForward;

  /**
   *
   * @param name                          name of the subsystem
   * @param motorTypes                    array of motor types used
   * @param motorDeviceIDs                CAN IDs of the motors
   * @param motorCanBus                   CAN buses which the motors are connected to
   * @param motorDirection                motor inversion settings
   * @param motorEnabledBrakeMode         brake mode when motors are enabled
   * @param motorDisabledBrakeMode        brake mode when motors are disabled
   * @param gearRatio                     gear ratios
   * @param trapezoidalLengthUnit         units for trapezoidal motion length
   * @param trapezoidalVelocityUnit       units for trapezoidal motion velocity
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
   * @param simSlot0                      simulated PID slot 0 configuration
   * @param simSlot1                      simulated PID slot 1 configuration
   * @param simSlot2                      simulated PID slot 2 configuration
   * @param mech2dDim                     dimensions of mechanical system
   * @param rootX                         sim root x coordinate
   * @param rootY                         sim root y coordinate
   * @param simNumLigaments               number of simulated ligaments used
   * @param rollerMOI                     moment of inertia for the roller
   */
  public RollerConf(
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
      FeedforwardConstants feedForward,
      FeedforwardConstants simFeedForward,
      CurrentLimitsConfigs currentLimitsConfigs,
      PIDSGVAConstants slot0,
      PIDSGVAConstants slot1,
      PIDSGVAConstants slot2,
      PIDSGVAConstants simSlot0,
      PIDSGVAConstants simSlot1,
      PIDSGVAConstants simSlot2,
      double mech2dDim,
      double rootX,
      double rootY,
      int simNumLigaments,
      double rollerMOI
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
    this.mech2dDim = mech2dDim;
    this.rootX = rootX;
    this.rootY = rootY;
    this.simNumLigaments = simNumLigaments;
    this.rollerMOI = rollerMOI;
    this.feedForward = IterativeRobotBase.isReal() ? feedForward.getSimpleMotorFeedforward()
                                                   : simFeedForward.getSimpleMotorFeedforward();
  }
}
