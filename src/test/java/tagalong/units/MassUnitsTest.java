/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.units;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MassUnitsTest {
  static final double kTol = 3e-6;
  static final double kAbsTol = 1e-12;

  private static void testMassUnits(
      MassUnits from, MassUnits to, double conversionRate, double mass
  ) {
    double converted = from.convertX(mass, to);
    assertEquals(mass * conversionRate, converted);
  }

  private static void testMassUnits(
      MassUnits from,
      MassUnits to,
      double conversionRate,
      double mass,
      double percentTol,
      double absoluteTol
  ) {
    double converted = from.convertX(mass, to);
    assertEquals(
        mass * conversionRate, converted, Math.max(absoluteTol, Math.abs(percentTol * converted))
    );
  }

  @Test
  public void gramsToKilograms() {
    double rate = 0.001;
    testMassUnits(MassUnits.GRAMS, MassUnits.KILOGRAMS, rate, 0.0);
    testMassUnits(MassUnits.GRAMS, MassUnits.KILOGRAMS, rate, -1.7976931348623157);
    testMassUnits(MassUnits.GRAMS, MassUnits.KILOGRAMS, rate, -1868.0123456789);
    testMassUnits(MassUnits.GRAMS, MassUnits.KILOGRAMS, rate, 179769313486231570000.0);
  }

  @Test
  public void gramsToPounds() {
    double rate = 0.00220462;
    testMassUnits(MassUnits.GRAMS, MassUnits.POUNDS, rate, 0.0);
    testMassUnits(MassUnits.GRAMS, MassUnits.POUNDS, rate, -1.7976931348623157, kTol, 0.0);
    testMassUnits(MassUnits.GRAMS, MassUnits.POUNDS, rate, -1868.0123456789, kTol, 0.0);
    testMassUnits(MassUnits.GRAMS, MassUnits.POUNDS, rate, 179769313486231570000.0, kTol, 0.0);
  }

  @Test
  public void gramsToOunces() {
    double rate = 1.0 / 28.34952;
    testMassUnits(MassUnits.GRAMS, MassUnits.OUNCES, rate, 0.0);
    testMassUnits(MassUnits.GRAMS, MassUnits.OUNCES, rate, -1.7976931348623157, kTol, 0.0);
    testMassUnits(MassUnits.GRAMS, MassUnits.OUNCES, rate, -1868.0123456789, kTol, 0.0);
    testMassUnits(MassUnits.GRAMS, MassUnits.OUNCES, rate, 179769313486231570000.0, kTol, 0.0);
  }

  @Test
  public void kilogramsToGrams() {
    double rate = 1000.0;
    testMassUnits(MassUnits.KILOGRAMS, MassUnits.GRAMS, rate, 0.0);
    testMassUnits(MassUnits.KILOGRAMS, MassUnits.GRAMS, rate, -1.7976931348623157);
    testMassUnits(MassUnits.KILOGRAMS, MassUnits.GRAMS, rate, -1868.0123456789);
    testMassUnits(MassUnits.KILOGRAMS, MassUnits.GRAMS, rate, 179769313486231570000.0);
  }

  @Test
  public void kilogramsToPounds() {
    double rate = 1.0 / 0.45359237;
    testMassUnits(MassUnits.KILOGRAMS, MassUnits.POUNDS, rate, 0.0);
    testMassUnits(MassUnits.KILOGRAMS, MassUnits.POUNDS, rate, -1.7976931348623157, kTol, 0.0);
    testMassUnits(MassUnits.KILOGRAMS, MassUnits.POUNDS, rate, -1868.0123456789, kTol, 0.0);
    testMassUnits(MassUnits.KILOGRAMS, MassUnits.POUNDS, rate, 179769313486231570000.0, kTol, 0.0);
  }

  @Test
  public void kilogramsToOunces() {
    double rate = 35.274;
    testMassUnits(MassUnits.KILOGRAMS, MassUnits.OUNCES, rate, 0.0);
    testMassUnits(MassUnits.KILOGRAMS, MassUnits.OUNCES, rate, -1.7976931348623157, kTol, 0.0);
    testMassUnits(MassUnits.KILOGRAMS, MassUnits.OUNCES, rate, -1868.0123456789, kTol, 0.0);
    testMassUnits(MassUnits.KILOGRAMS, MassUnits.OUNCES, rate, 179769313486231570000.0, kTol, 0.0);
  }

  @Test
  public void poundsToGrams() {
    double rate = 453.59237;
    testMassUnits(MassUnits.POUNDS, MassUnits.GRAMS, rate, 0.0);
    testMassUnits(MassUnits.POUNDS, MassUnits.GRAMS, rate, -1.7976931348623157, kTol, 0.0);
    testMassUnits(MassUnits.POUNDS, MassUnits.GRAMS, rate, -1868.0123456789, kTol, 0.0);
    testMassUnits(MassUnits.POUNDS, MassUnits.GRAMS, rate, 179769313486231570000.0);
  }

  @Test
  public void poundsToKilograms() {
    double rate = 0.453592;
    testMassUnits(MassUnits.POUNDS, MassUnits.KILOGRAMS, rate, 0.0);
    testMassUnits(MassUnits.POUNDS, MassUnits.KILOGRAMS, rate, -1.7976931348623157, kTol, 0.0);
    testMassUnits(MassUnits.POUNDS, MassUnits.KILOGRAMS, rate, -1868.0123456789, kTol, 0.0);
    testMassUnits(MassUnits.POUNDS, MassUnits.KILOGRAMS, rate, 179769313486231570000.0, kTol, 0.0);
  }

  @Test
  public void poundsToOunces() {
    double rate = 16.0;
    testMassUnits(MassUnits.POUNDS, MassUnits.OUNCES, rate, 0.0);
    testMassUnits(MassUnits.POUNDS, MassUnits.OUNCES, rate, -1.7976931348623157);
    testMassUnits(MassUnits.POUNDS, MassUnits.OUNCES, rate, -1868.0123456789);
    testMassUnits(MassUnits.POUNDS, MassUnits.OUNCES, rate, 179769313486231570000.0);
  }

  @Test
  public void ouncesToGrams() {
    double rate = 28.34952312;

    testMassUnits(MassUnits.OUNCES, MassUnits.GRAMS, rate, 0.0);
    testMassUnits(MassUnits.OUNCES, MassUnits.GRAMS, rate, -1.7976931348623157, kTol, 0.0);
    testMassUnits(MassUnits.OUNCES, MassUnits.GRAMS, rate, -1868.0123456789, kTol, 0.0);
    testMassUnits(MassUnits.OUNCES, MassUnits.GRAMS, rate, 179769313486231570000.0, kTol, 0.0);
  }

  @Test
  public void ouncesToKilograms() {
    double rate = 0.02834952312;

    testMassUnits(MassUnits.OUNCES, MassUnits.KILOGRAMS, rate, 0.0);
    testMassUnits(MassUnits.OUNCES, MassUnits.KILOGRAMS, rate, -1.7976931348623157, kTol, 0.0);
    testMassUnits(MassUnits.OUNCES, MassUnits.KILOGRAMS, rate, -1868.0123456789, kTol, 0.0);
    testMassUnits(MassUnits.OUNCES, MassUnits.KILOGRAMS, rate, 179769313486231570000.0, kTol, 0.0);
  }

  @Test
  public void ouncesToPounds() {
    double rate = 0.0625;
    testMassUnits(MassUnits.OUNCES, MassUnits.POUNDS, rate, 0.0);
    testMassUnits(MassUnits.OUNCES, MassUnits.POUNDS, rate, -1.7976931348623157);
    testMassUnits(MassUnits.OUNCES, MassUnits.POUNDS, rate, -1868.0123456789);
    testMassUnits(MassUnits.OUNCES, MassUnits.POUNDS, rate, 179769313486231570000.0);
  }
}
