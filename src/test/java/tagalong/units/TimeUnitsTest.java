/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.units;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TimeUnitsTest {
  static final double kTol = 3e-6;
  static final double kAbsTol = 1e-1;

  private static void testTimeUnits(
      TimeUnits from, TimeUnits to, double conversionRate, double dur
  ) {
    double converted = from.convertX(dur, to);
    assertEquals(dur * conversionRate, converted);
  }

  private static void testTimeUnits(
      TimeUnits from,
      TimeUnits to,
      double conversionRate,
      double dur,
      double percentTol,
      double absoluteTol
  ) {
    double converted = from.convertX(dur, to);
    assertEquals(
        dur * conversionRate, converted, Math.max(absoluteTol, Math.abs(percentTol * converted))
    );
  }

  @Test
  public void millisecondsToSeconds() {
    double rate = 1.0 / 1000.0;
    testTimeUnits(TimeUnits.MILLISECOND, TimeUnits.SECOND, rate, 0.0);
    testTimeUnits(TimeUnits.MILLISECOND, TimeUnits.SECOND, rate, 6000.0);
    testTimeUnits(TimeUnits.MILLISECOND, TimeUnits.SECOND, rate, -1034394.0);
    testTimeUnits(TimeUnits.MILLISECOND, TimeUnits.SECOND, rate, 1.8689235);
  }

  @Test
  public void millisecondsToMinutes() {
    double rate = 1.0 / 60000.0;
    testTimeUnits(TimeUnits.MILLISECOND, TimeUnits.MINUTE, rate, 0.0);
    testTimeUnits(TimeUnits.MILLISECOND, TimeUnits.MINUTE, rate, 6000.0);
    testTimeUnits(TimeUnits.MILLISECOND, TimeUnits.MINUTE, rate, -1034394.0, 0.0, kAbsTol);
    testTimeUnits(TimeUnits.MILLISECOND, TimeUnits.MINUTE, rate, 1.8689235, 0.0, kAbsTol);
  }

  @Test
  public void millisecondsToHours() {
    double rate = 1.0 / 3600000.0;
    testTimeUnits(TimeUnits.MILLISECOND, TimeUnits.HOUR, rate, 0.0);
    testTimeUnits(TimeUnits.MILLISECOND, TimeUnits.HOUR, rate, 6000.0, 0.0, kAbsTol);
    testTimeUnits(TimeUnits.MILLISECOND, TimeUnits.HOUR, rate, -1034394.0);
    testTimeUnits(TimeUnits.MILLISECOND, TimeUnits.HOUR, rate, 1.8689235);
  }

  @Test
  public void secondsToMilliseconds() {
    double rate = 1000.0;
    testTimeUnits(TimeUnits.SECOND, TimeUnits.MILLISECOND, rate, 0.0);
    testTimeUnits(TimeUnits.SECOND, TimeUnits.MILLISECOND, rate, 6000);
    testTimeUnits(TimeUnits.SECOND, TimeUnits.MILLISECOND, rate, -1034394);
    testTimeUnits(TimeUnits.SECOND, TimeUnits.MILLISECOND, rate, 1.8689235);
  }

  @Test
  public void secondsToHours() {
    double rate = 1.0 / 3600.0;
    testTimeUnits(TimeUnits.SECOND, TimeUnits.HOUR, rate, 0.0);
    testTimeUnits(TimeUnits.SECOND, TimeUnits.HOUR, rate, 6000);
    testTimeUnits(TimeUnits.SECOND, TimeUnits.HOUR, rate, -1034394);
    testTimeUnits(TimeUnits.SECOND, TimeUnits.HOUR, rate, 1.8689235);
  }

  public void testSecondsToMinutes(double s) {
    double min = TimeUnits.SECOND.convertX(s, TimeUnits.MINUTE);
    assertEquals(min, s / 60.0, Math.abs(kTol * min));
  }

  @Test
  public void secondsToMinutes() {
    double rate = 1.0 / 60.0;
    testTimeUnits(TimeUnits.SECOND, TimeUnits.MINUTE, rate, 0.0);
    testTimeUnits(TimeUnits.SECOND, TimeUnits.MINUTE, rate, 6000.0);
    testTimeUnits(TimeUnits.SECOND, TimeUnits.MINUTE, rate, -1034394.0);
    testTimeUnits(TimeUnits.SECOND, TimeUnits.MINUTE, rate, 1.8689235);
  }

  @Test
  public void minutesToMilliseconds() {
    double rate = 60000.0;
    testTimeUnits(TimeUnits.MINUTE, TimeUnits.MILLISECOND, rate, 0.0);
    testTimeUnits(TimeUnits.MINUTE, TimeUnits.MILLISECOND, rate, 6000.0);
    testTimeUnits(TimeUnits.MINUTE, TimeUnits.MILLISECOND, rate, -1034394.0);
    testTimeUnits(TimeUnits.MINUTE, TimeUnits.MILLISECOND, rate, 1.8689235, 0.0, kAbsTol);
  }

  @Test
  public void minutesToSeconds() {
    double rate = 60.0;
    testTimeUnits(TimeUnits.MINUTE, TimeUnits.SECOND, rate, 0.0);
    testTimeUnits(TimeUnits.MINUTE, TimeUnits.SECOND, rate, 6000.0);
    testTimeUnits(TimeUnits.MINUTE, TimeUnits.SECOND, rate, -1034394.0);
    testTimeUnits(TimeUnits.MINUTE, TimeUnits.SECOND, rate, 1.8689235);
  }

  @Test
  public void minutesToHours() {
    double rate = 1.0 / 60.0;
    testTimeUnits(TimeUnits.MINUTE, TimeUnits.HOUR, rate, 0.0);
    testTimeUnits(TimeUnits.MINUTE, TimeUnits.HOUR, rate, 6000.0);
    testTimeUnits(TimeUnits.MINUTE, TimeUnits.HOUR, rate, -1034394.0);
    testTimeUnits(TimeUnits.MINUTE, TimeUnits.HOUR, rate, 1.8689235);
  }

  @Test
  public void hoursToMilliseconds() {
    double rate = 3600000.0;
    testTimeUnits(TimeUnits.HOUR, TimeUnits.MILLISECOND, rate, 0.0);
    testTimeUnits(TimeUnits.HOUR, TimeUnits.MILLISECOND, rate, 6000.0);
    testTimeUnits(TimeUnits.HOUR, TimeUnits.MILLISECOND, rate, -1034394.0);
    testTimeUnits(TimeUnits.HOUR, TimeUnits.MILLISECOND, rate, 1.8689235);
  }

  @Test
  public void hoursToSeconds() {
    double rate = 3600.0;
    testTimeUnits(TimeUnits.HOUR, TimeUnits.SECOND, rate, 0.0);
    testTimeUnits(TimeUnits.HOUR, TimeUnits.SECOND, rate, 6000.0);
    testTimeUnits(TimeUnits.HOUR, TimeUnits.SECOND, rate, -1034394.0);
    testTimeUnits(TimeUnits.HOUR, TimeUnits.SECOND, rate, 1.8689235);
  }

  @Test
  public void hoursToMinutes() {
    double rate = 60.0;
    testTimeUnits(TimeUnits.HOUR, TimeUnits.MINUTE, rate, 0.0);
    testTimeUnits(TimeUnits.HOUR, TimeUnits.MINUTE, rate, 6000.0, 0.0, kAbsTol);
    testTimeUnits(TimeUnits.HOUR, TimeUnits.MINUTE, rate, -1034394.0);
    testTimeUnits(TimeUnits.HOUR, TimeUnits.MINUTE, rate, 1.8689235, 0.0, kAbsTol);
  }
}
