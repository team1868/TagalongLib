package tagalong.subsystems.micro.confs;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import tagalong.controls.FeedforwardConstants;
import tagalong.controls.PIDSGVAConstants;
import tagalong.devices.Motors;
import tagalong.units.AccelerationUnits;
import tagalong.units.DistanceUnits;
import tagalong.units.VelocityUnits;

/**
 * Configuration for the flywheel
 */
public class FlywheelConf extends RollerConf {
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
   * @param simSlot0                      simulated PID slot 0 configuration
   * @param simSlot1                      simulated PID slot 1 configuration
   * @param simSlot2                      simulated PID slot 2 configuration
   * @param simNumLigaments               number of simulated ligaments used
   * @param rollerMOI                     moment of inertia for the roller
   */
  public FlywheelConf(
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
        feedForward,
        simFeedForward,
        currentLimitsConfigs,
        slot0,
        slot1,
        slot2,
        simSlot0,
        simSlot1,
        simSlot2,
        simNumLigaments,
        rollerMOI
    );

    for (int i = 0; i < numMotors; i++) {
      motorConfig[i].MotorOutput.PeakReverseDutyCycle = 0.0;
      motorConfig[i].Voltage.PeakReverseVoltage = 0.0;
    }
  }
}
