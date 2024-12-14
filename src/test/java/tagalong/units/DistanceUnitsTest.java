/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.units;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DistanceUnitsTest {
  static final double kTol = 3e-6;
  static final double kAbsTol = 1e-12;

  private static void testDistanceUnits(
      DistanceUnits from, DistanceUnits to, double conversionRate, double distance
  ) {
    double converted = from.convertX(distance, to);
    assertEquals(distance * conversionRate, converted);
  }

  private static void testDistanceUnits(
      DistanceUnits from,
      DistanceUnits to,
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

  @Test
  public void inchesToFeet() {
    double rate = 1.0 / 12.0;
    testDistanceUnits(DistanceUnits.INCH, DistanceUnits.FEET, rate, 0.0);
    testDistanceUnits(DistanceUnits.INCH, DistanceUnits.FEET, rate, -1.7976931348623157, kTol, 0.0);
    testDistanceUnits(DistanceUnits.INCH, DistanceUnits.FEET, rate, -1868.0123456789, kTol, 0.0);
    testDistanceUnits(
        DistanceUnits.INCH, DistanceUnits.FEET, rate, 179769313486231570000.0, kTol, 0.0
    );
  }
  @Test
  public void inchesToMeters() {
    double rate = 0.0254;
    testDistanceUnits(DistanceUnits.INCH, DistanceUnits.METER, rate, 0.0);
    testDistanceUnits(
        DistanceUnits.INCH, DistanceUnits.METER, rate, -1.7976931348623157, kTol, 0.0
    );
    testDistanceUnits(DistanceUnits.INCH, DistanceUnits.METER, rate, -1868.0123456789, kTol, 0.0);
    testDistanceUnits(
        DistanceUnits.INCH, DistanceUnits.METER, rate, 179769313486231570000.0, kTol, 0.0
    );
  }

  @Test
  public void feetToInches() {
    double rate = 12.0;
    testDistanceUnits(DistanceUnits.FEET, DistanceUnits.INCH, rate, 0.0);
    testDistanceUnits(DistanceUnits.FEET, DistanceUnits.INCH, rate, -1.7976931348623157, kTol, 0.0);
    testDistanceUnits(DistanceUnits.FEET, DistanceUnits.INCH, rate, -1868.0123456789, kTol, 0.0);
    testDistanceUnits(
        DistanceUnits.FEET, DistanceUnits.INCH, rate, 179769313486231570000.0, kTol, 0.0
    );
  }
  @Test
  public void feetToMeters() {
    double rate = 0.3048;
    testDistanceUnits(DistanceUnits.FEET, DistanceUnits.METER, rate, 0.0);
    testDistanceUnits(
        DistanceUnits.FEET, DistanceUnits.METER, rate, -1.7976931348623157, kTol, 0.0
    );
    testDistanceUnits(DistanceUnits.FEET, DistanceUnits.METER, rate, -1868.0123456789, kTol, 0.0);
    testDistanceUnits(
        DistanceUnits.FEET, DistanceUnits.METER, rate, 179769313486231570000.0, kTol, 0.0
    );
  }

  @Test
  public void metersToInches() {
    double rate = 39.3701;
    testDistanceUnits(DistanceUnits.METER, DistanceUnits.INCH, rate, 0.0);
    testDistanceUnits(
        DistanceUnits.METER, DistanceUnits.INCH, rate, -1.7976931348623157, kTol, 0.0
    );
    testDistanceUnits(DistanceUnits.METER, DistanceUnits.INCH, rate, -1868.0123456789, kTol, 0.0);
    testDistanceUnits(
        DistanceUnits.METER, DistanceUnits.INCH, rate, 179769313486231570000.0, kTol, 0.0
    );
  }
  @Test
  public void metersToFeet() {
    double rate = 3.28084;
    testDistanceUnits(DistanceUnits.METER, DistanceUnits.FEET, rate, 0.0);
    testDistanceUnits(
        DistanceUnits.METER, DistanceUnits.FEET, rate, -1.7976931348623157, kTol, 0.0
    );
    testDistanceUnits(DistanceUnits.METER, DistanceUnits.FEET, rate, -1868.0123456789, kTol, 0.0);
    testDistanceUnits(
        DistanceUnits.METER, DistanceUnits.FEET, rate, 179769313486231570000.0, kTol, 0.0
    );
  }

  @Test
  public void degreesToRadians() {
    double rate = Math.PI / 180.0;
    testDistanceUnits(DistanceUnits.DEGREE, DistanceUnits.RADIAN, rate, 0.0);
    testDistanceUnits(
        DistanceUnits.DEGREE, DistanceUnits.RADIAN, rate, -1.7976931348623157, kTol, 0.0
    );
    testDistanceUnits(
        DistanceUnits.DEGREE, DistanceUnits.RADIAN, rate, -1868.0123456789, kTol, 0.0
    );
    testDistanceUnits(
        DistanceUnits.DEGREE, DistanceUnits.RADIAN, rate, 179769313486231570000.0, kTol, 0.0
    );
  }
  @Test
  public void degreesToRotations() {
    double rate = 1 / 360.0;
    testDistanceUnits(DistanceUnits.DEGREE, DistanceUnits.ROTATION, rate, 0.0);
    testDistanceUnits(
        DistanceUnits.DEGREE, DistanceUnits.ROTATION, rate, -1.7976931348623157, kTol, 0.0
    );
    testDistanceUnits(
        DistanceUnits.DEGREE, DistanceUnits.ROTATION, rate, -1868.0123456789, kTol, 0.0
    );
    testDistanceUnits(
        DistanceUnits.DEGREE, DistanceUnits.ROTATION, rate, 179769313486231570000.0, kTol, 0.0
    );
  }

  @Test
  public void radiansToDegrees() {
    double rate = 180.0 / Math.PI;
    testDistanceUnits(DistanceUnits.RADIAN, DistanceUnits.DEGREE, rate, 0.0);
    testDistanceUnits(
        DistanceUnits.RADIAN, DistanceUnits.DEGREE, rate, -1.7976931348623157, kTol, 0.0
    );
    testDistanceUnits(
        DistanceUnits.RADIAN, DistanceUnits.DEGREE, rate, -1868.0123456789, kTol, 0.0
    );
    testDistanceUnits(
        DistanceUnits.RADIAN, DistanceUnits.DEGREE, rate, 179769313486231570000.0, kTol, 0.0
    );
  }
  @Test
  public void radiansToRotations() {
    double rate = 1 / (2 * Math.PI);
    testDistanceUnits(DistanceUnits.RADIAN, DistanceUnits.ROTATION, rate, 0.0);
    testDistanceUnits(
        DistanceUnits.RADIAN, DistanceUnits.ROTATION, rate, -1.7976931348623157, kTol, 0.0
    );
    testDistanceUnits(
        DistanceUnits.RADIAN, DistanceUnits.ROTATION, rate, -1868.0123456789, kTol, 0.0
    );
    testDistanceUnits(
        DistanceUnits.RADIAN, DistanceUnits.ROTATION, rate, 179769313486231570000.0, kTol, 0.0
    );
  }

  @Test
  public void rotationsToDegrees() {
    double rate = 360.0;
    testDistanceUnits(DistanceUnits.ROTATION, DistanceUnits.DEGREE, rate, 0.0);
    testDistanceUnits(
        DistanceUnits.ROTATION, DistanceUnits.DEGREE, rate, -1.7976931348623157, kTol, 0.0
    );
    testDistanceUnits(
        DistanceUnits.ROTATION, DistanceUnits.DEGREE, rate, -1868.0123456789, kTol, 0.0
    );
    testDistanceUnits(
        DistanceUnits.ROTATION, DistanceUnits.DEGREE, rate, 179769313486231570000.0, kTol, 0.0
    );
  }
  @Test
  public void rotationsToRadians() {
    double rate = 2 * Math.PI;
    testDistanceUnits(DistanceUnits.ROTATION, DistanceUnits.RADIAN, rate, 0.0);
    testDistanceUnits(
        DistanceUnits.ROTATION, DistanceUnits.RADIAN, rate, -1.7976931348623157, kTol, 0.0
    );
    testDistanceUnits(
        DistanceUnits.ROTATION, DistanceUnits.RADIAN, rate, -1868.0123456789, kTol, 0.0
    );
    testDistanceUnits(
        DistanceUnits.ROTATION, DistanceUnits.RADIAN, rate, 179769313486231570000.0, kTol, 0.0
    );
  }

  @Test
  public void rotationalToLinear() {
    // assertThrowsExactly(null, null);
  }
  @Test
  public void linearToRotational() {}
}
