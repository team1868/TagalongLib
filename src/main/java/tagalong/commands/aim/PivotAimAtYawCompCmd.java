/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.commands.aim;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import tagalong.commands.base.PivotToDynamicCmd;
import tagalong.math.AlgebraicUtils;
import tagalong.subsystems.TagalongSubsystemBase;
import tagalong.subsystems.micro.augments.PivotAugment;

/**
 * Command that continuously points at a target in 2d space while compensating for the current
 * rotation of the robot. Since this is strictly rotational targeting give robot position as (X, Y)
 * pose
 */
public class PivotAimAtYawCompCmd<T extends TagalongSubsystemBase & PivotAugment>
    extends PivotToDynamicCmd<T> {
  @SuppressWarnings("unused")
  /**
   * Target to aim at
   */
  private final Translation2d _target;
  @SuppressWarnings("unused")
  /**
   * Robot location supplier (x, y, theta)
   */
  private final Supplier<Pose2d> _locationSupplier;

  /**
   * Transform position into an optimal path angle to target, compensates for the current robot yaw
   * while respecting the system's positional limits
   *
   * @param location Current robot location supplier
   * @param pivotPosition Pivot position supplier
   * @param target Target to aim at's location
   * @param pivotMinRot pivots minimum position in rotations
   * @param pivotMaxRot pivots maximum position in rotations
   * @return Optimal path target angle for the pivot
   */
  protected static DoubleSupplier positionTransform(
      Supplier<Pose2d> location,
      DoubleSupplier pivotPosition,
      final Translation2d target,
      double pivotMinRot,
      double pivotMaxRot
  ) {
    return () -> {
      Pose2d curPos = location.get();
      // Robot to target
      double toTargetRot =
          Math.atan((target.getX() - curPos.getX()) / (target.getY() - curPos.getY()));
      // Delta between front of robot and that target, aka the target angle for the system
      double delta = (toTargetRot - curPos.getRotation().getRotations()) % 1.0;
      delta += delta < 0 ? 1.0 : 0;

      // Take the shortest path to that position
      double scopedAngle = AlgebraicUtils.placeInScopeRot(pivotPosition.getAsDouble(), delta);
      if (scopedAngle > pivotMaxRot) {
        scopedAngle -= 1.0;
      }
      if (scopedAngle < pivotMinRot) {
        scopedAngle += 1.0;
      }

      return AlgebraicUtils.clamp(scopedAngle, pivotMinRot, pivotMaxRot);
    };
  }

  /**
   * Minimal constructor with default parameters
   *
   * @param id                Integer ID of the pivot microsystem inside the
   *                          Tagalong Subsystem
   * @param pivot            Tagalong Subsystem containing a pivot microsystem
   * @param positionSupplier  Supplies the current robot position position
   * @param target            Unmoving 2-dimensional target location
   */
  public PivotAimAtYawCompCmd(
      int id, T pivot, Supplier<Pose2d> positionSupplier, Translation2d target
  ) {
    this(id, pivot, positionSupplier, target, true);
  }

  /**
   * Minimal constructor with default parameters
   *
   * @param pivot            Tagalong Subsystem containing a pivot microsystem
   * @param positionSupplier  Supplies the current robot position position
   * @param target            Unmoving 2-dimensional target location
   */
  public PivotAimAtYawCompCmd(T pivot, Supplier<Pose2d> positionSupplier, Translation2d target) {
    this(pivot, positionSupplier, target, true);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Integer ID of the pivot microsystem inside the
   *                          Tagalong Subsystem
   * @param pivot            Tagalong Subsystem containing a pivot microsystem
   * @param positionSupplier  Supplies the current robot position position
   * @param target            Unmoving 2-dimensional target location
   * @param holdPositionAfter If the pivot should hold position when the command completes
   * @param maxVelocityRPS    The maximum velocity of the pivot, in rotations per second, during
   *     this command
   */
  public PivotAimAtYawCompCmd(
      int id,
      T pivot,
      Supplier<Pose2d> positionSupplier,
      Translation2d target,
      boolean holdPositionAfter,
      double maxVelocityRPS
  ) {
    super(
        id,
        pivot,
        positionTransform(
            positionSupplier,
            pivot.getPivot(id)::getPivotPosition,
            target,
            pivot.getPivot(id)._minPositionRot,
            pivot.getPivot(id)._maxPositionRot
        ),
        holdPositionAfter,
        maxVelocityRPS
    );
    _target = target;
    _locationSupplier = positionSupplier;
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param pivot            Tagalong Subsystem containing a pivot microsystem
   * @param positionSupplier  Supplies the current robot position position
   * @param target            Unmoving 2-dimensional target location
   * @param holdPositionAfter If the pivot should hold position when the command completes
   * @param maxVelocityRPS    The maximum velocity of the pivot, in rotations per second, during
   *     this command
   */
  public PivotAimAtYawCompCmd(
      T pivot,
      Supplier<Pose2d> positionSupplier,
      Translation2d target,
      boolean holdPositionAfter,
      double maxVelocityRPS
  ) {
    super(
        pivot,
        positionTransform(
            positionSupplier,
            pivot.getPivot()::getPivotPosition,
            target,
            pivot.getPivot()._minPositionRot,
            pivot.getPivot()._maxPositionRot
        ),
        holdPositionAfter,
        maxVelocityRPS
    );
    _target = target;
    _locationSupplier = positionSupplier;
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Integer ID of the pivot microsystem inside the
   *                          Tagalong Subsystem
   * @param pivot            Tagalong Subsystem containing a pivot microsystem
   * @param positionSupplier  Supplies the current robot position position
   * @param target            Unmoving 2-dimensional target location
   * @param holdPositionAfter If the pivot should hold position when the command completes
   */
  public PivotAimAtYawCompCmd(
      int id,
      T pivot,
      Supplier<Pose2d> positionSupplier,
      Translation2d target,
      boolean holdPositionAfter
  ) {
    this(
        id, pivot, positionSupplier, target, holdPositionAfter, pivot.getPivot(id)._maxVelocityRPS
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param pivot            Tagalong Subsystem containing a pivot microsystem
   * @param positionSupplier  Supplies the current robot position position
   * @param target            Unmoving 2-dimensional target location
   * @param holdPositionAfter If the pivot should hold position when the command completes
   */
  public PivotAimAtYawCompCmd(
      T pivot, Supplier<Pose2d> positionSupplier, Translation2d target, boolean holdPositionAfter
  ) {
    this(pivot, positionSupplier, target, holdPositionAfter, pivot.getPivot()._maxVelocityRPS);
  }
}
