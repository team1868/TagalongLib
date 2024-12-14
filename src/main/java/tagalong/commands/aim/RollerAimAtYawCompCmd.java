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
import tagalong.commands.base.RollToDynamicCmd;
import tagalong.math.AlgebraicUtils;
import tagalong.subsystems.TagalongSubsystemBase;
import tagalong.subsystems.micro.augments.RollerAugment;

/**
 * Command that continuously points at a target in 2d space while compensating for the current
 * rotation of the robot. Since this is strictly rotational targeting give robot position as (X, Y)
 * pose
 */
public class RollerAimAtYawCompCmd<T extends TagalongSubsystemBase & RollerAugment>
    extends RollToDynamicCmd<T> {
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
   * Transform position into an optimal path angle to target, ignores yaw
   *
   * @param location Current robot location supplier
   * @param rollerPosition Roller position supplier
   * @param target Target to aim at's location
   * @return Optimal path target angle for the roller
   */
  protected static DoubleSupplier positionTransform(
      Supplier<Pose2d> location, DoubleSupplier rollerPosition, final Translation2d target
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
      return AlgebraicUtils.placeInScopeRot(rollerPosition.getAsDouble(), delta);
    };
  }

  /**
   * Minimal constructor with default parameters
   *
   * @param id                Integer ID of the roller microsystem inside the
   *                          Tagalong Subsystem
   * @param roller            Tagalong Subsystem containing a roller microsystem
   * @param positionSupplier  Supplies the current robot position position
   * @param target            Unmoving 2-dimensional target location
   */
  public RollerAimAtYawCompCmd(
      int id, T roller, Supplier<Pose2d> positionSupplier, Translation2d target
  ) {
    this(id, roller, positionSupplier, target, true);
  }

  /**
   * Minimal constructor with default parameters
   *
   * @param roller            Tagalong Subsystem containing a roller microsystem
   * @param positionSupplier  Supplies the current robot position position
   * @param target            Unmoving 2-dimensional target location
   */
  public RollerAimAtYawCompCmd(T roller, Supplier<Pose2d> positionSupplier, Translation2d target) {
    this(roller, positionSupplier, target, true);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Integer ID of the roller microsystem inside the
   *                          Tagalong Subsystem
   * @param roller            Tagalong Subsystem containing a roller microsystem
   * @param positionSupplier  Supplies the current robot position position
   * @param target            Unmoving 2-dimensional target location
   * @param holdPositionAfter If the roller should hold position when the command completes
   * @param maxVelocityRPS    The maximum velocity of the roller, in rotations per second, during
   *     this command
   */
  public RollerAimAtYawCompCmd(
      int id,
      T roller,
      Supplier<Pose2d> positionSupplier,
      Translation2d target,
      boolean holdPositionAfter,
      double maxVelocityRPS
  ) {
    super(
        id,
        roller,
        positionTransform(positionSupplier, roller.getRoller(id)::getRollerPosition, target),
        holdPositionAfter,
        maxVelocityRPS
    );
    _target = target;
    _locationSupplier = positionSupplier;
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param roller            Tagalong Subsystem containing a roller microsystem
   * @param positionSupplier  Supplies the current robot position position
   * @param target            Unmoving 2-dimensional target location
   * @param holdPositionAfter If the roller should hold position when the command completes
   * @param maxVelocityRPS    The maximum velocity of the roller, in rotations per second, during
   *     this command
   */
  public RollerAimAtYawCompCmd(
      T roller,
      Supplier<Pose2d> positionSupplier,
      Translation2d target,
      boolean holdPositionAfter,
      double maxVelocityRPS
  ) {
    super(
        roller,
        positionTransform(positionSupplier, roller.getRoller()::getRollerPosition, target),
        holdPositionAfter,
        maxVelocityRPS
    );
    _target = target;
    _locationSupplier = positionSupplier;
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Integer ID of the roller microsystem inside the
   *                          Tagalong Subsystem
   * @param roller            Tagalong Subsystem containing a roller microsystem
   * @param positionSupplier  Supplies the current robot position position
   * @param target            Unmoving 2-dimensional target location
   * @param holdPositionAfter If the roller should hold position when the command completes
   */
  public RollerAimAtYawCompCmd(
      int id,
      T roller,
      Supplier<Pose2d> positionSupplier,
      Translation2d target,
      boolean holdPositionAfter
  ) {
    this(
        id,
        roller,
        positionSupplier,
        target,
        holdPositionAfter,
        roller.getRoller(id)._maxVelocityRPS
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param roller            Tagalong Subsystem containing a roller microsystem
   * @param positionSupplier  Supplies the current robot position position
   * @param target            Unmoving 2-dimensional target location
   * @param holdPositionAfter If the roller should hold position when the command completes
   */
  public RollerAimAtYawCompCmd(
      T roller, Supplier<Pose2d> positionSupplier, Translation2d target, boolean holdPositionAfter
  ) {
    this(roller, positionSupplier, target, holdPositionAfter, roller.getRoller()._maxVelocityRPS);
  }
}
