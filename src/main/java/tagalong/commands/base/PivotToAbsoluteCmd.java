/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.commands.base;

import tagalong.commands.TagalongCommand;
import tagalong.math.AlgebraicUtils;
import tagalong.measurements.Angle;
import tagalong.subsystems.TagalongSubsystemBase;
import tagalong.subsystems.micro.Pivot;
import tagalong.subsystems.micro.augments.PivotAugment;

/**
 * PivotToCommand with optimal pathing
 */
public class PivotToAbsoluteCmd<T extends TagalongSubsystemBase & PivotAugment>
    extends TagalongCommand {
  /**
   * pivot object
   */
  private final Pivot _pivot;
  /**
   * goal position in rotations
   */
  private final double _goalPositionRot;
  /**
   * whether or not the elevator must hold position after reaching target
   */
  private final boolean _holdPositionAfter;
  /**
   * lower pivot bound in rotations
   */
  private final double _lowerBoundRot;
  /**
   * upper pivot bound in rotations
   */
  private final double _upperBoundRot;
  /**
   * whether or not to require pivot to be in tolerance
   */
  private final boolean _requireInTolerance;
  /**
   * how long pivot must be in tolerance for in seconds
   */
  private final double _requiredInToleranceDurationS;

  /**
   *The maximum velocity of the pivot, in rotations per second, during this command
   */
  private double _maxVelocityRPS;
  /**
   * whether or not the pivot has started moving
   */
  private boolean _startedMovement;

  /**
   * closest wrapped goal angle in rotations
   */
  private double _scopedGoalPositionRot;

  @Override
  public void initialize() {
    _pivot.setHoldPosition(false);
    _startedMovement = false;
    _pivot.resetToleranceTimer();
  }

  @Override
  public void execute() {
    if (!_startedMovement && _pivot.isSafeToMove()) {
      _startedMovement = true;
      _scopedGoalPositionRot =
          AlgebraicUtils.placeInScopeRot(_pivot.getPivotPosition(), _goalPositionRot);
      if (_scopedGoalPositionRot < _pivot._minPositionRot) {
        _scopedGoalPositionRot += 1.0;
      }
      if (_scopedGoalPositionRot > _pivot._maxPositionRot) {
        _scopedGoalPositionRot -= 1.0;
      }

      _pivot.setPivotProfile(
          _pivot.clampPivotPosition(_scopedGoalPositionRot), 0.0, _maxVelocityRPS
      );
    }

    if (_startedMovement) {
      _pivot.followLastProfile();
    }
  }

  @Override
  public void end(boolean interrupted) {
    _pivot.setHoldPosition(_holdPositionAfter);
  }

  @Override
  public boolean isFinished() {
    // Command is finished when the profile is finished AND
    // Either the tolerance is bypassed or in tolerance for the desired duration
    return _pivot.isProfileFinished()
        && (!_requireInTolerance
            || _pivot.checkToleranceTime(
                _pivot.isPivotInAbsoluteTolerance(_lowerBoundRot, _upperBoundRot),
                _requiredInToleranceDurationS
            ));
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param pivot        Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition The goal position to reach
   */
  public PivotToAbsoluteCmd(T pivot, Angle goalPosition) {
    this(pivot, goalPosition, true);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id           The pivot integer ID
   * @param pivot        Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition The goal position to reach
   */
  public PivotToAbsoluteCmd(int id, T pivot, Angle goalPosition) {
    this(id, pivot, goalPosition, true);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param pivot             Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition      The goal position to reach
   * @param holdPositionAfter If the pivot should hold position when the command
   *                          completes
   *
   */
  public PivotToAbsoluteCmd(T pivot, Angle goalPosition, boolean holdPositionAfter) {
    this(pivot, goalPosition, holdPositionAfter, pivot.getPivot()._maxVelocityRPS);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                The pivot integer ID
   * @param pivot             Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition      The goal position to reach
   * @param holdPositionAfter If the pivot should hold position when the command
   *                          completes
   *
   */
  public PivotToAbsoluteCmd(int id, T pivot, Angle goalPosition, boolean holdPositionAfter) {
    this(id, pivot, goalPosition, holdPositionAfter, pivot.getPivot(id)._maxVelocityRPS);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param pivot             Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition      The goal position to reach
   * @param holdPositionAfter If the pivot should hold position when the command
   *                          completes
   * @param maxVelocityRPS    The maximum velocity of the pivot, in rotations per
   *                          second, during this command
   */

  public PivotToAbsoluteCmd(
      T pivot, Angle goalPosition, boolean holdPositionAfter, double maxVelocityRPS
  ) {
    this(
        pivot,
        goalPosition,
        holdPositionAfter,
        maxVelocityRPS,
        pivot.getPivot()._defaultPivotLowerToleranceRot,
        pivot.getPivot()._defaultPivotUpperToleranceRot
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                The pivot integer ID
   * @param pivot             Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition      The goal position to reach
   * @param holdPositionAfter If the pivot should hold position when the command
   *                          completes
   * @param maxVelocityRPS    The maximum velocity of the pivot, in rotations per
   *                          second, during this command
   */

  public PivotToAbsoluteCmd(
      int id, T pivot, Angle goalPosition, boolean holdPositionAfter, double maxVelocityRPS
  ) {
    this(
        id,
        pivot,
        goalPosition,
        holdPositionAfter,
        maxVelocityRPS,
        pivot.getPivot(id)._defaultPivotLowerToleranceRot,
        pivot.getPivot(id)._defaultPivotUpperToleranceRot
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param pivot             Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition      The goal position to reach
   * @param holdPositionAfter If the pivot should hold position when the command
   *                          completes
   * @param maxVelocityRPS    The maximum velocity of the pivot, in rotations per
   *                          second, during this command
   * @param toleranceRot      The number of rotations below or beyond the target
   *                          position the pivot can
   *                          be while still being considered in tolerance
   */

  public PivotToAbsoluteCmd(
      T pivot,
      Angle goalPosition,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double toleranceRot
  ) {
    this(pivot, goalPosition, holdPositionAfter, maxVelocityRPS, toleranceRot, toleranceRot);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                The pivot integer ID
   * @param pivot             Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition      The goal position to reach
   * @param holdPositionAfter If the pivot should hold position when the command
   *                          completes
   * @param maxVelocityRPS    The maximum velocity of the pivot, in rotations per
   *                          second, during this command
   * @param toleranceRot      The number of rotations below or beyond the target
   *                          position the pivot can
   *                          be while still being considered in tolerance
   */
  public PivotToAbsoluteCmd(
      int id,
      T pivot,
      Angle goalPosition,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double toleranceRot
  ) {
    this(id, pivot, goalPosition, holdPositionAfter, maxVelocityRPS, toleranceRot, toleranceRot);
  }

  /**
   * Constructor with no in tolerance duration requirement
   *
   * @param pivot             Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition      The goal position to reach
   * @param holdPositionAfter If the pivot should hold position when the command
   *                          completes
   * @param maxVelocityRPS    The maximum velocity of the pivot, in rotations per
   *                          second, during this command
   * @param lowerToleranceRot The number of rotations below the target position
   *                          the pivot can be
   *                          while still being considered in tolerance
   * @param upperToleranceRot The number of rotations beyond the target position
   *                          the pivot can be
   *                          while still being considered in tolerance
   */

  public PivotToAbsoluteCmd(
      T pivot,
      Angle goalPosition,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceRot,
      double upperToleranceRot
  ) {
    this(
        pivot,
        goalPosition,
        holdPositionAfter,
        maxVelocityRPS,
        lowerToleranceRot,
        upperToleranceRot,
        -1.0
    );
  }

  /**
   * Constructor with ID for specifying pivot but no in tolerance duration requirement
   *
   * @param id                The pivot integer ID
   * @param pivot             Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition      The goal position to reach
   * @param holdPositionAfter If the pivot should hold position when the command
   *                          completes
   * @param maxVelocityRPS    The maximum velocity of the pivot, in rotations per
   *                          second, during this command
   * @param lowerToleranceRot The number of rotations below the target position
   *                          the pivot can be
   *                          while still being considered in tolerance
   * @param upperToleranceRot The number of rotations beyond the target position
   *                          the pivot can be
   *                          while still being considered in tolerance
   */

  public PivotToAbsoluteCmd(
      int id,
      T pivot,
      Angle goalPosition,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceRot,
      double upperToleranceRot
  ) {
    this(
        id,
        pivot,
        goalPosition,
        holdPositionAfter,
        maxVelocityRPS,
        lowerToleranceRot,
        upperToleranceRot,
        -1.0
    );
  }

  /**
   * Full constructor
   *
   * @param pivot                        Tagalong Subsystem containing a pivot
   *                                     microsystem
   * @param goalPosition                 The goal position to reach
   * @param holdPositionAfter            If the pivot should hold position when
   *                                     the command completes
   * @param maxVelocityRPS               The maximum velocity of the pivot, in
   *                                     rotations per second, during this
   *                                     command
   * @param upperToleranceRot            The number of rotations beyond the target
   *                                     position the pivot can be
   *                                     while still being considered in tolerance
   * @param lowerToleranceRot            The number of rotations below the target
   *                                     position the pivot can be
   *                                     while still being considered in tolerance
   * @param requiredInToleranceDurationS the duration (in seconds) the pivot must
   *                                     stay in
   *                                     tolerance
   */
  public PivotToAbsoluteCmd(
      T pivot,
      Angle goalPosition,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceRot,
      double upperToleranceRot,
      double requiredInToleranceDurationS
  ) {
    this(
        pivot,
        goalPosition.getRotations(),
        holdPositionAfter,
        maxVelocityRPS,
        lowerToleranceRot,
        upperToleranceRot,
        requiredInToleranceDurationS
    );
  }

  /**
   * Full constructor with ID for specifying pivot
   *
   * @param id                           The pivot integer ID
   * @param pivot                        Tagalong Subsystem containing a pivot
   *                                     microsystem
   * @param goalPosition                 The goal position to reach
   * @param holdPositionAfter            If the pivot should hold position when
   *                                     the command completes
   * @param maxVelocityRPS               The maximum velocity of the pivot, in
   *                                     rotations per second, during this
   *                                     command
   * @param lowerToleranceRot            The number of rotations below the target
   *                                     position the pivot can be
   *                                     while still being considered in tolerance
   * @param upperToleranceRot            The number of rotations beyond the target
   *                                     position the pivot can be
   *                                     while still being considered in tolerance
   * @param requiredInToleranceDurationS the duration (in seconds) the pivot must
   *                                     stay in
   *                                     tolerance
   */
  public PivotToAbsoluteCmd(
      int id,
      T pivot,
      Angle goalPosition,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceRot,
      double upperToleranceRot,
      double requiredInToleranceDurationS
  ) {
    this(
        id,
        pivot,
        goalPosition.getRotations(),
        holdPositionAfter,
        maxVelocityRPS,
        lowerToleranceRot,
        upperToleranceRot,
        requiredInToleranceDurationS
    );
  }

  /**
   * Full constructor, allows for double for goal position (rotations) in rare cases of advance use
   * needing a direct way to interact
   *
   * @param pivot                        Tagalong Subsystem containing a pivot
   *                                     microsystem
   * @param goalPositionRot              The goal position to reach, in rotations
   * @param holdPositionAfter            If the pivot should hold position when
   *                                     the command completes
   * @param maxVelocityRPS               The maximum velocity of the pivot, in
   *                                     rotations per second, during this
   *                                     command
   * @param lowerToleranceRot            The number of rotations below the target
   *                                     position the pivot can be
   *                                     while still being considered in tolerance
   * @param upperToleranceRot            The number of rotations beyond the target
   *                                     position the pivot can be
   *                                     while still being considered in tolerance
   * @param requiredInToleranceDurationS the duration (in seconds) the pivot must
   *                                     stay in
   *                                     tolerance
   */
  public PivotToAbsoluteCmd(
      T pivot,
      double goalPositionRot,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceRot,
      double upperToleranceRot,
      double requiredInToleranceDurationS
  ) {
    _pivot = pivot.getPivot();
    _goalPositionRot = AlgebraicUtils.cppMod(goalPositionRot, 1.0);
    _holdPositionAfter = holdPositionAfter;
    _maxVelocityRPS = maxVelocityRPS;
    _lowerBoundRot = AlgebraicUtils.cppMod(_goalPositionRot, 1.0) - Math.abs(lowerToleranceRot);
    _upperBoundRot = AlgebraicUtils.cppMod(_goalPositionRot, 1.0) + Math.abs(upperToleranceRot);
    _requiredInToleranceDurationS = requiredInToleranceDurationS;
    _requireInTolerance = _requiredInToleranceDurationS >= 0.0;

    addRequirements(pivot);
  }

  /**
   * Full constructor, allows for double for goal position (rotations) in rare cases of advance use
   * needing a direct way to interact
   *
   * @param id                           The pivot integer ID
   * @param pivot                        Tagalong Subsystem containing a pivot
   *                                     microsystem
   * @param goalPositionRot              The goal position to reach, in rotations
   * @param holdPositionAfter            If the pivot should hold position when
   *                                     the command completes
   * @param maxVelocityRPS               The maximum velocity of the pivot, in
   *                                     rotations per second, during this
   *                                     command
   * @param lowerToleranceRot            The number of rotations below the target
   *                                     position the pivot can be
   *                                     while still being considered in tolerance
   * @param upperToleranceRot            The number of rotations beyond the target
   *                                     position the pivot can be
   *                                     while still being considered in tolerance
   * @param requiredInToleranceDurationS the duration (in seconds) the pivot must
   *                                     stay in
   *                                     tolerance
   */
  public PivotToAbsoluteCmd(
      int id,
      T pivot,
      double goalPositionRot,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceRot,
      double upperToleranceRot,
      double requiredInToleranceDurationS
  ) {
    _pivot = pivot.getPivot(id);
    _goalPositionRot = AlgebraicUtils.cppMod(goalPositionRot, 1.0);
    _holdPositionAfter = holdPositionAfter;
    _maxVelocityRPS = maxVelocityRPS;
    _lowerBoundRot = AlgebraicUtils.cppMod(_goalPositionRot, 1.0) - Math.abs(lowerToleranceRot);
    _upperBoundRot = AlgebraicUtils.cppMod(_goalPositionRot, 1.0) + Math.abs(upperToleranceRot);
    _requiredInToleranceDurationS = requiredInToleranceDurationS;
    _requireInTolerance = _requiredInToleranceDurationS >= 0.0;

    addRequirements(pivot);
  }
}
