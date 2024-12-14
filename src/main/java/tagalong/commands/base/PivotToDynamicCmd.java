/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.commands.base;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import tagalong.commands.TagalongCommand;
import tagalong.subsystems.TagalongSubsystemBase;
import tagalong.subsystems.micro.Pivot;
import tagalong.subsystems.micro.augments.PivotAugment;

/**
 * Command that continuously moves the pivot to the goal supplier's target position
 */
public class PivotToDynamicCmd<T extends TagalongSubsystemBase & PivotAugment>
    extends TagalongCommand {
  /**
   * Pivot microsystem
   */
  private final Pivot _pivot;
  /**
   * Supplier for the goal position in rotations
   */
  private final DoubleSupplier _goalSupplierRot;
  /**
   * The condition for starting the command
   */
  private final BooleanSupplier _startCondition;
  /**
   * Whether or not the pivot should maintain its position after reaching the target
   */
  private boolean _holdPositionAfter;
  /**
   * The maximum velocity of the pivot in rotations per second
   */
  private double _maxVelocityRPS;
  /**
   * Whether or not the pivot has started moving
   */
  private boolean _startedMovement;
  /**
   * The goal position in rotations
   */
  private double _goalPositionRot;

  @Override
  public void initialize() {
    _pivot.setHoldPosition(false);
    _startedMovement = false;
  }

  @Override
  public void execute() {
    // if pivot has not started moving, check for legal states
    _goalPositionRot = _pivot.clampPivotPosition(_goalSupplierRot.getAsDouble());
    if (_startedMovement) {
      _pivot.setPivotProfile(_goalPositionRot, 0.0, _maxVelocityRPS);
      _pivot.followLastProfile();
    } else if (_startCondition.getAsBoolean()) {
      _startedMovement = true;
      _pivot.setPivotProfile(_goalPositionRot, 0.0, _maxVelocityRPS);
      _pivot.followLastProfile();
    }
  }

  @Override
  public void end(boolean interrupted) {
    _pivot.setHoldPosition(_holdPositionAfter);
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param id                Integer ID of the pivot microsystem inside the
   *                          Tagalong Subsystem
   * @param pivot             Tagalong Subsystem containing an pivot
   *                          microsystem
   * @param goalSupplierRot   DoubleSupplier for the pivot rotation
   * @param holdPositionAfter If the pivot should hold position when the
   *                          command completes
   * @param maxVelocityRPS    Maximum velocity for the pivot during this command
   * @param startSupplier     BooleanSupplier condition for movement to start,
   *                          defaults to pivot::isSafeToMove
   */
  public PivotToDynamicCmd(
      int id,
      T pivot,
      DoubleSupplier goalSupplierRot,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      BooleanSupplier startSupplier
  ) {
    _pivot = pivot.getPivot(id);
    _goalSupplierRot = goalSupplierRot;
    _holdPositionAfter = holdPositionAfter;
    _maxVelocityRPS = maxVelocityRPS;
    _startCondition = startSupplier;

    addRequirements(pivot);
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param pivot             Tagalong Subsystem containing an pivot
   *                          microsystem
   * @param goalSupplierRot   DoubleSupplier for the pivot rotation
   * @param holdPositionAfter If the pivot should hold position when the
   *                          command completes
   * @param maxVelocityRPS    Maximum velocity for the pivot during this command
   * @param startSupplier     BooleanSupplier condition for movement to start,
   *                          defaults to pivot::isSafeToMove
   */
  public PivotToDynamicCmd(
      T pivot,
      DoubleSupplier goalSupplierRot,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      BooleanSupplier startSupplier
  ) {
    _pivot = pivot.getPivot();
    _goalSupplierRot = goalSupplierRot;
    _holdPositionAfter = holdPositionAfter;
    _maxVelocityRPS = maxVelocityRPS;
    _startCondition = startSupplier;

    addRequirements(pivot);
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param id                Integer ID of the pivot microsystem inside the
   *                          Tagalong Subsystem
   * @param pivot             Tagalong Subsystem containing an pivot
   *                          microsystem
   * @param goalSupplierRot   DoubleSupplier for the pivot rotation
   * @param holdPositionAfter If the pivot should hold position when the
   *                          command completes
   * @param maxVelocityRPS    Maximum velocity for the pivot during this command
   */
  public PivotToDynamicCmd(
      int id,
      T pivot,
      DoubleSupplier goalSupplierRot,
      boolean holdPositionAfter,
      double maxVelocityRPS
  ) {
    this(
        id,
        pivot,
        goalSupplierRot,
        holdPositionAfter,
        maxVelocityRPS,
        pivot.getPivot(id)::isSafeToMove
    );
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param pivot             Tagalong Subsystem containing an pivot
   *                          microsystem
   * @param goalSupplierRot   DoubleSupplier for the pivot rotation
   * @param holdPositionAfter If the pivot should hold position when the
   *                          command completes
   * @param maxVelocityRPS    Maximum velocity for the pivot during this command
   */
  public PivotToDynamicCmd(
      T pivot, DoubleSupplier goalSupplierRot, boolean holdPositionAfter, double maxVelocityRPS
  ) {
    this(pivot, goalSupplierRot, holdPositionAfter, maxVelocityRPS, pivot.getPivot()::isSafeToMove);
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param id                Integer ID of the pivot microsystem inside the
   *                          Tagalong Subsystem
   * @param pivot             Tagalong Subsystem containing an pivot
   *                          microsystem
   * @param goalSupplierRot   DoubleSupplier for the pivot rotation
   * @param holdPositionAfter If the pivot should hold position when the
   *                          command completes
   */
  public PivotToDynamicCmd(
      int id, T pivot, DoubleSupplier goalSupplierRot, boolean holdPositionAfter
  ) {
    this(id, pivot, goalSupplierRot, holdPositionAfter, pivot.getPivot(id)._maxVelocityRPS);
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param pivot             Tagalong Subsystem containing an pivot
   *                          microsystem
   * @param goalSupplierRot   DoubleSupplier for the pivot rotation
   * @param holdPositionAfter If the pivot should hold position when the
   *                          command completes
   *                          command
   */
  public PivotToDynamicCmd(T pivot, DoubleSupplier goalSupplierRot, boolean holdPositionAfter) {
    this(pivot, goalSupplierRot, holdPositionAfter, pivot.getPivot()._maxVelocityRPS);
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param id              Integer ID of the pivot microsystem inside the
   *                        Tagalong Subsystem
   * @param pivot           Tagalong Subsystem containing an pivot microsystem
   * @param goalSupplierRot DoubleSupplier for the pivot rotation
   */
  public PivotToDynamicCmd(int id, T pivot, DoubleSupplier goalSupplierRot) {
    this(id, pivot, goalSupplierRot, true);
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param pivot           Tagalong Subsystem containing an pivot microsystem
   * @param goalSupplierRot DoubleSupplier for the pivot rotation
   */
  public PivotToDynamicCmd(T pivot, DoubleSupplier goalSupplierRot) {
    this(pivot, goalSupplierRot, true);
  }
}
