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
 * Base Command for linear system movement, moves system to specified height.
 */
public class ElevateToCmd<T extends TagalongSubsystemBase & ElevatorAugment>
    extends TagalongCommand {
  /**
   * The subsystem's underlying Tagalong Elevator that the ElevatorRaiseToCommand
   * command acts on and operates.
   */
  private final Elevator _elevator;
  /**
   * Lower bound of the goal tolerance
   */
  private final double _lowerBoundM;
  /**
   * Upper bound of the goal tolerance
   */
  private final double _upperBoundM;
  /**
   * Target height for this command
   */
  private final double _goalPositionM;
  /**
   * True if the elevator needs to be in tolerance for the command to complete.
   * False if only the profile needs to complete for the command to complete.
   */
  private final boolean _requireInTolerance;
  /**
   * Duration the elevator must be in tolerance before the command completes. If
   * set to 0.0 or more, require in tolerance will be automatically set to True.
   */
  private final double _requiredInToleranceDurationS;
  /**
   * True if the elevator should actively hold position after the command
   * completes.
   * False otherwise.
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

  @Override
  public void initialize() {
    _elevator.setHoldPosition(false);
    _startedMovement = false;
    _elevator.resetToleranceTimer();
  }

  @Override
  public void execute() {
    // if elevator has not started moving, check for legal states
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
    _elevator.setHoldPosition(_holdPositionAfter);
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
   * @param elevator      Tagalong Subsystem containing an elevator microsystem
   * @param goalPositionM The target height for the elevator, in meters
   */
  public ElevateToCmd(T elevator, Height goalPositionM) {
    this(elevator, goalPositionM, true);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id            Integer ID of the elevator microsystem inside the
   *                      Tagalong Subsystem
   * @param elevator      Tagalong Subsystem containing an elevator microsystem
   * @param goalPositionM The target height for the elevator, in meters
   */
  public ElevateToCmd(int id, T elevator, Height goalPositionM) {
    this(id, elevator, goalPositionM, true);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param goalPositionM     The target height for the elevator, in meters
   * @param holdPositionAfter If the elevator should hold position when the
   *                          command completes
   */
  public ElevateToCmd(T elevator, Height goalPositionM, boolean holdPositionAfter) {
    this(elevator, goalPositionM, holdPositionAfter, elevator.getElevator()._maxVelocityMPS);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Integer ID of the elevator microsystem inside the
   *                          Tagalong Subsystem
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param goalPositionM     The target height for the elevator, in meters
   * @param holdPositionAfter If the elevator should hold position when the
   *                          command completes
   */
  public ElevateToCmd(int id, T elevator, Height goalPositionM, boolean holdPositionAfter) {
    this(id, elevator, goalPositionM, holdPositionAfter, elevator.getElevator(id)._maxVelocityMPS);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param goalPositionM     The target height for the elevator, in meters
   * @param holdPositionAfter If the elevator should hold position when the
   *                          command completes
   * @param maxVelocityMPS    maximum velocity, in meters per second, the elevator
   *                          can move
   */
  public ElevateToCmd(
      T elevator, Height goalPositionM, boolean holdPositionAfter, double maxVelocityMPS
  ) {
    this(
        elevator,
        goalPositionM,
        holdPositionAfter,
        maxVelocityMPS,
        elevator.getElevator()._defaultElevatorLowerToleranceM,
        elevator.getElevator()._defaultElevatorUpperToleranceM
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Integer ID of the elevator microsystem inside the
   *                          Tagalong Subsystem
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param goalPositionM     The target height for the elevator, in meters
   * @param holdPositionAfter If the elevator should hold position when the
   *                          command completes
   * @param maxVelocityMPS    maximum velocity, in meters per second, the elevator
   *                          can move
   */
  public ElevateToCmd(
      int id, T elevator, Height goalPositionM, boolean holdPositionAfter, double maxVelocityMPS
  ) {
    this(
        id,
        elevator,
        goalPositionM,
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
   * @param goalPositionM     The target height for the elevator, in meters
   * @param holdPositionAfter If the elevator should hold position when the
   *                          command completes
   * @param maxVelocityMPS    maximum velocity, in meters per second, the elevator
   *                          can move
   * @param toleranceM        the desired tolerance for height, in meters
   */
  public ElevateToCmd(
      T elevator,
      Height goalPositionM,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      double toleranceM
  ) {
    this(elevator, goalPositionM, holdPositionAfter, maxVelocityMPS, toleranceM, toleranceM);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Integer ID of the elevator microsystem inside the
   *                          Tagalong Subsystem
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param goalPositionM     The target height for the elevator, in meters
   * @param holdPositionAfter If the elevator should hold position when the
   *                          command completes
   * @param maxVelocityMPS    maximum velocity, in meters per second, the elevator
   *                          can move
   * @param toleranceM        the desired tolerance for height, in meters
   */
  public ElevateToCmd(
      int id,
      T elevator,
      Height goalPositionM,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      double toleranceM
  ) {
    this(id, elevator, goalPositionM, holdPositionAfter, maxVelocityMPS, toleranceM, toleranceM);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param goalPositionM     The target height for the elevator, in meters
   * @param holdPositionAfter If the elevator should hold position when the
   *                          command completes
   * @param maxVelocityMPS    maximum velocity, in meters per second, the elevator
   *                          can move
   * @param lowerToleranceM   the lower bound for height tolerance, in meters
   * @param upperToleranceM   the upper bound for height tolerance, in meters
   */
  public ElevateToCmd(
      T elevator,
      Height goalPositionM,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      double lowerToleranceM,
      double upperToleranceM
  ) {
    this(
        elevator,
        goalPositionM,
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
   * @param id                Integer ID of the elevator microsystem inside the
   *                          Tagalong Subsystem
   * @param elevator          Tagalong Subsystem containing an elevator
   *                          microsystem
   * @param goalPositionM     The target height for the elevator, in meters
   * @param holdPositionAfter If the elevator should hold position when the
   *                          command completes
   * @param maxVelocityMPS    maximum velocity, in meters per second, the elevator
   *                          can move
   * @param lowerToleranceM   the lower bound for height tolerance, in meters
   * @param upperToleranceM   the upper bound for height tolerance, in meters
   */
  public ElevateToCmd(
      int id,
      T elevator,
      Height goalPositionM,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      double lowerToleranceM,
      double upperToleranceM
  ) {
    this(
        id,
        elevator,
        goalPositionM,
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
   * @param goalPosition                 The target height for the elevator
   * @param holdPositionAfter            If the elevator should hold position when
   *                                     the command completes
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
  public ElevateToCmd(
      T elevator,
      Height goalPosition,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      double lowerToleranceM,
      double upperToleranceM,
      double requiredInToleranceDurationS
  ) {
    this(
        elevator,
        goalPosition.getHeightM(),
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
   * @param id                           Integer ID of the elevator microsystem
   *                                     inside the Tagalong Subsystem
   * @param elevator                     Tagalong Subsystem containing an elevator
   *                                     microsystem
   * @param goalPosition                 The target height for the elevator
   * @param holdPositionAfter            If the elevator should hold position when
   *                                     the command completes
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
  public ElevateToCmd(
      int id,
      T elevator,
      Height goalPosition,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      double lowerToleranceM,
      double upperToleranceM,
      double requiredInToleranceDurationS
  ) {
    this(
        id,
        elevator,
        goalPosition.getHeightM(),
        holdPositionAfter,
        maxVelocityMPS,
        lowerToleranceM,
        upperToleranceM,
        requiredInToleranceDurationS
    );
  }

  /**
   * Full constructor, with the below parameters. Allows for double for goal
   * in rare cases of advance use needing a direct way to interact
   *
   * @param elevator                     Tagalong Subsystem containing an elevator
   *                                     microsystem
   * @param goalPositionM                The target height for the elevator, in
   *                                     meters, as a double
   * @param holdPositionAfter            If the elevator should hold position when
   *                                     the command completes
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
  public ElevateToCmd(
      T elevator,
      double goalPositionM,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      double lowerToleranceM,
      double upperToleranceM,
      double requiredInToleranceDurationS
  ) {
    _elevator = elevator.getElevator();
    _goalPositionM = goalPositionM;
    _holdPositionAfter = holdPositionAfter;
    _lowerBoundM = _goalPositionM - Math.abs(lowerToleranceM);
    _upperBoundM = _goalPositionM + Math.abs(upperToleranceM);
    _maxVelocityMPS = maxVelocityMPS;
    _requiredInToleranceDurationS = requiredInToleranceDurationS;
    _requireInTolerance = _requiredInToleranceDurationS >= 0.0;

    addRequirements(elevator);
  }

  /**
   * Full constructor, with the below parameters. Allows for double for goal
   * in rare cases of advance use needing a direct way to interact
   *
   * @param id                           Integer ID of the elevator microsystem
   *                                     inside the Tagalong Subsystem
   * @param elevator                     Tagalong Subsystem containing an elevator
   *                                     microsystem
   * @param goalPositionM                The target height for the elevator, in
   *                                     meters, as a double.
   * @param holdPositionAfter            If the elevator should hold position when
   *                                     the command completes
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
  public ElevateToCmd(
      int id,
      T elevator,
      double goalPositionM,
      boolean holdPositionAfter,
      double maxVelocityMPS,
      double lowerToleranceM,
      double upperToleranceM,
      double requiredInToleranceDurationS
  ) {
    _elevator = elevator.getElevator(id);
    _goalPositionM = goalPositionM;
    _holdPositionAfter = holdPositionAfter;
    _lowerBoundM = _goalPositionM - Math.abs(lowerToleranceM);
    _upperBoundM = _goalPositionM + Math.abs(upperToleranceM);
    _maxVelocityMPS = maxVelocityMPS;
    _requiredInToleranceDurationS = requiredInToleranceDurationS;
    _requireInTolerance = _requiredInToleranceDurationS >= 0.0;

    addRequirements(elevator);
  }
}
