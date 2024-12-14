/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.math;

import edu.wpi.first.math.MatBuilder;
import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.Nat;
import edu.wpi.first.math.Pair;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N2;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.numbers.N4;

/**
 * Algebraic functions
 */
public class AlgebraicUtils {
  /**
   * @param x Cartesian x coordinate
   * @param y Cartesian y coordinate
   * @return pair representing the polar coordinate radius and theta in radians
   */
  public static Pair<Double, Double> cart2pol(final double x, final double y) {
    final double r = Math.sqrt(x * x + y * y);
    final double theta = Math.atan2(y, x);
    return new Pair<>(r, theta);
  }

  /**
   * Converts polar coordinates to Cartesian coordinates.
   *
   * @param r     radius in polar coordinates
   * @param theta angle in radians
   * @return pair of Cartesian coordinates x and y
   */
  public static Pair<Double, Double> pol2cart(final double r, final double theta) {
    final double x = r * Math.cos(theta);
    final double y = r * Math.sin(theta);
    return new Pair<>(x, y);
  }

  /**
   * Constrains an angle to be within [-pi, pi).
   *
   * @param angle angle in radians
   * @return angle in radians that is between [-pi,pi]
   */
  public static double constrainAngleNegPiToPi(final double angle) {
    double x = (angle + Math.PI) % (2.0 * Math.PI);
    if (x < 0.0) {
      x += 2.0 * Math.PI;
    }
    return x - Math.PI;
  }

  /**
   * Scopes the angle to be at most pi radians away from the reference angle
   *
   * @param referenceAngleRad Base of the scope to place the angle in
   * @param desiredAngleRad   Angle that should be placed in radians
   * @return |angle| placed within within [-pi, pi) of |referenceAngle|.
   */
  public static double placeInScopeRad(
      final double referenceAngleRad, final double desiredAngleRad
  ) {
    return referenceAngleRad + constrainAngleNegPiToPi(desiredAngleRad - referenceAngleRad);
  }

  /**
   * Scopes the angle to be at most 180 degrees away from the reference angle
   *
   * @param referenceAngleDeg Base of the scope to place the angle in
   * @param desiredAngleDeg   Angle that should be placed in degrees
   * @return |angle| placed within within [-180, 180) of |referenceAngle|.
   */
  public static double placeInScopeDeg(
      final double referenceAngleDeg, final double desiredAngleDeg
  ) {
    return Math.toDegrees(
        placeInScopeRad(Math.toRadians(referenceAngleDeg), Math.toRadians(desiredAngleDeg))
    );
  }

  /**
   * Returns the desired angle in the closest scope of the current angle
   *
   * @param referenceAngleRot Current angle in rotations
   * @param desiredAngleRot Target angle in rotations
   * @return Scoped angle in rotations
   */
  public static double placeInScopeRot(
      final double referenceAngleRot, final double desiredAngleRot
  ) {
    double delta = (desiredAngleRot - referenceAngleRot) % 1.0;
    if (delta > 0.5) {
      delta += -1.0;
    } else if (delta < -0.5) {
      delta += 1.0;
    }
    return referenceAngleRot + delta;
  }

  /**
   * Rotates a vector by a given angle.
   *
   * @param vector initial vector
   * @param theta  desired angle rotation
   * @return rotated vector as a matrix
   */
  public static Matrix<N2, N1> rotateVector(final Matrix<N2, N1> vector, final double theta) {
    final double x1 = vector.get(0, 0);
    final double y1 = vector.get(1, 0);
    final double c = Math.cos(theta);
    final double s = Math.sin(theta);
    final double x2 = c * x1 - s * y1;
    final double y2 = s * x1 + c * y1;
    return MatBuilder.fill(Nat.N2(), Nat.N1(), x2, y2);
  }

  /**
   * Returns double z component from the 4*4 matrix.
   *
   * @param matrix 4*4 Matrix
   * @return double Z component from the matrix
   */
  public static double extractTFMatrixZ(final Matrix<N4, N4> matrix) {
    return matrix.get(2, 3);
  }

  /**
   * Returns double Y rotation form the 4*4 matrix.
   *
   * @param matrix 4*4 matrix
   * @return double Y rotation from the matrix
   */
  public static double extractTFMatrixYRot(final Matrix<N4, N4> matrix) {
    return Math.atan2(
        -matrix.get(2, 0),
        Math.sqrt(matrix.get(2, 1) * matrix.get(2, 1) + matrix.get(2, 2) * matrix.get(2, 2))
    );
  }

  /**
   * Converts the 2D pose into a matrix with a rotation around the z-axis.
   *
   * @param pose 2D position of the robot on the field
   * @return Transformation matrix from Pose2d
   */
  public static Matrix<N4, N4> pose2DToZRotTFMatrix(Pose2d pose) {
    return makeZRotTFMatrix(pose.getX(), pose.getY(), 0.0, pose.getRotation().getRadians());
  }

  /**
   * Converts matrix back into 2D pose.
   *
   * @param zRotTFMatrix a 4x4 matrix representing a 2D affine transformation with translation and
   *     rotation
   * @return Pose2d
   */
  public static Pose2d zRotTFMatrixToPose(final Matrix<N4, N4> zRotTFMatrix) {
    return new Pose2d(
        zRotTFMatrix.get(0, 3),
        zRotTFMatrix.get(1, 3),
        new Rotation2d(Math.atan2(zRotTFMatrix.get(1, 0), zRotTFMatrix.get(0, 0)))
    );
  }

  /**
   * Transforms a 3D point into a given frame of reference using matrix
   * operations.
   *
   * @param point 3x1 column matrix representing the coordinates of a point.
   *              ---not sure if this is
   *              right
   * @param frame 4x4 matrix representing the pose
   * @return Transformed point as matrix
   */
  public static Matrix<N3, N1> getPointInFrame(Matrix<N3, N1> point, final Matrix<N4, N4> frame) {
    final Matrix<N4, N1> pointMod =
        MatBuilder.fill(Nat.N4(), Nat.N1(), point.get(0, 0), point.get(1, 0), point.get(2, 0), 1);
    return frame.inv().times(pointMod).block(Nat.N3(), Nat.N1(), 0, 0);
  }

  /**
   * Creates a matrix about with rotation about the y-axis.
   *
   * @param x     translation along x-axis
   * @param y     translation along y-axis
   * @param z     translation along z-axis
   * @param theta angle of rotation about y-axis
   * @return transformation matrix for Y rotation
   */
  public static Matrix<N4, N4> makeYRotTFMatrix(
      final double x, final double y, final double z, final double theta
  ) {
    return MatBuilder.fill(
        Nat.N4(),
        Nat.N4(),
        Math.cos(theta),
        0,
        Math.sin(theta),
        x,
        0,
        1,
        0,
        y,
        -Math.sin(theta),
        0,
        Math.cos(theta),
        z,
        0,
        0,
        0,
        1
    );
  }

  /**
   *
   * @param x     translation along x-axis
   * @param y     translation along y-axis
   * @param z     translation along z-axis
   * @param theta angle of rotation about Z-axis
   * @return Transformation matrix for Z rotation
   */
  public static Matrix<N4, N4> makeZRotTFMatrix(
      final double x, final double y, final double z, final double theta
  ) {
    return MatBuilder.fill(
        Nat.N4(),
        Nat.N4(),
        Math.cos(theta),
        -Math.sin(theta),
        0,
        x,
        Math.sin(theta),
        Math.cos(theta),
        0,
        y,
        0,
        0,
        1,
        z,
        0,
        0,
        0,
        1
    );
  }

  private static final double kEps = 1E-9;

  /**
   * Obtain a new Pose2d from a (constant curvature) velocity. See:
   * https://github.com/strasdat/Sophus/blob/master/sophus/se2.hpp. Borrowed from
   * 254:
   * https://github.com/Team254/FRC-2022-Public/blob/main/src/main/java/com/team254/lib/geometry/Pose2d.java
   *
   * @param delta robot change
   * @return New Pose2d from velocity
   */
  public static Pose2d exp(final Twist2d delta) {
    final double sin_theta = Math.sin(delta.dtheta);
    final double cos_theta = Math.cos(delta.dtheta);
    final double s = Math.abs(delta.dtheta) < kEps ? 1.0 - 1.0 / 6.0 * delta.dtheta * delta.dtheta
                                                   : sin_theta / delta.dtheta;
    final double c =
        Math.abs(delta.dtheta) < kEps ? 0.5 * delta.dtheta : (1.0 - cos_theta) / delta.dtheta;
    return new Pose2d(
        new Translation2d(delta.dx * s - delta.dy * c, delta.dx * c + delta.dy * s),
        new Rotation2d(cos_theta, sin_theta)
    );
  }

  /**
   * Logical inverse of the above. Borrowed from 254:
   * https://github.com/Team254/FRC-2022-Public/blob/main/src/main/java/com/team254/lib/geometry/Pose2d.java
   *
   * @param transform Pose to derive velocity from
   * @return Twist2d representing transformation
   */
  public static Twist2d log(final Pose2d transform) {
    final double dtheta = transform.getRotation().getRadians();
    final double half_dtheta = 0.5 * dtheta;
    final double cos_minus_one = Math.cos(transform.getRotation().getRadians()) - 1.0;
    final double halftheta_by_tan_of_halfdtheta = Math.abs(cos_minus_one) < kEps
        ? 1.0 - 1.0 / 12.0 * dtheta * dtheta
        : -(half_dtheta * Math.sin(transform.getRotation().getRadians())) / cos_minus_one;
    final Translation2d translation_part = transform.getTranslation().rotateBy(
        new Rotation2d(halftheta_by_tan_of_halfdtheta, -half_dtheta)
    );
    return new Twist2d(translation_part.getX(), translation_part.getY(), dtheta);
  }

  /**
   * Returns a modulo result that matches the C++ rather than Java implementation
   *
   * @param num   Numerator
   * @param denom Denominator
   * @return modulo result
   */
  public static double cppMod(double num, double denom) {
    return ((num % denom) + denom) % denom;
  }

  /**
   * Clamps a target value between the minimum and maximum values, ensuring the
   * value is in the given range.
   *
   * @param target target value
   * @param min    min value
   * @param max    max value
   * @return the target value rounded to be in range (min, max)
   */
  public static double clamp(double target, double min, double max) {
    return Math.max(min, Math.min(max, target));
  }

  /**
   *
   * Units must be consistent between all 3 values
   *
   * @param position   actual position of the mechanism
   * @param lowerBound minimum position of the mechanism
   * @param upperBound maximum position of the mechanism
   * @return True if position is greater than or equal to the lowerBound and less
   *         than or equal to the upperBound.
   *         False otherwise, meaning the position falls out of the range
   */
  public static boolean inTolerance(double position, double lowerBound, double upperBound) {
    return (position >= lowerBound) && (position <= upperBound);
  }
}
