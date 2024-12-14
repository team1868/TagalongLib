/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.commands.base;

import tagalong.commands.TagalongCommand;
import tagalong.measurements.Angle;
import tagalong.subsystems.TagalongSubsystemBase;
import tagalong.subsystems.micro.Pivot;
import tagalong.subsystems.micro.augments.PivotAugment;

/**
 * Command that moves the pivot absolutely to the desired position
 */
public class PivotToCmd<T extends TagalongSubsystemBase & PivotAugment> extends TagalongCommand {
  /**
   * Pivot microsystem
   */
  private final Pivot _pivot;
  /**
   * Goal position in rotations
   */
  private final double _goalPositionRot;
  /**
   * Whether or not the pivot must hold position after reaching target
   */
  private final boolean _holdPositionAfter;
  /**
   * Lower pivot bound in rotations
   */
  private final double _lowerBoundRot;
  /**
   * Upper pivot bound in rotations
   */
  private final double _upperBoundRot;
  /**
   * Whether or not to require pivot to be in tolerance
   */
  private final boolean _requireInTolerance;
  /**
   * How long pivot must be in tolerance for in seconds
   */
  private final double _requiredInToleranceDurationS;

  /**
   * The maximum velocity of the pivot, in rotations per second, during this command
   */
  private double _maxVelocityRPS;
  /**
   * Whether or not the pivot has started moving
   */
  private boolean _startedMovement;

  @Override
  public void initialize() {
    _pivot.setHoldPosition(false);
    _startedMovement = false;
  }

  @Override
  public void execute() {
    if (!_startedMovement) {
      _startedMovement = true;
      _pivot.setPivotProfile(_pivot.clampPivotPosition(_goalPositionRot), 0.0, _maxVelocityRPS);
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
                _pivot.isPivotInTolerance(_lowerBoundRot, _upperBoundRot),
                _requiredInToleranceDurationS
            ));
  }

  /**
   * Constructor with default to hold position after
   *
   * @param pivot        Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition Goal pivot position
   */
  public PivotToCmd(T pivot, Angle goalPosition) {
    this(pivot, goalPosition, true);
  }

  /**
   * Constructor with default to hold position after
   *
   * @param id           The pivot integer ID
   * @param pivot        Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition Goal pivot positon
   */
  public PivotToCmd(int id, T pivot, Angle goalPosition) {
    this(id, pivot, goalPosition, true);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param pivot             Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition      Goal pivot positon
   * @param holdPositionAfter If the pivot should hold position when the command
   *                          completes
   *
   */
  public PivotToCmd(T pivot, Angle goalPosition, boolean holdPositionAfter) {
    this(pivot, goalPosition, holdPositionAfter, pivot.getPivot()._maxVelocityRPS);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                The pivot integer ID
   * @param pivot             Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition      Goal pivot positon
   * @param holdPositionAfter If the pivot should hold position when the command
   *                          completes
   *
   */
  public PivotToCmd(int id, T pivot, Angle goalPosition, boolean holdPositionAfter) {
    this(id, pivot, goalPosition, holdPositionAfter, pivot.getPivot(id)._maxVelocityRPS);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param pivot             Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition      Goal pivot positon
   * @param holdPositionAfter If the pivot should hold position when the command
   *                          completes
   * @param maxVelocityRPS    The maximum velocity of the pivot, in rotations per
   *                          second, during this command
   */

  public PivotToCmd(T pivot, Angle goalPosition, boolean holdPositionAfter, double maxVelocityRPS) {
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
   * @param goalPosition      Goal pivot positon
   * @param holdPositionAfter If the pivot should hold position when the command
   *                          completes
   * @param maxVelocityRPS    The maximum velocity of the pivot, in rotations per
   *                          second, during this command
   */

  public PivotToCmd(
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
   * @param goalPosition      Goal pivot positon
   * @param holdPositionAfter If the pivot should hold position when the command
   *                          completes
   * @param maxVelocityRPS    The maximum velocity of the pivot, in rotations per
   *                          second, during this command
   * @param toleranceRot       The number of rotations short of or beyond the
   *                          target position the
   *                          pivot can be while still being considered in
   *                          tolerance
   */

  public PivotToCmd(
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
   * @param goalPosition      Goal pivot positon
   * @param holdPositionAfter If the pivot should hold position when the command
   *                          completes
   * @param maxVelocityRPS    The maximum velocity of the pivot, in rotations per
   *                          second, during this command
   * @param toleranceRot      The number of rotations short of or beyond the
   *                          target position the
   *                          pivot can be while still being considered in
   *                          tolerance
   */
  public PivotToCmd(
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
   * Constructor that creates the command with the below parameters.
   *
   * @param pivot             Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition      Goal pivot positon
   * @param holdPositionAfter If the pivot should hold position when the command
   *                          completes
   * @param maxVelocityRPS    The maximum velocity of the pivot, in rotations per
   *                          second, during this command
   * @param lowerToleranceRot The number of rotations short of the target position
   *                          the pivot can be
   *                          while still being considered in tolerance
   * @param upperToleranceRot The number of rotations beyond the target position
   *                          the pivot can be
   *                          while still being considered in tolerance
   */

  public PivotToCmd(
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
   * Constructor that creates the command with the below parameters.
   *
   * @param id                The pivot integer ID
   * @param pivot             Tagalong Subsystem containing a pivot microsystem
   * @param goalPosition      Goal pivot positon
   * @param holdPositionAfter If the pivot should hold position when the command
   *                          completes
   * @param maxVelocityRPS    The maximum velocity of the pivot, in rotations per
   *                          second, during this command
   * @param lowerToleranceRot The number of rotations short of the target position
   *                          the pivot can be
   *                          while still being considered in tolerance
   * @param upperToleranceRot The number of rotations beyond the target position
   *                          the pivot can be
   *                          while still being considered in tolerance
   */

  public PivotToCmd(
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
   * Constructor that creates the command with the below parameters.
   *
   * @param pivot                        Tagalong Subsystem containing a pivot
   *                                     microsystem
   * @param goalPosition                 Goal pivot positon
   * @param holdPositionAfter            If the pivot should hold position when
   *                                     the command completes
   * @param maxVelocityRPS               The maximum velocity of the pivot, in
   *                                     rotations per second, during this
   *                                     command
   * @param upperToleranceRot            The number of rotations beyond the target
   *                                     position the pivot can be
   *                                     while still being considered in tolerance
   * @param lowerToleranceRot            The number of rotations short of the
   *                                     target position the pivot can be
   *                                     while still being considered in tolerance
   * @param requiredInToleranceDurationS The number of seconds that being in
   *                                     tolerance is required for
   */
  public PivotToCmd(
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
   * Constructor that creates the command with the below parameters.
   *
   * @param id                           The pivot integer ID
   * @param pivot                        Tagalong Subsystem containing a pivot
   *                                     microsystem
   * @param goalPosition                 Goal pivot positon
   * @param holdPositionAfter            If the pivot should hold position when
   *                                     the command completes
   * @param maxVelocityRPS               The maximum velocity of the pivot, in
   *                                     rotations per second, during this
   *                                     command
   * @param lowerToleranceRot            The number of rotations short of the
   *                                     target position the pivot can be
   *                                     while still being considered in tolerance
   * @param upperToleranceRot            The number of rotations beyond the target
   *                                     position the pivot can be
   *                                     while still being considered in tolerance
   * @param requiredInToleranceDurationS The number of seconds that being in
   *                                     tolerance is required for
   */
  public PivotToCmd(
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
   * Full constructor, allows for double for goal in rare cases of advance use
   * needing a direct way to interact
   *
   * @param pivot                        Tagalong Subsystem containing a pivot
   *                                     microsystem
   * @param goalPosition                 Goal pivot positon
   * @param holdPositionAfter            If the pivot should hold position when
   *                                     the command completes
   * @param maxVelocityRPS               The maximum velocity of the pivot, in
   *                                     rotations per second, during this
   *                                     command
   * @param lowerToleranceRot            The number of rotations short of the
   *                                     target position the pivot can be
   *                                     while still being considered in tolerance
   * @param upperToleranceRot            The number of rotations beyond the target
   *                                     position the pivot can be
   *                                     while still being considered in tolerance
   * @param requiredInToleranceDurationS The number of seconds that being in
   *                                     tolerance is required for
   */
  public PivotToCmd(
      T pivot,
      double goalPosition,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceRot,
      double upperToleranceRot,
      double requiredInToleranceDurationS
  ) {
    _pivot = pivot.getPivot();
    _goalPositionRot = goalPosition;
    _holdPositionAfter = holdPositionAfter;
    _maxVelocityRPS = maxVelocityRPS;
    _lowerBoundRot = _goalPositionRot - Math.abs(lowerToleranceRot);
    _upperBoundRot = _goalPositionRot + Math.abs(upperToleranceRot);
    _requiredInToleranceDurationS = requiredInToleranceDurationS;
    _requireInTolerance = _requiredInToleranceDurationS >= 0.0;

    addRequirements(pivot);
  }

  /**
   * Full constructor, allows for double for goal in rare cases of advance use
   * needing a direct way to interact
   *
   * @param id                           The pivot integer ID
   * @param pivot                        Tagalong Subsystem containing a pivot
   *                                     microsystem
   * @param goalPosition                 Goal pivot positon
   * @param holdPositionAfter            If the pivot should hold position when
   *                                     the command completes
   * @param maxVelocityRPS               The maximum velocity of the pivot, in
   *                                     rotations per second, during this
   *                                     command
   * @param lowerToleranceRot            The number of rotations short of the
   *                                     target position the pivot can be
   *                                     while still being considered in tolerance
   * @param upperToleranceRot            The number of rotations beyond the target
   *                                     position the pivot can be
   *                                     while still being considered in tolerance
   * @param requiredInToleranceDurationS The number of seconds that being in
   *                                     tolerance is required for
   */
  public PivotToCmd(
      int id,
      T pivot,
      double goalPosition,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceRot,
      double upperToleranceRot,
      double requiredInToleranceDurationS
  ) {
    _pivot = pivot.getPivot(id);
    _goalPositionRot = goalPosition;
    _holdPositionAfter = holdPositionAfter;
    _maxVelocityRPS = maxVelocityRPS;
    _lowerBoundRot = _goalPositionRot - Math.abs(lowerToleranceRot);
    _upperBoundRot = _goalPositionRot + Math.abs(upperToleranceRot);
    _requiredInToleranceDurationS = requiredInToleranceDurationS;
    _requireInTolerance = _requiredInToleranceDurationS >= 0.0;

    addRequirements(pivot);
  }
}
