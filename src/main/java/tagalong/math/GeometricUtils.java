/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.math;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

/**
 * Utility functions for geometric math like finding if a location is within a
 * given 2d-shapes.
 */
public class GeometricUtils {
  /**
   * Checks if current position is in range of the target
   *
   * @param curPose           current position of the robot
   * @param yaw               current yaw (position of robot with z-axis)
   * @param targetPose        target position
   * @param xyToleranceM      tolerance for x and y in meters
   * @param thetaToleranceDeg Tolerance in degrees
   * @return True if the current pose is in the specified range
   */
  public static boolean inRange(
      Pose2d curPose,
      Rotation2d yaw,
      Pose2d targetPose,
      double xyToleranceM,
      double thetaToleranceDeg
  ) {
    double thetaDiff = targetPose.getRotation().getDegrees() - yaw.getDegrees();
    return Math.abs(targetPose.getX() - curPose.getX()) <= xyToleranceM
        && Math.abs(targetPose.getY() - curPose.getY()) <= xyToleranceM
        && Math.abs(thetaDiff > 180 ? thetaDiff - 360 : thetaDiff) <= thetaToleranceDeg;
  }

  /**
   * @param X   X-coordinate of the point to check
   * @param Y   Y-coordinate of the point to check
   * @param T1X X-coordinate of the first vertex
   * @param T1Y Y-coordinate of the first vertex
   * @param T2X X-coordinate of the second vertex
   * @param T2Y Y-coordinate of the second vertex
   * @param T3X X-coordinate of the third vertex
   * @param T3Y Y-coordinate of the third vertex
   * @return Whether or not (X, Y) falls within the triangle specified
   */
  public static boolean inTriangle(
      double X, double Y, double T1X, double T1Y, double T2X, double T2Y, double T3X, double T3Y
  ) {
    double t1yMt2y = T1Y - T2Y;
    double t1xMt2x = T1X - T2X;
    double t2yMt3y = T2Y - T3Y;
    double t2xMt3x = T2X - T3X;
    double t1yMt3y = T1Y - T3Y;
    double t1xMt3x = T1X - T3X;

    double Slope12 = t1yMt2y / t1xMt2x;
    double Slope23 = t2yMt3y / t2xMt3x;
    double Slope13 = t1yMt3y / t1xMt3x;

    // check whether to shade up or down. True is up and False is down
    boolean isInequality12Up = t1yMt3y > Slope12 * t1xMt3x;
    boolean isInequality23Up = t1yMt2y > Slope23 * t1xMt2x;
    boolean isInequality13Up = t2yMt3y > Slope13 * t2xMt3x;

    // check whether coordinate is in determined shaded region
    double comparisonValue12 = (Slope12 * (X - T1X)) - (Y - T1Y);
    double comparisonValue23 = (Slope23 * (X - T2X)) - (Y - T2Y);
    double comparisonValue13 = (Slope13 * (X - T3X)) - (Y - T3Y);

    boolean checkLine12 = isInequality12Up ? 0 > comparisonValue12 : 0 < comparisonValue12;
    boolean checkLine23 = isInequality23Up ? 0 > comparisonValue23 : 0 < comparisonValue23;
    boolean checkLine13 = isInequality13Up ? 0 > comparisonValue13 : 0 < comparisonValue13;

    return checkLine13 && checkLine23 && checkLine12;
  }

  /**
   *
   * @param X   X-coordinate of the point to check
   * @param Y   Y-coordinate of the point to check
   * @param R1X X-coordinate of the first vertex
   * @param R1Y Y-coordinate of the first vertex
   * @param R2X X-coordinate of the second vertex
   * @param R2Y Y-coordinate of the second vertex
   * @param R3X X-coordinate of the third vertex
   * @param R3Y Y-coordinate of the third vertex
   * @param R4X X-coordinate of the fourth vertex
   * @param R4Y Y-coordinate of the fourth vertex
   * @return Whether or not (X, Y) falls within the rectangle specified
   */
  public static boolean inRectangle(
      double X,
      double Y,
      double R1X,
      double R1Y,
      double R2X,
      double R2Y,
      double R3X,
      double R3Y,
      double R4X,
      double R4Y
  ) {
    double r1yMr2y = R1Y - R2Y; // 1 and 2
    double r1xMr2x = R1X - R2X;
    double r2yMr3y = R2Y - R3Y; // 2 and 3
    double r2xMr3x = R2X - R3X;
    double r3yMr4y = R3Y - R4Y; // 3 and 4
    double r3xMr4x = R3X - R4X;
    double r1yMr4y = R1Y - R4Y; // 1 and 4
    double r1xMr4x = R1X - R4X;

    double Slope12 = r1yMr2y / r1xMr2x;
    double Slope23 = r2yMr3y / r2xMr3x;
    double Slope34 = r3yMr4y / r3xMr4x;
    double Slope14 = r1yMr4y / r1xMr4x;

    // check whether to shade up or down
    boolean isInequality12Up = r2yMr3y > Slope12 * r2xMr3x;
    boolean isInequality23Up = r3yMr4y > Slope23 * r3xMr4x;
    boolean isInequality34Up = r1yMr4y > Slope34 * r1xMr4x;
    boolean isInequality14Up = r1yMr2y > Slope14 * r1xMr2x;

    // check whether coordinate is in shaded range
    double comparisonValue12 = Slope12 * (X - R1X) - (Y - R1Y);
    double comparisonValue23 = Slope23 * (X - R2X) - (Y - R2Y);
    double comparisonValue34 = Slope34 * (X - R3X) - (Y - R3Y);
    double comparisonValue14 = Slope14 * (X - R4X) - (Y - R4Y);

    boolean checkLine12 = isInequality12Up ? 0 > comparisonValue12 : 0 < comparisonValue12;
    boolean checkLine23 = isInequality23Up ? 0 > comparisonValue23 : 0 < comparisonValue23;
    boolean checkLine34 = isInequality34Up ? 0 > comparisonValue34 : 0 < comparisonValue34;
    boolean checkLine14 = isInequality14Up ? 0 > comparisonValue14 : 0 < comparisonValue14;

    return checkLine12 && checkLine23 && checkLine34 && checkLine14;
  }
}
