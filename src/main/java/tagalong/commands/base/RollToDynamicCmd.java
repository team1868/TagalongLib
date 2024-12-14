/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.commands.base;

import java.util.function.DoubleSupplier;
import tagalong.commands.TagalongCommand;
import tagalong.subsystems.TagalongSubsystemBase;
import tagalong.subsystems.micro.Roller;
import tagalong.subsystems.micro.augments.RollerAugment;

/**
 * Command for the roller to continuously move towards the goal supplier's target height
 */
public class RollToDynamicCmd<T extends TagalongSubsystemBase & RollerAugment>
    extends TagalongCommand {
  /**
   * Roller microsystem
   */
  private final Roller _roller;
  /**
   * Supplier for the goal position in rotations
   */
  private final DoubleSupplier _goalSupplierRot;
  /**
   * Whether or not the roller should holds its position after the command ends
   */
  private boolean _holdPositionAfter;
  /**
   * Maximum velocity of the roller in rotations per second
   */
  private double _maxVelocityRPS;
  /**
   * Goal position of the roller in rotations
   */
  private double _goalPositionRot;

  @Override
  public void initialize() {
    _roller.setHoldPosition(false);
    _roller.resetToleranceTimer();
    _roller.setRollerProfile(_goalPositionRot, 0.0, _maxVelocityRPS);
  }

  @Override
  public void execute() {
    _goalPositionRot = _goalSupplierRot.getAsDouble();
    _roller.setRollerProfile(
        _goalPositionRot, 0.0, _maxVelocityRPS, _roller._maxAccelerationRPS2, false
    );
    _roller.followLastProfile();
  }

  @Override
  public void end(boolean interrupted) {
    _roller.setHoldPosition(_holdPositionAfter);
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
   * @param id                Integer ID of the roller microsystem inside the
   *                          Tagalong Subsystem
   * @param roller            Tagalong Subsystem containing an roller
   *                          microsystem
   * @param goalSupplierRot   DoubleSupplier for the roller height
   * @param holdPositionAfter If the roller should hold position when the
   *                          command completes
   * @param maxVelocityRPS    Maximum velocity for the roller during this command
   */
  public RollToDynamicCmd(
      int id,
      T roller,
      DoubleSupplier goalSupplierRot,
      boolean holdPositionAfter,
      double maxVelocityRPS
  ) {
    _roller = roller.getRoller(id);
    _goalSupplierRot = goalSupplierRot;
    _holdPositionAfter = holdPositionAfter;
    _maxVelocityRPS = maxVelocityRPS;

    addRequirements(roller);
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param roller            Tagalong Subsystem containing an roller
   *                          microsystem
   * @param goalSupplierRot   DoubleSupplier for the roller height
   * @param holdPositionAfter If the roller should hold position when the
   *                          command completes
   * @param maxVelocityRPS    Maximum velocity for the roller during this command
   */
  public RollToDynamicCmd(
      T roller, DoubleSupplier goalSupplierRot, boolean holdPositionAfter, double maxVelocityRPS
  ) {
    _roller = roller.getRoller();
    _goalSupplierRot = goalSupplierRot;
    _holdPositionAfter = holdPositionAfter;
    _maxVelocityRPS = maxVelocityRPS;

    addRequirements(roller);
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param id                Integer ID of the roller microsystem inside the
   *                          Tagalong Subsystem
   * @param roller            Tagalong Subsystem containing an roller
   *                          microsystem
   * @param goalSupplierRot   DoubleSupplier for the roller height
   * @param holdPositionAfter If the roller should hold position when the
   *                          command completes
   */
  public RollToDynamicCmd(
      int id, T roller, DoubleSupplier goalSupplierRot, boolean holdPositionAfter
  ) {
    this(id, roller, goalSupplierRot, holdPositionAfter, roller.getRoller(id)._maxVelocityRPS);
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param roller            Tagalong Subsystem containing an roller
   *                          microsystem
   * @param goalSupplierRot   DoubleSupplier for the roller height
   * @param holdPositionAfter If the roller should hold position when the
   *                          command completes
   *                          command
   */
  public RollToDynamicCmd(T roller, DoubleSupplier goalSupplierRot, boolean holdPositionAfter) {
    this(roller, goalSupplierRot, holdPositionAfter, roller.getRoller()._maxVelocityRPS);
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param id              Integer ID of the roller microsystem inside the
   *                        Tagalong Subsystem
   * @param roller          Tagalong Subsystem containing an roller microsystem
   * @param goalSupplierRot DoubleSupplier for the roller height
   */
  public RollToDynamicCmd(int id, T roller, DoubleSupplier goalSupplierRot) {
    this(id, roller, goalSupplierRot, true);
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param roller          Tagalong Subsystem containing an roller microsystem
   * @param goalSupplierRot DoubleSupplier for the roller height
   */
  public RollToDynamicCmd(T roller, DoubleSupplier goalSupplierRot) {
    this(roller, goalSupplierRot, true);
  }
}
