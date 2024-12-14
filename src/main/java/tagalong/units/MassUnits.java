/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.units;

/**
 * Mass units enum identifiers.
 * Used for configuration and robot setup instead of the WPILib Java units to
 * avoid reallocations and garbage collections in match.
 */
public enum MassUnits {
  /* Commonly used units in FRC */
  /**
   * Grams
   */
  GRAMS(0.001),
  /**
   * Kilograms
   */
  KILOGRAMS(1.0),
  /**
   * Pounds
   */
  POUNDS(0.45359237),
  /**
   * Ounces
   */
  OUNCES(0.45359237 / 16.0);

  /**
   * Kilograms per unit (conversion l.t. 1 if less than a kilogram)
   */
  public final double conversionToKgs;

  /**
   *
   * @param kilogramsPerUnitMass Conversion factor for kilograms to the unit
   */
  MassUnits(double kilogramsPerUnitMass) {
    conversionToKgs = kilogramsPerUnitMass;
  }

  /**
   * Convert from the base unit to the given argument unit
   *
   * @param x          double to be converted
   * @param targetUnit target unit
   * @return x converted to the target units
   */
  public double convertX(double x, MassUnits targetUnit) {
    return x * conversionToKgs / targetUnit.conversionToKgs;
  }
}
