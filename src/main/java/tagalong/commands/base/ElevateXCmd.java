/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.commands.base;

import tagalong.commands.TagalongCommand;
import tagalong.measurements.Height;
import tagalong.subsystems.TagalongSubsystemBase;
import tagalong.subsystems.micro.Elevator;
import tagalong.subsystems.micro.augments.ElevatorAugment;

/**
 * Command that raises elevator by a target height based on specified
 * parameters.
 */
public class ElevateXCmd<T extends TagalongSubsystemBase & ElevatorAugment>
    extends TagalongCommand {
  /**
   * Elevator microsystem
   */
  private final Elevator _elevator;
  /**
   * Desired additional movement for the elevator relative to its current position
   */
  private final double _relativeMovementM;
  /**
   * Lower tolerance in meters
   */
  private final double _lowerToleranceM;
  /**
   * Upper tolerance in meters
   */
  private final double _upperToleranceM;
  /**
   * Whether or not the elevator must be in tolerance for the command to end
   */
  private final boolean _requireInTolerance;
  /**
   * Duration that the elevator must be in tolerance for the command to end
   */
  private final double _requiredInToleranceDurationS;

  /**
   * Lower bound of the goal tolerance
   */
  private double _lowerBoundM;
  /**
   * Upper bound of the goal tolerance
   */
  private double _upperBoundM;
  /**
   * Whether or not the elevator should holds its position after the command ends
   */
  private boolean _holdPositionAfter;
  /**
   * True if the command has started to move yet
   * False if waiting for a safe state before moving
   */
  private boolean _startedMovement;
  /**
   * Max velocity of the elevator for this specific command
   */
  private double _maxVelocityMPS;
  /**
   * Starting height of the elevator when the command executes
   */
  private double _startHeightM;
  /**
   * Actual goal height of the elevator when the command executes
   */
  private double _goalPositionM;

  @Override
  public void initialize() {
    _startedMovement = false;
    _startHeightM = _elevator.getElevatorHeightM();
    _goalPositionM = _elevator.clampElevatorPosition(_startHeightM + _relativeMovementM);
    _lowerBoundM = _goalPositionM - _lowerToleranceM;
    _upperBoundM = _goalPositionM + _upperToleranceM;
  }

  @Override
  public void execute() {
    if (!_startedMovement) {
      if (_elevator.isSafeToMove()) {
        _elevator.setElevatorProfile(_goalPositionM, 0.0, _maxVelocityMPS);
        _startedMovement = true;
        _elevator.followLastProfile();
      }
    } else {
      _elevator.followLastProfile();
    }
  }

  @Override
  public void end(boolean interrupted) {
    _elevator.setFollowProfile(_holdPositionAfter);
  }

  @Override
  public boolean isFinished() {
    // Command is finished when the profile is finished AND
    // Either the tolerance is bypassed or in tolerance for the desired duration
    return _startedMovement && _elevator.isProfileFinished()
        && (!_requireInTolerance
            || _elevator.checkToleranceTime(
                _elevator.isElevatorInTolerance(_lowerBoundM, _upperBoundM),
                _requiredInToleranceDurationS
            ));
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param relativeMovementM the target amount to move, in meters
   */
  public ElevateXCmd(T elevator, Height relativeMovementM) {
    this(elevator, relativeMovementM, true);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param relativeMovementM the target amount to move, in meters
   */
  public ElevateXCmd(int id, T elevator, Height relativeMovementM) {
    this(id, elevator, relativeMovementM, true);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param relativeMovementM the target amount to move, in meters
   * @param holdPositionAfter whether or not the elevator must hold position after
   *                          reaching target
   */
  public ElevateXCmd(T elevator, Height relativeMovementM, boolean holdPositionAfter) {
    this(elevator, relativeMovementM, holdPositionAfter, elevator.getElevator()._maxVelocityMPS);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param relativeMovementM the target amount to move, in meters
   * @param holdPositionAfter whether or not the elevator must hold position after
   *                          reaching target
   */
  public ElevateXCmd(int id, T elevator, Height relativeMovementM, boolean holdPositionAfter) {
    this(
        id, elevator, relativeMovementM, holdPositionAfter, elevator.getElevator(id)._maxVelocityMPS
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param relativeMovementM the target amount to move, in meters
   * @param holdPositionAfter whether or not the elevator must hold position after
   *                          reaching target
   * @param maxVelocityMPS    maximum velocity, in meters per second, the elevator
   *                          can move
   */
  public ElevateXCmd(
      T elevator, Height relativeMovementM, boolean holdPositionAfter, double maxVelocityMPS
  ) {
    this(
        elevator,
        relativeMovementM,
        holdPositionAfter,
        maxVelocityMPS,
        elevator.getElevator()._defaultElevatorLowerToleranceM,
        elevator.getElevator()._defaultElevatorUpperToleranceM
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param relativeMovementM the target amount to move, in meters
   * @param holdPositionAfter whether or not the elevator must hold position after
   *                          reaching target
   * @param maxVelocityMPS    maximum velocity, in meters per second, the elevator
   *                          can move
   */
  public ElevateXCmd(
      int id, T elevator, Height relativeMovementM, boolean holdPositionAfter, double maxVelocityMPS
  ) {
    this(
        id,
        elevator,
        relativeMovementM,
        holdPositionAfter,
        maxVelocityMPS,
        elevator.getElevator(id)._defaultElevatorLowerToleranceM,
        elevator.getElevator(id)._defaultElevatorUpperToleranceM
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param relativeMovementM the target amount to move, in meters
   * @param holdPositionAfter whether or not the elevator must hold position after
   *                          reaching target
   * @param maxVelocityMPS    maximum velocity, in meters per second, the elevator
   *                          can move
   * @param toleranceM        the desired tolerance for height, in meters
   */
  public ElevateXCmd(
      T elevator,
      Height relativeMovementM,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      double toleranceM
  ) {
    this(elevator, relativeMovementM, holdPositionAfter, maxVelocityMPS, toleranceM, toleranceM);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param relativeMovementM the target amount to move, in meters
   * @param holdPositionAfter whether or not the elevator must hold position after
   *                          reaching target
   * @param maxVelocityMPS    maximum velocity, in meters per second, the elevator
   *                          can move
   * @param toleranceM        the desired tolerance for height, in meters
   */
  public ElevateXCmd(
      int id,
      T elevator,
      Height relativeMovementM,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      double toleranceM
  ) {
    this(
        id, elevator, relativeMovementM, holdPositionAfter, maxVelocityMPS, toleranceM, toleranceM
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param relativeMovementM the target amount to move, in meters
   * @param holdPositionAfter whether or not the elevator must hold position after
   *                          reaching target
   * @param maxVelocityMPS    maximum velocity, in meters per second, the elevator
   *                          can move
   * @param lowerToleranceM   the lower bound for height tolerance, in meters
   * @param upperToleranceM   the upper bound for height tolerance, in meters
   */
  public ElevateXCmd(
      T elevator,
      Height relativeMovementM,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      double lowerToleranceM,
      double upperToleranceM
  ) {
    this(
        elevator,
        relativeMovementM,
        holdPositionAfter,
        maxVelocityMPS,
        lowerToleranceM,
        upperToleranceM,
        -1.0
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param relativeMovementM the target amount to move, in meters
   * @param holdPositionAfter whether or not the elevator must hold position after
   *                          reaching target
   * @param maxVelocityMPS    maximum velocity, in meters per second, the elevator
   *                          can move
   * @param lowerToleranceM   the lower bound for height tolerance, in meters
   * @param upperToleranceM   the upper bound for height tolerance, in meters
   */
  public ElevateXCmd(
      int id,
      T elevator,
      Height relativeMovementM,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      double lowerToleranceM,
      double upperToleranceM
  ) {
    this(
        id,
        elevator,
        relativeMovementM,
        holdPositionAfter,
        maxVelocityMPS,
        lowerToleranceM,
        upperToleranceM,
        -1.0
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param elevator                     Tagalong Subsystem containing an elevator
   *                                     microsystem
   * @param relativeMovement             the target amount to move
   * @param holdPositionAfter            whether or not the elevator must hold
   *                                     position after reaching target
   * @param maxVelocityMPS               maximum velocity, in meters per second,
   *                                     the elevator can move
   * @param lowerToleranceM              the lower bound for height tolerance, in
   *                                     meters
   * @param upperToleranceM              the upper bound for height tolerance, in
   *                                     meters
   * @param requiredInToleranceDurationS the duration (in seconds) the elevator
   *                                     must stay in
   *                                     tolerance
   */
  public ElevateXCmd(
      T elevator,
      Height relativeMovement,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      double lowerToleranceM,
      double upperToleranceM,
      double requiredInToleranceDurationS
  ) {
    this(
        elevator,
        relativeMovement.getHeightM(),
        holdPositionAfter,
        maxVelocityMPS,
        lowerToleranceM,
        upperToleranceM,
        requiredInToleranceDurationS
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                           Tagalong Subsystem containing an elevator
   *                                     microsystem
   * @param elevator                     Tagalong Subsystem containing an elevator
   *                                     microsystem
   * @param relativeMovement             the target amount to move
   * @param holdPositionAfter            whether or not the elevator must hold
   *                                     position after reaching target
   * @param maxVelocityMPS               maximum velocity, in meters per second,
   *                                     the elevator can move
   * @param lowerToleranceM              the lower bound for height tolerance, in
   *                                     meters
   * @param upperToleranceM              the upper bound for height tolerance, in
   *                                     meters
   * @param requiredInToleranceDurationS the duration (in seconds) the elevator
   *                                     must stay in
   *                                     tolerance
   */
  public ElevateXCmd(
      int id,
      T elevator,
      Height relativeMovement,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      double lowerToleranceM,
      double upperToleranceM,
      double requiredInToleranceDurationS
  ) {
    this(
        id,
        elevator,
        relativeMovement.getHeightM(),
        holdPositionAfter,
        maxVelocityMPS,
        lowerToleranceM,
        upperToleranceM,
        requiredInToleranceDurationS
    );
  }

  /**
   * Full constructor, allows for double for goal in rare cases of advance use
   * needing a direct way to interact
   *
   * @param elevator                     Tagalong Subsystem containing an elevator
   *                                     microsystem
   * @param relativeMovementM            the target amount to move, in meters
   * @param holdPositionAfter            whether or not the elevator must hold
   *                                     position after reaching target
   * @param maxVelocityMPS               maximum velocity, in meters per second,
   *                                     the elevator can move
   * @param lowerToleranceM              the lower bound for height tolerance, in
   *                                     meters
   * @param upperToleranceM              the upper bound for height tolerance, in
   *                                     meters
   * @param requiredInToleranceDurationS the duration (in seconds) the elevator
   *                                     must stay in
   *                                     tolerance
   */
  public ElevateXCmd(
      T elevator,
      double relativeMovementM,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      double lowerToleranceM,
      double upperToleranceM,
      double requiredInToleranceDurationS
  ) {
    _elevator = elevator.getElevator();
    _relativeMovementM = relativeMovementM;
    _holdPositionAfter = holdPositionAfter;
    _lowerToleranceM = Math.abs(lowerToleranceM);
    _upperToleranceM = Math.abs(upperToleranceM);
    _maxVelocityMPS = maxVelocityMPS;
    _requiredInToleranceDurationS = requiredInToleranceDurationS;
    _requireInTolerance = _requiredInToleranceDurationS >= 0.0;

    addRequirements(elevator);
  }

  /**
   * Full constructor, allows for double for goal in rare cases of advance use
   * needing a direct way to interact
   *
   * @param id                           Tagalong Subsystem containing an elevator
   *                                     microsystem
   * @param elevator                     Tagalong Subsystem containing an elevator
   *                                     microsystem
   * @param relativeMovementM            the target amount to move, in meters
   * @param holdPositionAfter            whether or not the elevator must hold
   *                                     position after reaching target
   * @param maxVelocityMPS               maximum velocity, in meters per second,
   *                                     the elevator can move
   * @param lowerToleranceM              the lower bound for height tolerance, in
   *                                     meters
   * @param upperToleranceM              the upper bound for height tolerance, in
   *                                     meters
   * @param requiredInToleranceDurationS the duration (in seconds) the elevator
   *                                     must stay in
   *                                     tolerance
   */
  public ElevateXCmd(
      int id,
      T elevator,
      double relativeMovementM,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      double lowerToleranceM,
      double upperToleranceM,
      double requiredInToleranceDurationS
  ) {
    _elevator = elevator.getElevator(id);
    _relativeMovementM = relativeMovementM;
    _holdPositionAfter = holdPositionAfter;
    _lowerToleranceM = _goalPositionM - Math.abs(lowerToleranceM);
    _upperToleranceM = _goalPositionM + Math.abs(upperToleranceM);
    _maxVelocityMPS = maxVelocityMPS;
    _requiredInToleranceDurationS = requiredInToleranceDurationS;
    _requireInTolerance = _requiredInToleranceDurationS >= 0.0;

    addRequirements(elevator);
  }
}
