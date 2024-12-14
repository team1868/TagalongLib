/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.units;

/**
 * Acceleration units enum identifiers.
 * Used for configuration and robot setup instead of the WPILib Java units to
 * avoid reallocations and garbage collections in match.
 */
public enum AccelerationUnits {
  /* -------- Common Aliases -------- */
  /**
   * Meters per second per second
   */
  MPS2(DistanceUnits.METER, TimeUnits.SECOND),
  /**
   * Rotations per second per second
   */
  ROTPS2(DistanceUnits.ROTATION, TimeUnits.SECOND),
  /**
   * Rotations per second per second
   */
  RPS2(DistanceUnits.ROTATION, TimeUnits.SECOND),
  /**
   * Radians per second per second
   */
  RADPS2(DistanceUnits.RADIAN, TimeUnits.SECOND),
  /**
   * Degrees per second per second AKA degrees per second^2
   */
  DEGPS2(DistanceUnits.DEGREE, TimeUnits.SECOND),
  /* -------- Meters -------- */
  /**
   * Meters per millisecond per millisecond
   */
  METERS_PER_MILLISECOND2(DistanceUnits.METER, TimeUnits.MILLISECOND),
  /**
   * Meters per second per second
   */
  METERS_PER_SECOND2(DistanceUnits.METER, TimeUnits.SECOND),
  /**
   * Meters per minute per minute
   */
  METERS_PER_MINUTE2(DistanceUnits.METER, TimeUnits.MINUTE),
  /**
   * Meters per hour per hour
   */
  METERS_PER_HOUR2(DistanceUnits.METER, TimeUnits.HOUR),
  /* -------- Feet -------- */
  /**
   * Feet per millisecond per millisecond
   */
  FEET_PER_MILLISECOND2(DistanceUnits.FEET, TimeUnits.MILLISECOND),
  /**
   * Feet per second per second
   */
  FEET_PER_SECOND2(DistanceUnits.FEET, TimeUnits.SECOND),
  /**
   * Feet per minute per minute
   */
  FEET_PER_MINUTE2(DistanceUnits.FEET, TimeUnits.MINUTE),
  /**
   * Feet per hour per hour
   */
  FEET_PER_HOUR2(DistanceUnits.FEET, TimeUnits.HOUR),
  /* -------- Inches -------- */
  /**
   * Inches per millisecond per millisecond
   */
  INCHES_PER_MILLISECOND2(DistanceUnits.INCH, TimeUnits.MILLISECOND),
  /**
   * Inches per second per second
   */
  INCHES_PER_SECOND2(DistanceUnits.INCH, TimeUnits.SECOND),
  /**
   * Inches per minute per minute
   */
  INCHES_PER_MINUTE2(DistanceUnits.INCH, TimeUnits.MINUTE),
  /**
   * Inches per hour per hour
   */
  INCHES_PER_HOUR2(DistanceUnits.INCH, TimeUnits.HOUR),
  /* -------- Rotations -------- */
  /**
   * Rotations per millisecond per millisecond
   */
  ROTATIONS_PER_MILLISECOND2(DistanceUnits.ROTATION, TimeUnits.MILLISECOND),
  /**
   * Rotations per second per second
   */
  ROTATIONS_PER_SECOND2(DistanceUnits.ROTATION, TimeUnits.SECOND),
  /**
   * Rotations per minute per minute
   */
  ROTATIONS_PER_MINUTE2(DistanceUnits.ROTATION, TimeUnits.MINUTE),
  /**
   * Rotations per hour per hour
   */
  ROTATIONS_PER_HOUR2(DistanceUnits.ROTATION, TimeUnits.HOUR),
  /* -------- Radians -------- */
  /**
   * Radians per millisecond per millisecond
   */
  RADIANS_PER_MILLISECOND2(DistanceUnits.RADIAN, TimeUnits.MILLISECOND),
  /**
   * Radians per second per second
   */
  RADIANS_PER_SECOND2(DistanceUnits.RADIAN, TimeUnits.SECOND),
  /**
   * Radians per minute per minute
   */
  RADIANS_PER_MINUTE2(DistanceUnits.RADIAN, TimeUnits.MINUTE),
  /**
   * Radians per hour per hour
   */
  RADIANS_PER_HOUR2(DistanceUnits.RADIAN, TimeUnits.HOUR),
  /* -------- Degrees -------- */
  /**
   * Degrees per millisecond per millisecond
   */
  DEGREES_PER_MILLISECOND2(DistanceUnits.DEGREE, TimeUnits.MILLISECOND),
  /**
   * Degrees per second per second
   */
  DEGREES_PER_SECOND2(DistanceUnits.DEGREE, TimeUnits.SECOND),
  /**
   * Degrees per minute per minute
   */
  DEGREES_PER_MINUTE2(DistanceUnits.DEGREE, TimeUnits.MINUTE),
  /**
   * Degrees per hour per hour
   */
  DEGREES_PER_HOUR2(DistanceUnits.DEGREE, TimeUnits.HOUR);

  /**
   * Unit of distance
   */
  public final DistanceUnits distanceUnit;
  /**
   * Unit of time
   */
  public final TimeUnits timeUnit;

  /**
   *
   * @param distanceUnit desired distance unit
   * @param timeUnit     desired time unit
   */
  AccelerationUnits(DistanceUnits distanceUnit, TimeUnits timeUnit) {
    this.distanceUnit = distanceUnit;
    this.timeUnit = timeUnit;
  }

  /**
   *
   * @param x          double to be converted
   * @param targetUnit target unit
   * @return x converted to the target units
   */
  public double convertX(double x, AccelerationUnits targetUnit) {
    // convert to second argument unit
    return distanceUnit.convertX(x, targetUnit.distanceUnit)
        / timeUnit.convertX(timeUnit.convertX(1.0, targetUnit.timeUnit), targetUnit.timeUnit);
  }
}
