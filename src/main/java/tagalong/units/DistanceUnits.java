/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.units;

/**
 * Distance units enum identifiers.
 * Used for configuration and robot setup instead of the WPILib Java units to
 * avoid reallocations and garbage collections in match.
 */
public enum DistanceUnits {
  /**
   * Meter
   */
  METER(false, 1.0),
  /**
   * Foot
   */
  FEET(false, 0.3048),
  /**
   * Inch
   */
  INCH(false, 0.0254),
  /**
   * Rotation
   */
  ROTATION(true, 1.0),
  /**
   * Radian
   */
  RADIAN(true, 0.5 / Math.PI),
  /**
   * Degree
   */
  DEGREE(true, 1.0 / 360.0);

  /**
   * True if unit measures rotational distance (degree, radians, rotation)
   * False if unit measures linear distance (inch, foot, meter)
   */
  public final boolean isRotational;
  /**
   * If rotational, rotations per unit (conversion l.t. 1 if smaller than a
   * rotation)
   * If linear, meters per unit (conversion l.t. 1 if smaller than a meter)
   */
  public final double conversionToBase;

  /**
   *
   * @param rotationalUnit  True if it is a rotational unit
   * @param perUnitDist     Conversion rate to the common unit
   */
  DistanceUnits(boolean rotationalUnit, double perUnitDist) {
    isRotational = rotationalUnit;
    conversionToBase = perUnitDist;
  }

  /**
   *
   * @param x          double to be converted
   * @param targetUnit target units
   * @return x converted to the target units
   */
  public double convertX(double x, DistanceUnits targetUnit) {
    if (isRotational != targetUnit.isRotational) {
      System.err.println("Incompatible conversion between rotational and linear distances");
      System.exit(1);
      return 0.0;
    } else {
      return x * conversionToBase / targetUnit.conversionToBase;
    }
  }
}
