/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.units;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AccelerationUnitsTest {
  static final double kTol = 3e-6;
  static final double kAbsTol = 1e-12;

  private static void testAccelerationUnits(
      AccelerationUnits from, AccelerationUnits to, double conversionRate, double distance
  ) {
    double converted = from.convertX(distance, to);
    assertEquals(distance * conversionRate, converted);
  }

  private static void testAccelerationUnits(
      AccelerationUnits from,
      AccelerationUnits to,
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

  private static void testAccelerationUnitsSet(
      AccelerationUnits from, AccelerationUnits to, double rate
  ) {
    testAccelerationUnits(from, to, rate, 0.0);
    testAccelerationUnits(from, to, rate, 12345.678, kTol, 0.0);
    testAccelerationUnits(from, to, rate, -1.7976931348623157, kTol, 0.0);
    testAccelerationUnits(from, to, rate, -1868.0123456789, kTol, 0.0);
    testAccelerationUnits(from, to, rate, 179769313486231570000.0, kTol, 0.0);
  }

  /* -------- Meters -------- */
  @Test
  public void metersPerMillisecond2toMetersPerSecond2() {
    double rate = 1000000.0; // 1 * 10^6
    testAccelerationUnitsSet(
        AccelerationUnits.METERS_PER_MILLISECOND2, AccelerationUnits.METERS_PER_SECOND2, rate
    );
  }

  @Test
  public void metersPerSecond2toMetersPerMinute2() {
    double rate = 3600.0;
    testAccelerationUnitsSet(
        AccelerationUnits.METERS_PER_SECOND2, AccelerationUnits.METERS_PER_MINUTE2, rate
    );
  }

  @Test
  public void metersPerMinute2toMetersPerHour2() {
    double rate = 3600.0;
    testAccelerationUnitsSet(
        AccelerationUnits.METERS_PER_MINUTE2, AccelerationUnits.METERS_PER_HOUR2, rate
    );
  }

  @Test
  public void meterPerHour2toMeterPerMillisecond2() {
    double rate = 1.0 / 12960000000000.0;
    testAccelerationUnitsSet(
        AccelerationUnits.METERS_PER_HOUR2, AccelerationUnits.METERS_PER_MILLISECOND2, rate
    );
  }

  /* -------- Feet -------- */
  @Test
  public void feetPerMillisecond2toFeetPerSecond2() {
    double rate = 1000000.0; // 1 * 10^6
    testAccelerationUnitsSet(
        AccelerationUnits.FEET_PER_MILLISECOND2, AccelerationUnits.FEET_PER_SECOND2, rate
    );
  }

  @Test
  public void feetPerSecond2toFeetPerMinute2() {
    double rate = 3600.0;
    testAccelerationUnitsSet(
        AccelerationUnits.FEET_PER_SECOND2, AccelerationUnits.FEET_PER_MINUTE2, rate
    );
  }

  @Test
  public void feetPerMinute2toFeetPerHour2() {
    double rate = 3600.0;
    testAccelerationUnitsSet(
        AccelerationUnits.FEET_PER_MINUTE2, AccelerationUnits.FEET_PER_HOUR2, rate
    );
  }

  @Test
  public void feetPerHour2toFeetPerMillisecond2() {
    double rate = 1.0 / 12960000000000.0;
    testAccelerationUnitsSet(
        AccelerationUnits.FEET_PER_HOUR2, AccelerationUnits.FEET_PER_MILLISECOND2, rate
    );
  }

  /* -------- Inches -------- */
  @Test
  public void inchesPerMillisecond2toInchesPerSecond2() {
    double rate = 1000000.0; // 1 * 10^6
    testAccelerationUnitsSet(
        AccelerationUnits.INCHES_PER_MILLISECOND2, AccelerationUnits.INCHES_PER_SECOND2, rate
    );
  }

  @Test
  public void inchesPerSecond2toInchesPerMinute2() {
    double rate = 3600.0;
    testAccelerationUnitsSet(
        AccelerationUnits.INCHES_PER_SECOND2, AccelerationUnits.INCHES_PER_MINUTE2, rate
    );
  }

  @Test
  public void inchesPerMinute2toInchesPerHour2() {
    double rate = 3600.0;
    testAccelerationUnitsSet(
        AccelerationUnits.INCHES_PER_MINUTE2, AccelerationUnits.INCHES_PER_HOUR2, rate
    );
  }

  @Test
  public void inchPerHour2toInchPerMillisecond2() {
    double rate = 1.0 / 12960000000000.0;
    testAccelerationUnitsSet(
        AccelerationUnits.INCHES_PER_HOUR2, AccelerationUnits.INCHES_PER_MILLISECOND2, rate
    );
  }

  /* -------- Distance Conversions -------- */
  @Test
  public void meterPerSecond2toFeetPerSecond2() {
    double rate = 3.28084;
    testAccelerationUnitsSet(
        AccelerationUnits.METERS_PER_SECOND2, AccelerationUnits.FEET_PER_SECOND2, rate
    );
  }

  @Test
  public void feetPerSecond2toMeterPerSecond2() {
    double rate = 1.0 / 3.28084;
    testAccelerationUnitsSet(
        AccelerationUnits.FEET_PER_SECOND2, AccelerationUnits.METERS_PER_SECOND2, rate
    );
  }

  @Test
  public void feetPerSecond2toInchPerSecond2() {
    double rate = 12.0;
    testAccelerationUnitsSet(
        AccelerationUnits.FEET_PER_SECOND2, AccelerationUnits.INCHES_PER_SECOND2, rate
    );
  }

  @Test
  public void inchPerSecond2toFeetPerSecond2() {
    double rate = 1.0 / 12.0;
    testAccelerationUnitsSet(
        AccelerationUnits.INCHES_PER_SECOND2, AccelerationUnits.FEET_PER_SECOND2, rate
    );
  }
}
