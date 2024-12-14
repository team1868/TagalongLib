/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class AlgebraicUtilsTest {
  static final double kTol = 1e-6;

  @Test
  public void cart2pol2carTest() {
    var pol = AlgebraicUtils.cart2pol(0.0, 0.0);
    var xy = AlgebraicUtils.pol2cart(pol.getFirst(), pol.getSecond());
    assertEquals(0.0, pol.getFirst());
    assertEquals(0.0, pol.getSecond());
    assertEquals(0.0, xy.getFirst());
    assertEquals(0.0, xy.getSecond());

    pol = AlgebraicUtils.cart2pol(0.0, 1.0);
    xy = AlgebraicUtils.pol2cart(pol.getFirst(), pol.getSecond());
    assertEquals(1.0, pol.getFirst());
    assertEquals(Math.PI / 2.0, pol.getSecond());
    assertEquals(0.0, xy.getFirst(), kTol);
    assertEquals(1.0, xy.getSecond(), kTol);

    pol = AlgebraicUtils.cart2pol(-Math.sqrt(2.0), -Math.sqrt(2.0));
    xy = AlgebraicUtils.pol2cart(pol.getFirst(), pol.getSecond());
    assertEquals(2.0, pol.getFirst());
    assertEquals(-3.0 * Math.PI / 4, pol.getSecond());
    assertEquals(-Math.sqrt(2.0), xy.getFirst(), kTol);
    assertEquals(-Math.sqrt(2.0), xy.getSecond(), kTol);
  }

  @Test
  public void constrainAngleNegPiToPiTest() {
    // Zero
    assertEquals(0.0, AlgebraicUtils.constrainAngleNegPiToPi(0.0), kTol);

    // Bounds
    assertEquals(-Math.PI, AlgebraicUtils.constrainAngleNegPiToPi(-Math.PI), kTol);
    assertEquals(-Math.PI, AlgebraicUtils.constrainAngleNegPiToPi(Math.PI), kTol);
    assertEquals(-Math.PI + 1e-6, AlgebraicUtils.constrainAngleNegPiToPi(Math.PI + 1e-6), kTol);
    assertEquals(Math.PI - 1e-6, AlgebraicUtils.constrainAngleNegPiToPi(Math.PI - 1e-6), kTol);

    // > PI
    assertEquals(0.5, AlgebraicUtils.constrainAngleNegPiToPi(10.0 * Math.PI + 0.5), kTol);
    assertEquals(-0.5, AlgebraicUtils.constrainAngleNegPiToPi(10.0 * Math.PI - 0.5), kTol);
    assertEquals(0.5, AlgebraicUtils.constrainAngleNegPiToPi(-10.0 * Math.PI + 0.5), kTol);
    assertEquals(-0.5, AlgebraicUtils.constrainAngleNegPiToPi(-10.0 * Math.PI - 0.5), kTol);
  }

  @Test
  public void scopedAngleTest() {
    assertEquals(0, AlgebraicUtils.placeInScopeDeg(0, 0), kTol);
    assertEquals(0.5, AlgebraicUtils.placeInScopeDeg(0, 0.5), kTol);
    assertEquals(-0.5, AlgebraicUtils.placeInScopeDeg(0, -0.5), kTol);

    assertEquals(720, AlgebraicUtils.placeInScopeDeg(800, 0), kTol);
    assertEquals(720, AlgebraicUtils.placeInScopeDeg(700, 0), kTol);
    assertEquals(-720, AlgebraicUtils.placeInScopeDeg(-800, 0), kTol);
    assertEquals(-720, AlgebraicUtils.placeInScopeDeg(-700, 0), kTol);

    assertEquals(765, AlgebraicUtils.placeInScopeDeg(800, 45), kTol);
    assertEquals(765, AlgebraicUtils.placeInScopeDeg(700, 45), kTol);

    assertEquals(675, AlgebraicUtils.placeInScopeDeg(800, -45), kTol);
    assertEquals(675, AlgebraicUtils.placeInScopeDeg(700, -45), kTol);

    assertEquals(360, AlgebraicUtils.placeInScopeDeg(300, 360), kTol);
    assertEquals(360, AlgebraicUtils.placeInScopeDeg(300, -360), kTol);
    assertEquals(360, AlgebraicUtils.placeInScopeDeg(300, 720), kTol);
    assertEquals(360, AlgebraicUtils.placeInScopeDeg(300, -720), kTol);
  }

  @Test
  public void clampTest() {
    // Range completely negative
    assertEquals(-3, AlgebraicUtils.clamp(-6, -3, -2));
    assertEquals(-9.7, AlgebraicUtils.clamp(-9.7, -9.7, 0.2));
    assertEquals(-0.6, AlgebraicUtils.clamp(-0.6, -1, -0.1));
    assertEquals(-0.45, AlgebraicUtils.clamp(-0.01, -9999999, -0.45));
    assertEquals(-0.45, AlgebraicUtils.clamp(500, -9999999, -0.45));
    assertEquals(-6, AlgebraicUtils.clamp(-6, -89, -6));
    // Range is on both sides of 0
    assertEquals(-6, AlgebraicUtils.clamp(-200, -6, 7));
    assertEquals(-9.8, AlgebraicUtils.clamp(-9.8, -9.8, 5678));
    assertEquals(0, AlgebraicUtils.clamp(0, -0.7490, 0.4798203));
    assertEquals(628317.589031, AlgebraicUtils.clamp(20988888, -59802, 628317.589031));
    assertEquals(5, AlgebraicUtils.clamp(5, -5, 5));
    // Range completely positive
    assertEquals(6, AlgebraicUtils.clamp(4, 6, 9));
    assertEquals(6, AlgebraicUtils.clamp(-85920, 6, 9));
    assertEquals(8.91, AlgebraicUtils.clamp(8.91, 8.91, 60008592057.84901875));
    assertEquals(829.8928, AlgebraicUtils.clamp(829.8928, 14.7489, 4729073480.489027480));
    assertEquals(60, AlgebraicUtils.clamp(400, 1, 60));
    assertEquals(3.9, AlgebraicUtils.clamp(3.9, 1, 3.9));

    // Extreme case of range is 0
    assertEquals(1, AlgebraicUtils.clamp(-10, 1, 1));
    assertEquals(1, AlgebraicUtils.clamp(1, 1, 1));
    assertEquals(1, AlgebraicUtils.clamp(10, 1, 1));
  }

  @Test
  public void inToleranceTest() {
    // Range completely negative
    assertEquals(false, AlgebraicUtils.inTolerance(-6, -3, -2));
    assertEquals(true, AlgebraicUtils.inTolerance(-9.7, -9.7, 0.2));
    assertEquals(true, AlgebraicUtils.inTolerance(-0.6, -1, -0.1));
    assertEquals(false, AlgebraicUtils.inTolerance(-0.01, -9999999, -0.45));
    assertEquals(false, AlgebraicUtils.inTolerance(500, -9999999, -0.45));
    assertEquals(true, AlgebraicUtils.inTolerance(-6, -89, -6));
    // Range is on both sides of 0
    assertEquals(false, AlgebraicUtils.inTolerance(-200, -6, 7));
    assertEquals(true, AlgebraicUtils.inTolerance(-9.8, -9.8, 5678));
    assertEquals(true, AlgebraicUtils.inTolerance(0, -0.7490, 0.4798203));
    assertEquals(false, AlgebraicUtils.inTolerance(20988888, -59802, 628317.589031));
    assertEquals(true, AlgebraicUtils.inTolerance(5, -5, 5));
    // Range completely positive
    assertEquals(false, AlgebraicUtils.inTolerance(4, 6, 9));
    assertEquals(false, AlgebraicUtils.inTolerance(-85920, 6, 9));
    assertEquals(true, AlgebraicUtils.inTolerance(8.91, 8.91, 60008592057.84901875));
    assertEquals(true, AlgebraicUtils.inTolerance(829.8928, 14.7489, 4729073480.489027480));
    assertEquals(false, AlgebraicUtils.inTolerance(400, 1, 60));
    assertEquals(true, AlgebraicUtils.inTolerance(3.9, 1, 3.9));

    // Extreme case of range is 0
    assertEquals(false, AlgebraicUtils.inTolerance(-10, 1, 1));
    assertEquals(true, AlgebraicUtils.inTolerance(1, 1, 1));
    assertEquals(false, AlgebraicUtils.inTolerance(10, 1, 1));
  }
}
