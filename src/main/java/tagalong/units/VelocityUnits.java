/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.units;

/**
 * Velocity units enum identifiers.
 * Used for configuration and robot setup instead of the WPILib Java units to
 * avoid reallocations and garbage collections in match.
 */
public enum VelocityUnits {
  /* -------- Common Aliases -------- */
  /**
   * Meter per Second aka MPS aka meter/second
   */
  MPS(DistanceUnits.METER, TimeUnits.SECOND),
  /**
   * Rotations per Second aka RPS aka rotations/second
   */
  ROTPS(DistanceUnits.ROTATION, TimeUnits.SECOND),
  /**
   * Rotations per Second aka RPS aka rotations/second
   */
  RPS(DistanceUnits.ROTATION, TimeUnits.SECOND),
  /**
   * Radians per Second aka RadPS aka radians/second
   */
  RADPS(DistanceUnits.RADIAN, TimeUnits.SECOND),
  /**
   * Degrees per Second aka DegPS aka deg/second
   */
  DEGPS(DistanceUnits.DEGREE, TimeUnits.SECOND),
  /* -------- Meters -------- */
  /**
   * Meter per Millisecond aka meter/millisecond
   */
  METERS_PER_MILLISECOND(DistanceUnits.METER, TimeUnits.MILLISECOND),
  /**
   * Meter per Second aka MPS aka meter/second
   */
  METERS_PER_SECOND(DistanceUnits.METER, TimeUnits.SECOND),
  /**
   * Meter per Minute aka meter/minute
   */
  METERS_PER_MINUTE(DistanceUnits.METER, TimeUnits.MINUTE),
  /**
   * Meter per Hour aka meter/hour
   */
  METERS_PER_HOUR(DistanceUnits.METER, TimeUnits.HOUR),
  /* -------- Feet -------- */
  /**
   * Feet per Millisecond aka feet/millisecond
   */
  FEET_PER_MILLISECOND(DistanceUnits.FEET, TimeUnits.MILLISECOND),
  /**
   * Feet per Second aka FPS aka feet/second
   */
  FEET_PER_SECOND(DistanceUnits.FEET, TimeUnits.SECOND),
  /**
   * Feet per Minute aka feet/minute
   */
  FEET_PER_MINUTE(DistanceUnits.FEET, TimeUnits.MINUTE),
  /**
   * Feet per Hour aka feet/hour
   */
  FEET_PER_HOUR(DistanceUnits.FEET, TimeUnits.HOUR),
  /* -------- Inches -------- */
  /**
   * Inches per Millisecond aka feet/millisecond
   */
  INCHES_PER_MILLISECOND(DistanceUnits.INCH, TimeUnits.MILLISECOND),
  /**
   * Inches per Second aka inch/second
   */
  INCHES_PER_SECOND(DistanceUnits.INCH, TimeUnits.SECOND),
  /**
   * Inches per Minute aka inch/minute
   */
  INCHES_PER_MINUTE(DistanceUnits.INCH, TimeUnits.MINUTE),
  /**
   * Inches per Hour aka inch/hour
   */
  INCHES_PER_HOUR(DistanceUnits.INCH, TimeUnits.HOUR),
  /* -------- Rotations -------- */
  /**
   * Rotations per Millisecond aka rotations/Millisecond
   */
  ROTATIONS_PER_MILLISECOND(DistanceUnits.ROTATION, TimeUnits.MILLISECOND),
  /**
   * Rotations per Second aka RPS aka rotations/second
   */
  ROTATIONS_PER_SECOND(DistanceUnits.ROTATION, TimeUnits.SECOND),
  /**
   * Rotations per Minute aka RPM aka rotations/minute
   */
  ROTATIONS_PER_MINUTE(DistanceUnits.ROTATION, TimeUnits.MINUTE),
  /**
   * Rotations per Hour aka rotations/hour
   */
  ROTATIONS_PER_HOUR(DistanceUnits.ROTATION, TimeUnits.HOUR),
  /* -------- Radians -------- */
  /**
   * Radians per Millisecond aka radians/millisecond
   */
  RADIANS_PER_MILLISECOND(DistanceUnits.RADIAN, TimeUnits.MILLISECOND),
  /**
   * Radians per Second aka radians/second
   */
  RADIANS_PER_SECOND(DistanceUnits.RADIAN, TimeUnits.SECOND),
  /**
   * Radians per Minute aka radians/minute
   */
  RADIANS_PER_MINUTE(DistanceUnits.RADIAN, TimeUnits.MINUTE),
  /**
   * Radians per Hour aka radians/hour
   */
  RADIANS_PER_HOUR(DistanceUnits.RADIAN, TimeUnits.HOUR),
  /* -------- Degrees -------- */
  /**
   * Degrees per Millisecond aka deg/millisecond
   */
  DEGREES_PER_MILLISECOND(DistanceUnits.DEGREE, TimeUnits.MILLISECOND),
  /**
   * Degrees per Second aka DegPS aka deg/second
   */
  DEGREES_PER_SECOND(DistanceUnits.DEGREE, TimeUnits.SECOND),
  /**
   * Degrees per Minute aka deg/minute
   */
  DEGREES_PER_MINUTE(DistanceUnits.DEGREE, TimeUnits.MINUTE),
  /**
   * Degrees per Hour aka deg/hour
   */
  DEGREES_PER_HOUR(DistanceUnits.DEGREE, TimeUnits.HOUR);

  /**
   * The distance portion of the velocity unit
   */
  public final DistanceUnits distanceUnit;
  /**
   * The time portion of the velocity unit
   */
  public final TimeUnits timeUnit;

  /**
   * @param distanceUnit desired distance unit
   * @param timeUnit     desired time unit
   */
  VelocityUnits(DistanceUnits distanceUnit, TimeUnits timeUnit) {
    this.distanceUnit = distanceUnit;
    this.timeUnit = timeUnit;
  }

  /**
   * Convert from base class' unit to the targetUnit type
   *
   * @param x          double value to be converted
   * @param targetUnit target unit
   * @return x converted to the target units
   */
  public double convertX(double x, VelocityUnits targetUnit) {
    return distanceUnit.convertX(x, targetUnit.distanceUnit)
        / timeUnit.convertX(1.0, targetUnit.timeUnit);
  }
}
