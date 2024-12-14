/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.units;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class VelocityUnitsTest {
  static final double kTol = 3e-6;
  static final double kAbsTol = 1e-12;

  private static void testVelocityUnits(
      VelocityUnits from, VelocityUnits to, double conversionRate, double distance
  ) {
    double converted = from.convertX(distance, to);
    assertEquals(distance * conversionRate, converted);
  }

  private static void testVelocityUnits(
      VelocityUnits from,
      VelocityUnits to,
      double conversionRate,
      double distance,
      double percentTol,
      double absoluteTol
  ) {
    double converted = from.convertX(distance, to);
    assertEquals(
        distance * conversionRate,
        converted,
        Math.max(absoluteTol, Math.abs(percentTol * converted))
    );
  }

  private static void testVelocityUnitsSet(VelocityUnits from, VelocityUnits to, double rate) {
    testVelocityUnits(from, to, rate, 0.0);
    testVelocityUnits(from, to, rate, 12345.6789, kTol, 0.0);
    testVelocityUnits(from, to, rate, -1.7976931348623157, kTol, 0.0);
    testVelocityUnits(from, to, rate, -1868.0123456789, kTol, 0.0);
    testVelocityUnits(from, to, rate, 179769313486231570000.0, kTol, 0.0);
  }

  /* -------- Meters -------- */
  @Test
  public void metersPerMillisecondToMetersPerSecond() {
    double rate = 1000.0; // 1 * 10^6
    testVelocityUnitsSet(
        VelocityUnits.METERS_PER_MILLISECOND, VelocityUnits.METERS_PER_SECOND, rate
    );
  }

  @Test
  public void metersPerSecondToMetersPerMinute() {
    double rate = 60.0;
    testVelocityUnitsSet(VelocityUnits.METERS_PER_SECOND, VelocityUnits.METERS_PER_MINUTE, rate);
  }

  @Test
  public void metersPerMinuteToMetersPerHour() {
    double rate = 60.0;
    testVelocityUnitsSet(VelocityUnits.METERS_PER_MINUTE, VelocityUnits.METERS_PER_HOUR, rate);
  }

  @Test
  public void meterPerHourToMeterPerMillisecond() {
    double rate = 1.0 / 3600000.0;
    testVelocityUnitsSet(VelocityUnits.METERS_PER_HOUR, VelocityUnits.METERS_PER_MILLISECOND, rate);
  }

  /* -------- Feet -------- */
  @Test
  public void feetPerMillisecondToFeetPerSecond() {
    double rate = 1000.0; // 1 * 10^6
    testVelocityUnitsSet(VelocityUnits.FEET_PER_MILLISECOND, VelocityUnits.FEET_PER_SECOND, rate);
  }

  @Test
  public void feetPerSecondToFeetPerMinute() {
    double rate = 60.0;
    testVelocityUnitsSet(VelocityUnits.FEET_PER_SECOND, VelocityUnits.FEET_PER_MINUTE, rate);
  }

  @Test
  public void feetPerMinuteToFeetPerHour() {
    double rate = 60.0;
    testVelocityUnitsSet(VelocityUnits.FEET_PER_MINUTE, VelocityUnits.FEET_PER_HOUR, rate);
  }

  @Test
  public void feetPerHourToFeetPerMillisecond() {
    double rate = 1.0 / 3600000.0;
    testVelocityUnitsSet(VelocityUnits.FEET_PER_HOUR, VelocityUnits.FEET_PER_MILLISECOND, rate);
  }

  /* -------- Inches -------- */
  @Test
  public void inchesPerMillisecondToInchesPerSecond() {
    double rate = 1000.0; // 1 * 10^6
    testVelocityUnitsSet(
        VelocityUnits.INCHES_PER_MILLISECOND, VelocityUnits.INCHES_PER_SECOND, rate
    );
  }

  @Test
  public void inchesPerSecondToInchesPerMinute() {
    double rate = 60.0;
    testVelocityUnitsSet(VelocityUnits.INCHES_PER_SECOND, VelocityUnits.INCHES_PER_MINUTE, rate);
  }

  @Test
  public void inchesPerMinuteToInchesPerHour() {
    double rate = 60.0;
    testVelocityUnitsSet(VelocityUnits.INCHES_PER_MINUTE, VelocityUnits.INCHES_PER_HOUR, rate);
  }

  @Test
  public void inchPerHourToInchPerMillisecond() {
    double rate = 1.0 / 3600000.0;
    testVelocityUnitsSet(VelocityUnits.INCHES_PER_HOUR, VelocityUnits.INCHES_PER_MILLISECOND, rate);
  }

  /* -------- Distance Conversions -------- */
  @Test
  public void meterPerSecondToFeetPerSecond() {
    double rate = 3.28084;
    testVelocityUnitsSet(VelocityUnits.METERS_PER_SECOND, VelocityUnits.FEET_PER_SECOND, rate);
  }

  @Test
  public void feetPerSecondToMeterPerSecond() {
    double rate = 1.0 / 3.28084;
    testVelocityUnitsSet(VelocityUnits.FEET_PER_SECOND, VelocityUnits.METERS_PER_SECOND, rate);
  }

  @Test
  public void feetPerSecondToInchPerSecond() {
    double rate = 12.0;
    testVelocityUnitsSet(VelocityUnits.FEET_PER_SECOND, VelocityUnits.INCHES_PER_SECOND, rate);
  }

  @Test
  public void inchPerSecondToFeetPerSecond() {
    double rate = 1.0 / 12.0;
    testVelocityUnitsSet(VelocityUnits.INCHES_PER_SECOND, VelocityUnits.FEET_PER_SECOND, rate);
  }
}
