/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.commands.base;

import tagalong.commands.TagalongCommand;
import tagalong.measurements.Angle;
import tagalong.subsystems.TagalongSubsystemBase;
import tagalong.subsystems.micro.Roller;
import tagalong.subsystems.micro.augments.RollerAugment;

/**
 * Command that rotates roller by a target angle based on specified
 * parameters.
 */
public class RollXCmd<T extends TagalongSubsystemBase & RollerAugment> extends TagalongCommand {
  /**
   * Roller microsystem
   */
  private final Roller _roller;
  /**
   * Desired additional movement for the roller relative to its current position
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
   * Whether or not to require roller to be in tolerance
   */
  private final boolean _requireInTolerance;
  /**
   * How long roller must be in tolerance for in seconds
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
   * Whether or not the roller should holds its position after the command ends
   */
  private boolean _holdPositionAfter;
  /**
   * whether or not the roller has started moving
   */
  private boolean _startedMovement;
  /**
   *The maximum velocity of the roller, in rotations per second, during this command
   */
  private double _maxVelocityRPS;
  /**
   * Starting angle of the roller when the command executes
   */
  private double _startAngleRot;
  /**
   * Actual goal angle of the roller when the command executes
   */
  private double _goalAngleRot;

  @Override
  public void initialize() {
    _startedMovement = false;
    _startAngleRot = _roller.getRollerPosition();
    _goalAngleRot = _startAngleRot + _relativeMovementRot;
    _lowerBoundRot = _goalAngleRot - _lowerToleranceRot;
    _upperBoundRot = _goalAngleRot + _upperToleranceRot;
  }

  @Override
  public void execute() {
    if (!_startedMovement) {
      _roller.setRollerProfile(_goalAngleRot, 0.0, _maxVelocityRPS);
      _startedMovement = true;
      _roller.followLastProfile();
    } else {
      _roller.followLastProfile();
    }
  }

  @Override
  public void end(boolean interrupted) {
    _roller.setFollowProfile(_holdPositionAfter);
  }

  @Override
  public boolean isFinished() {
    // Command is finished when the profile is finished AND
    // Either the tolerance is bypassed or in tolerance for the desired duration
    return _roller.isProfileFinished()
        && (!_requireInTolerance
            || _roller.checkToleranceTime(
                _roller.isRollerInTolerance(_lowerBoundRot, _upperBoundRot),
                _requiredInToleranceDurationS
            ));
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param roller          Tagalong Subsystem containing a roller microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   */
  public RollXCmd(T roller, Angle relativeMovementRot) {
    this(roller, relativeMovementRot, true);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Tagalong Subsystem containing a roller microsystem
   * @param roller          Tagalong Subsystem containing a roller microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   */
  public RollXCmd(int id, T roller, Angle relativeMovementRot) {
    this(id, roller, relativeMovementRot, true);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param roller          Tagalong Subsystem containing a roller microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   * @param holdPositionAfter whether or not the roller must hold position after reaching target
   */
  public RollXCmd(T roller, Angle relativeMovementRot, boolean holdPositionAfter) {
    this(roller, relativeMovementRot, holdPositionAfter, roller.getRoller()._maxVelocityRPS);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Tagalong Subsystem containing a roller microsystem
   * @param roller          Tagalong Subsystem containing a roller microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   * @param holdPositionAfter whether or not the roller must hold position after
   *                          reaching target
   */
  public RollXCmd(int id, T roller, Angle relativeMovementRot, boolean holdPositionAfter) {
    this(id, roller, relativeMovementRot, holdPositionAfter, roller.getRoller(id)._maxVelocityRPS);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param roller          Tagalong Subsystem containing a roller
   *                          microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   * @param holdPositionAfter whether or not the roller must hold position after
   *                          reaching target
   * @param maxVelocityRPS    maximum velocity, in rotations per second, the roller
   *                          can move
   */
  public RollXCmd(
      T roller, Angle relativeMovementRot, boolean holdPositionAfter, double maxVelocityRPS
  ) {
    this(
        roller,
        relativeMovementRot,
        holdPositionAfter,
        maxVelocityRPS,
        roller.getRoller()._defaultRollerLowerToleranceRot,
        roller.getRoller()._defaultRollerUpperToleranceRot
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Tagalong Subsystem containing a roller
   *                          microsystem
   * @param roller          Tagalong Subsystem containing a roller
   *                          microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   * @param holdPositionAfter whether or not the roller must hold position after
   *                          reaching target
   * @param maxVelocityRPS    maximum velocity, in rotations per second, the roller
   *                          can move
   */
  public RollXCmd(
      int id, T roller, Angle relativeMovementRot, boolean holdPositionAfter, double maxVelocityRPS
  ) {
    this(
        id,
        roller,
        relativeMovementRot,
        holdPositionAfter,
        maxVelocityRPS,
        roller.getRoller(id)._defaultRollerLowerToleranceRot,
        roller.getRoller(id)._defaultRollerUpperToleranceRot
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param roller          Tagalong Subsystem containing a roller
   *                          microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   * @param holdPositionAfter whether or not the roller must hold position after
   *                          reaching target
   * @param maxVelocityRPS    maximum velocity, in rotations per second, the roller
   *                          can move
   * @param toleranceM        the desired tolerance for angle, in rotations
   */
  public RollXCmd(
      T roller,
      Angle relativeMovementRot,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double toleranceM
  ) {
    this(roller, relativeMovementRot, holdPositionAfter, maxVelocityRPS, toleranceM, toleranceM);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                Tagalong Subsystem containing a roller
   *                          microsystem
   * @param roller          Tagalong Subsystem containing a roller
   *                          microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   * @param holdPositionAfter whether or not the roller must hold position after
   *                          reaching target
   * @param maxVelocityRPS    maximum velocity, in rotations per second, the roller
   *                          can move
   * @param toleranceM        the desired tolerance for angle, in rotations
   */
  public RollXCmd(
      int id,
      T roller,
      Angle relativeMovementRot,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double toleranceM
  ) {
    this(
        id, roller, relativeMovementRot, holdPositionAfter, maxVelocityRPS, toleranceM, toleranceM
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param roller          Tagalong Subsystem containing a roller
   *                          microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   * @param holdPositionAfter whether or not the roller must hold position after
   *                          reaching target
   * @param maxVelocityRPS    maximum velocity, in rotations per second, the roller
   *                          can move
   * @param lowerToleranceM   the lower bound for angle tolerance, in rotations
   * @param upperToleranceM   the upper bound for angle tolerance, in rotations
   */
  public RollXCmd(
      T roller,
      Angle relativeMovementRot,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceM,
      double upperToleranceM
  ) {
    this(
        roller,
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
   * @param id                Tagalong Subsystem containing a roller
   *                          microsystem
   * @param roller          Tagalong Subsystem containing a roller
   *                          microsystem
   * @param relativeMovementRot The target amount to move, in rotations
   * @param holdPositionAfter whether or not the roller must hold position after
   *                          reaching target
   * @param maxVelocityRPS    maximum velocity, in rotations per second, the roller
   *                          can move
   * @param lowerToleranceM   the lower bound for angle tolerance, in rotations
   * @param upperToleranceM   the upper bound for angle tolerance, in rotations
   */
  public RollXCmd(
      int id,
      T roller,
      Angle relativeMovementRot,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceM,
      double upperToleranceM
  ) {
    this(
        id,
        roller,
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
   * @param roller                     Tagalong Subsystem containing a roller
   *                                     microsystem
   * @param relativeMovement             the target amount to move
   * @param holdPositionAfter            whether or not the roller must hold
   *                                     position after reaching target
   * @param maxVelocityRPS               maximum velocity, in rotations per second,
   *                                     the roller can move
   * @param lowerToleranceM              the lower bound for angle tolerance, in
   *                                     rotations
   * @param upperToleranceM              the upper bound for angle tolerance, in
   *                                     rotations
   * @param requiredInToleranceDurationS the duration (in seconds) the roller
   *                                     must stay in
   *                                     tolerance
   */
  public RollXCmd(
      T roller,
      Angle relativeMovement,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceM,
      double upperToleranceM,
      double requiredInToleranceDurationS
  ) {
    this(
        roller,
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
   * @param id                           Tagalong Subsystem containing a roller
   *                                     microsystem
   * @param roller                     Tagalong Subsystem containing a roller
   *                                     microsystem
   * @param relativeMovement             the target amount to move
   * @param holdPositionAfter            whether or not the roller must hold
   *                                     position after reaching target
   * @param maxVelocityRPS               maximum velocity, in rotations per second,
   *                                     the roller can move
   * @param lowerToleranceM              the lower bound for angle tolerance, in
   *                                     rotations
   * @param upperToleranceM              the upper bound for angle tolerance, in
   *                                     rotations
   * @param requiredInToleranceDurationS the duration (in seconds) the roller
   *                                     must stay in
   *                                     tolerance
   */
  public RollXCmd(
      int id,
      T roller,
      Angle relativeMovement,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceM,
      double upperToleranceM,
      double requiredInToleranceDurationS
  ) {
    this(
        id,
        roller,
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
   * @param roller                     Tagalong Subsystem containing a roller
   *                                     microsystem
   * @param relativeMovementRot            the target amount to move, in rotations
   * @param holdPositionAfter            whether or not the roller must hold
   *                                     position after reaching target
   * @param maxVelocityRPS               maximum velocity, in rotations per second,
   *                                     the roller can move
   * @param lowerToleranceM              the lower bound for angle tolerance, in
   *                                     rotations
   * @param upperToleranceM              the upper bound for angle tolerance, in
   *                                     rotations
   * @param requiredInToleranceDurationS the duration (in seconds) the roller
   *                                     must stay in
   *                                     tolerance
   */
  public RollXCmd(
      T roller,
      double relativeMovementRot,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceM,
      double upperToleranceM,
      double requiredInToleranceDurationS
  ) {
    _roller = roller.getRoller();
    _relativeMovementRot = relativeMovementRot;
    _holdPositionAfter = holdPositionAfter;
    _lowerToleranceRot = Math.abs(lowerToleranceM);
    _upperToleranceRot = Math.abs(upperToleranceM);
    _maxVelocityRPS = maxVelocityRPS;
    _requiredInToleranceDurationS = requiredInToleranceDurationS;
    _requireInTolerance = _requiredInToleranceDurationS >= 0.0;

    addRequirements(roller);
  }

  /**
   * Full constructor, allows for double for goal in rare cases of advance use
   * needing a direct way to interact
   *
   * @param id                           Tagalong Subsystem containing a roller
   *                                     microsystem
   * @param roller                     Tagalong Subsystem containing a roller
   *                                     microsystem
   * @param relativeMovementRot            the target amount to move, in rotations
   * @param holdPositionAfter            whether or not the roller must hold
   *                                     position after reaching target
   * @param maxVelocityRPS               maximum velocity, in rotations per second,
   *                                     the roller can move
   * @param lowerToleranceM              the lower bound for angle tolerance, in
   *                                     rotations
   * @param upperToleranceM              the upper bound for angle tolerance, in
   *                                     rotations
   * @param requiredInToleranceDurationS the duration (in seconds) the roller
   *                                     must stay in
   *                                     tolerance
   */
  public RollXCmd(
      int id,
      T roller,
      double relativeMovementRot,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceM,
      double upperToleranceM,
      double requiredInToleranceDurationS
  ) {
    _roller = roller.getRoller(id);
    _relativeMovementRot = relativeMovementRot;
    _holdPositionAfter = holdPositionAfter;
    _lowerToleranceRot = _goalAngleRot - Math.abs(lowerToleranceM);
    _upperToleranceRot = _goalAngleRot + Math.abs(upperToleranceM);
    _maxVelocityRPS = maxVelocityRPS;
    _requiredInToleranceDurationS = requiredInToleranceDurationS;
    _requireInTolerance = _requiredInToleranceDurationS >= 0.0;

    addRequirements(roller);
  }
}
