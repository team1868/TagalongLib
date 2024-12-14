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
import tagalong.subsystems.micro.Elevator;
import tagalong.subsystems.micro.augments.ElevatorAugment;

/**
 * Command for the elevator to continuously move towards the goal supplier's target height
 */
public class ElevateToDynamicCmd<T extends TagalongSubsystemBase & ElevatorAugment>
    extends TagalongCommand {
  /**
   * Elevator microsystem
   */
  private final Elevator _elevator;
  /**
   * Supplier for the goal position in meters
   */
  private final DoubleSupplier _goalSupplierM;
  /**
   * Supplier for the condition to start the command
   */
  private final BooleanSupplier _startCondition;
  /**
   * Whether or not the elevator should hold its position after the command ends
   */
  private boolean _holdPositionAfter;
  /**
   * Maximum velocity of the elevator in meters per second
   */
  private double _maxVelocityMPS;
  /**
   * Whether or not the elevator has started moving
   */
  private boolean _startedMovement;
  /**
   * Goal height of the elevator in meters
   */
  private double _goalPositionM;

  @Override
  public void initialize() {
    _elevator.setHoldPosition(false);
    _startedMovement = false;
  }

  @Override
  public void execute() {
    // if elevator has not started moving, check for legal states
    _goalPositionM = _elevator.clampElevatorPosition(_goalSupplierM.getAsDouble());
    if (_startedMovement) {
      _elevator.setElevatorProfile(
          _goalPositionM, 0.0, _maxVelocityMPS, _elevator._maxAccelerationMPS2, false
      );
      _elevator.followLastProfile();
    } else if (_startCondition.getAsBoolean()) {
      _startedMovement = true;
      _elevator.setElevatorProfile(_goalPositionM, 0.0, _maxVelocityMPS);
      _elevator.followLastProfile();
    }
  }

  @Override
  public void end(boolean interrupted) {
    _elevator.setHoldPosition(_holdPositionAfter);
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
   * @param id                Integer ID of the elevator microsystem inside the
   *                          Tagalong Subsystem
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param goalSupplierM     DoubleSupplier for the elevator height
   * @param holdPositionAfter If the elevator should hold position when the
   *                          command completes
   * @param maxVelocityMPS    Maximum velocity for the elevator during this command
   * @param startSupplier     BooleanSupplier condition for movement to start,
   *                          defaults to elevator::isSafeToMove
   */
  public ElevateToDynamicCmd(
      int id,
      T elevator,
      DoubleSupplier goalSupplierM,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      BooleanSupplier startSupplier
  ) {
    _elevator = elevator.getElevator(id);
    _goalSupplierM = goalSupplierM;
    _holdPositionAfter = holdPositionAfter;
    _maxVelocityMPS = maxVelocityMPS;
    _startCondition = startSupplier;

    addRequirements(elevator);
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param goalSupplierM     DoubleSupplier for the elevator height
   * @param holdPositionAfter If the elevator should hold position when the
   *                          command completes
   * @param maxVelocityMPS    Maximum velocity for the elevator during this command
   * @param startSupplier     BooleanSupplier condition for movement to start,
   *                          defaults to elevator::isSafeToMove
   */
  public ElevateToDynamicCmd(
      T elevator,
      DoubleSupplier goalSupplierM,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      BooleanSupplier startSupplier
  ) {
    _elevator = elevator.getElevator();
    _goalSupplierM = goalSupplierM;
    _holdPositionAfter = holdPositionAfter;
    _maxVelocityMPS = maxVelocityMPS;
    _startCondition = startSupplier;

    addRequirements(elevator);
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param id                Integer ID of the elevator microsystem inside the
   *                          Tagalong Subsystem
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param goalSupplierM     DoubleSupplier for the elevator height
   * @param holdPositionAfter If the elevator should hold position when the
   *                          command completes
   * @param maxVelocityMPS    Maximum velocity for the elevator during this command
   */
  public ElevateToDynamicCmd(
      int id,
      T elevator,
      DoubleSupplier goalSupplierM,
      boolean holdPositionAfter,
      double maxVelocityMPS
  ) {
    this(
        id,
        elevator,
        goalSupplierM,
        holdPositionAfter,
        maxVelocityMPS,
        elevator.getElevator(id)::isSafeToMove
    );
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param goalSupplierM     DoubleSupplier for the elevator height
   * @param holdPositionAfter If the elevator should hold position when the
   *                          command completes
   * @param maxVelocityMPS    Maximum velocity for the elevator during this command
   */
  public ElevateToDynamicCmd(
      T elevator, DoubleSupplier goalSupplierM, boolean holdPositionAfter, double maxVelocityMPS
  ) {
    this(
        elevator,
        goalSupplierM,
        holdPositionAfter,
        maxVelocityMPS,
        elevator.getElevator()::isSafeToMove
    );
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param id                Integer ID of the elevator microsystem inside the
   *                          Tagalong Subsystem
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param goalSupplierM     DoubleSupplier for the elevator height
   * @param holdPositionAfter If the elevator should hold position when the
   *                          command completes
   */
  public ElevateToDynamicCmd(
      int id, T elevator, DoubleSupplier goalSupplierM, boolean holdPositionAfter
  ) {
    this(id, elevator, goalSupplierM, holdPositionAfter, elevator.getElevator(id)._maxVelocityMPS);
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param goalSupplierM     DoubleSupplier for the elevator height
   * @param holdPositionAfter If the elevator should hold position when the
   *                          command completes
   *                          command
   */
  public ElevateToDynamicCmd(T elevator, DoubleSupplier goalSupplierM, boolean holdPositionAfter) {
    this(elevator, goalSupplierM, holdPositionAfter, elevator.getElevator()._maxVelocityMPS);
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param id            Integer ID of the elevator microsystem inside the
   *                      Tagalong Subsystem
   * @param elevator      Tagalong Subsystem containing an elevator microsystem
   * @param goalSupplierM DoubleSupplier for the elevator height
   */
  public ElevateToDynamicCmd(int id, T elevator, DoubleSupplier goalSupplierM) {
    this(id, elevator, goalSupplierM, true);
  }

  /**
   * Continuously move to the double suppliers target position. The function is
   * continuous, it has no end condition, so the user must interrupt the command
   * or decorate the command with an end condition. Useful for dynamic movements
   * that are dependent on sensor readings, field conditions, or driver
   * configurations.
   *
   * @param elevator      Tagalong Subsystem containing an elevator microsystem
   * @param goalSupplierM DoubleSupplier for the elevator height
   */
  public ElevateToDynamicCmd(T elevator, DoubleSupplier goalSupplierM) {
    this(elevator, goalSupplierM, true);
  }
}
