/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.units;

/**
 * Time units enum identifiers.
 * Used for configuration and robot setup instead of the WPILib Java units to
 * avoid reallocations and garbage collections in match.
 */
public enum TimeUnits {
  /**
   * Milliseconds
   */
  MILLISECOND(.001),
  /**
   * Seconds
   */
  SECOND(1.0),
  /**
   * Minutes
   */
  MINUTE(60.0),
  /**
   * Hour
   */
  HOUR(3600.0);

  /**
   * Seconds per unit (conversion l.t. 1 if smaller than a second)
   */
  public final double conversionToSeconds;

  /**
   *
   * @param secondsPerUnitTime Conversion factor for seconds to the unit
   */
  TimeUnits(double secondsPerUnitTime) {
    conversionToSeconds = secondsPerUnitTime;
  }

  /**
   * Convert from the base unit to the given argument unit
   *
   * @param x          double to be converted
   * @param targetUnit target unit
   * @return x converted to the target units
   */
  public double convertX(double x, TimeUnits targetUnit) {
    // convert to second argument unit
    return x * conversionToSeconds / targetUnit.conversionToSeconds;
  }
}
