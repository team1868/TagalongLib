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
 * Command that rotates pivot by a target angle based on specified
 * parameters.
 */
public class PivotXCmd<T extends TagalongSubsystemBase & PivotAugment> extends TagalongCommand {
  /**
   * Pivot microsystem
   */
  private final Pivot _pivot;
  /**
   * Desired additional movement for the pivot relative to its current position
   */
  private final double _relativeMovementRot;
  /**
   * Lower tolerance in rotations
   */
  private final double _lowerToleranceRot;
  /**
   * Upper tolerance in rotations
   */
  private final double _upperToleranceRot;
  /**
   * Whether or not to require pivot to be in tolerance
   */
  private final boolean _requireInTolerance;
  /**
   * How long pivot must be in tolerance for in seconds
   */
  private final double _requiredInToleranceDurationS;

  /**
   * Lower bound of the goal tolerance
   */
  private double _lowerBoundRot;
  /**
   * Upper bound of the goal tolerance
   */
  private double _upperBoundRot;
  /**
   * Whether or not the pivot should holds its position after the command ends
   */
  private boolean _holdPositionAfter;
  /**
   * whether or not the pivot has started moving
   */
  private boolean _startedMovement;
  /**
   *The maximum velocity of the pivot, in rotations per second, during this command
   */
  private double _maxVelocityRPS;
  /**
   * Starting angle of the pivot when the command executes
   */
  private double _startAngleRot;
  /**
   * Actual goal angle of the pivot when the command executes
   */
  private double _goalAngleRot;

  @Override
  public void initialize() {
    _startedMovement = false;
    _startAngleRot = _pivot.getPivotPosition();
    _goalAngleRot = _pivot.clampPivotPosition(_startAngleRot + _relativeMovementRot);
    _lowerBoundRot = _goalAngleRot - _lowerToleranceRot;
    _upperBoundRot = _goalAngleRot + _upperToleranceRot;
  }

  @Override
  public void execute() {
    if (!_startedMovement) {
      if (_pivot.isSafeToMove()) {
        _pivot.setPivotProfile(_goalAngleRot, 0.0, _maxVelocityRPS);
        _startedMovement = true;
        _pivot.followLastProfile();
      }
    } else {
      _pivot.followLastProfile();
    }
  }

  @Override
  public void end(boolean interrupted) {
    _pivot.setFollowProfile(_holdPositionAfter);
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
   * Constructor that creates the command with the below parameters.
   *
   * @param pivot          Tagalong Subsystem containing a pivot
   *                          microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   */
  public PivotXCmd(T pivot, Angle relativeMovementRot) {
    this(pivot, relativeMovementRot, true);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Tagalong Subsystem containing a pivot
   *                          microsystem
   * @param pivot          Tagalong Subsystem containing a pivot
   *                          microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   */
  public PivotXCmd(int id, T pivot, Angle relativeMovementRot) {
    this(id, pivot, relativeMovementRot, true);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param pivot          Tagalong Subsystem containing a pivot
   *                          microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   * @param holdPositionAfter whether or not the pivot must hold position after
   *                          reaching target
   */
  public PivotXCmd(T pivot, Angle relativeMovementRot, boolean holdPositionAfter) {
    this(pivot, relativeMovementRot, holdPositionAfter, pivot.getPivot()._maxVelocityRPS);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Tagalong Subsystem containing a pivot
   *                          microsystem
   * @param pivot          Tagalong Subsystem containing a pivot
   *                          microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   * @param holdPositionAfter whether or not the pivot must hold position after
   *                          reaching target
   */
  public PivotXCmd(int id, T pivot, Angle relativeMovementRot, boolean holdPositionAfter) {
    this(id, pivot, relativeMovementRot, holdPositionAfter, pivot.getPivot(id)._maxVelocityRPS);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param pivot          Tagalong Subsystem containing a pivot
   *                          microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   * @param holdPositionAfter whether or not the pivot must hold position after
   *                          reaching target
   * @param maxVelocityRPS    maximum velocity, in rotations per second, the pivot
   *                          can move
   */
  public PivotXCmd(
      T pivot, Angle relativeMovementRot, boolean holdPositionAfter, double maxVelocityRPS
  ) {
    this(
        pivot,
        relativeMovementRot,
        holdPositionAfter,
        maxVelocityRPS,
        pivot.getPivot()._defaultPivotLowerToleranceRot,
        pivot.getPivot()._defaultPivotUpperToleranceRot
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Tagalong Subsystem containing a pivot
   *                          microsystem
   * @param pivot          Tagalong Subsystem containing a pivot
   *                          microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   * @param holdPositionAfter whether or not the pivot must hold position after
   *                          reaching target
   * @param maxVelocityRPS    maximum velocity, in rotations per second, the pivot
   *                          can move
   */
  public PivotXCmd(
      int id, T pivot, Angle relativeMovementRot, boolean holdPositionAfter, double maxVelocityRPS
  ) {
    this(
        id,
        pivot,
        relativeMovementRot,
        holdPositionAfter,
        maxVelocityRPS,
        pivot.getPivot(id)._defaultPivotLowerToleranceRot,
        pivot.getPivot(id)._defaultPivotUpperToleranceRot
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param pivot          Tagalong Subsystem containing a pivot
   *                          microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   * @param holdPositionAfter whether or not the pivot must hold position after
   *                          reaching target
   * @param maxVelocityRPS    maximum velocity, in rotations per second, the pivot
   *                          can move
   * @param toleranceM        the desired tolerance for angle, in rotations
   */
  public PivotXCmd(
      T pivot,
      Angle relativeMovementRot,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double toleranceM
  ) {
    this(pivot, relativeMovementRot, holdPositionAfter, maxVelocityRPS, toleranceM, toleranceM);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Tagalong Subsystem containing a pivot
   *                          microsystem
   * @param pivot          Tagalong Subsystem containing a pivot
   *                          microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   * @param holdPositionAfter whether or not the pivot must hold position after
   *                          reaching target
   * @param maxVelocityRPS    maximum velocity, in rotations per second, the pivot
   *                          can move
   * @param toleranceM        the desired tolerance for angle, in rotations
   */
  public PivotXCmd(
      int id,
      T pivot,
      Angle relativeMovementRot,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double toleranceM
  ) {
    this(id, pivot, relativeMovementRot, holdPositionAfter, maxVelocityRPS, toleranceM, toleranceM);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param pivot          Tagalong Subsystem containing a pivot
   *                          microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   * @param holdPositionAfter whether or not the pivot must hold position after
   *                          reaching target
   * @param maxVelocityRPS    maximum velocity, in rotations per second, the pivot
   *                          can move
   * @param lowerToleranceM   the lower bound for angle tolerance, in rotations
   * @param upperToleranceM   the upper bound for angle tolerance, in rotations
   */
  public PivotXCmd(
      T pivot,
      Angle relativeMovementRot,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceM,
      double upperToleranceM
  ) {
    this(
        pivot,
        relativeMovementRot,
        holdPositionAfter,
        maxVelocityRPS,
        lowerToleranceM,
        upperToleranceM,
        -1.0
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Tagalong Subsystem containing a pivot
   *                          microsystem
   * @param pivot          Tagalong Subsystem containing a pivot
   *                          microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   * @param holdPositionAfter whether or not the pivot must hold position after
   *                          reaching target
   * @param maxVelocityRPS    maximum velocity, in rotations per second, the pivot
   *                          can move
   * @param lowerToleranceM   the lower bound for angle tolerance, in rotations
   * @param upperToleranceM   the upper bound for angle tolerance, in rotations
   */
  public PivotXCmd(
      int id,
      T pivot,
      Angle relativeMovementRot,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceM,
      double upperToleranceM
  ) {
    this(
        id,
        pivot,
        relativeMovementRot,
        holdPositionAfter,
        maxVelocityRPS,
        lowerToleranceM,
        upperToleranceM,
        -1.0
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param pivot                     Tagalong Subsystem containing a pivot
   *                                     microsystem
   * @param relativeMovement             the target amount to move
   * @param holdPositionAfter            whether or not the pivot must hold
   *                                     position after reaching target
   * @param maxVelocityRPS               maximum velocity, in rotations per second,
   *                                     the pivot can move
   * @param lowerToleranceM              the lower bound for angle tolerance, in
   *                                     rotations
   * @param upperToleranceM              the upper bound for angle tolerance, in
   *                                     rotations
   * @param requiredInToleranceDurationS the duration (in seconds) the pivot
   *                                     must stay in
   *                                     tolerance
   */
  public PivotXCmd(
      T pivot,
      Angle relativeMovement,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceM,
      double upperToleranceM,
      double requiredInToleranceDurationS
  ) {
    this(
        pivot,
        relativeMovement.getRotations(),
        holdPositionAfter,
        maxVelocityRPS,
        lowerToleranceM,
        upperToleranceM,
        requiredInToleranceDurationS
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                           Tagalong Subsystem containing a pivot
   *                                     microsystem
   * @param pivot                        Tagalong Subsystem containing a pivot
   *                                     microsystem
   * @param relativeMovement             the target amount to move
   * @param holdPositionAfter            whether or not the pivot must hold
   *                                     position after reaching target
   * @param maxVelocityRPS               maximum velocity, in rotations per second,
   *                                     the pivot can move
   * @param lowerToleranceM              the lower bound for angle tolerance, in
   *                                     rotations
   * @param upperToleranceM              the upper bound for angle tolerance, in
   *                                     rotations
   * @param requiredInToleranceDurationS the duration (in seconds) the pivot
   *                                     must stay in
   *                                     tolerance
   */
  public PivotXCmd(
      int id,
      T pivot,
      Angle relativeMovement,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceM,
      double upperToleranceM,
      double requiredInToleranceDurationS
  ) {
    this(
        id,
        pivot,
        relativeMovement.getRotations(),
        holdPositionAfter,
        maxVelocityRPS,
        lowerToleranceM,
        upperToleranceM,
        requiredInToleranceDurationS
    );
  }

  /**
   * Full constructor, allows for double for goal in rare cases of advance use
   * needing a direct way to interact
   *
   * @param pivot                     Tagalong Subsystem containing a pivot
   *                                     microsystem
   * @param relativeMovementRot            the target amount to move, in rotations
   * @param holdPositionAfter            whether or not the pivot must hold
   *                                     position after reaching target
   * @param maxVelocityRPS               maximum velocity, in rotations per second,
   *                                     the pivot can move
   * @param lowerToleranceM              the lower bound for angle tolerance, in
   *                                     rotations
   * @param upperToleranceM              the upper bound for angle tolerance, in
   *                                     rotations
   * @param requiredInToleranceDurationS the duration (in seconds) the pivot
   *                                     must stay in
   *                                     tolerance
   */
  public PivotXCmd(
      T pivot,
      double relativeMovementRot,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceM,
      double upperToleranceM,
      double requiredInToleranceDurationS
  ) {
    _pivot = pivot.getPivot();
    _relativeMovementRot = relativeMovementRot;
    _holdPositionAfter = holdPositionAfter;
    _lowerToleranceRot = Math.abs(lowerToleranceM);
    _upperToleranceRot = Math.abs(upperToleranceM);
    _maxVelocityRPS = maxVelocityRPS;
    _requiredInToleranceDurationS = requiredInToleranceDurationS;
    _requireInTolerance = _requiredInToleranceDurationS >= 0.0;

    addRequirements(pivot);
  }

  /**
   * Full constructor, allows for double for goal in rare cases of advance use
   * needing a direct way to interact
   *
   * @param id                           Tagalong Subsystem containing a pivot
   *                                     microsystem
   * @param pivot                     Tagalong Subsystem containing a pivot
   *                                     microsystem
   * @param relativeMovementRot            the target amount to move, in rotations
   * @param holdPositionAfter            whether or not the pivot must hold
   *                                     position after reaching target
   * @param maxVelocityRPS               maximum velocity, in rotations per second,
   *                                     the pivot can move
   * @param lowerToleranceM              the lower bound for angle tolerance, in
   *                                     rotations
   * @param upperToleranceM              the upper bound for angle tolerance, in
   *                                     rotations
   * @param requiredInToleranceDurationS the duration (in seconds) the pivot
   *                                     must stay in
   *                                     tolerance
   */
  public PivotXCmd(
      int id,
      T pivot,
      double relativeMovementRot,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceM,
      double upperToleranceM,
      double requiredInToleranceDurationS
  ) {
    _pivot = pivot.getPivot(id);
    _relativeMovementRot = relativeMovementRot;
    _holdPositionAfter = holdPositionAfter;
    _lowerToleranceRot = _goalAngleRot - Math.abs(lowerToleranceM);
    _upperToleranceRot = _goalAngleRot + Math.abs(upperToleranceM);
    _maxVelocityRPS = maxVelocityRPS;
    _requiredInToleranceDurationS = requiredInToleranceDurationS;
    _requireInTolerance = _requiredInToleranceDurationS >= 0.0;

    addRequirements(pivot);
  }
}
