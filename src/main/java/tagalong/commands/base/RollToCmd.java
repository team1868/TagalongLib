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
 * Command that moves the roller absolutely to the desired position
 */
public class RollToCmd<T extends TagalongSubsystemBase & RollerAugment> extends TagalongCommand {
  /**
   * Roller microsystem
   */
  private final Roller _roller;
  /**
   * Goal position in rotations
   */
  private final double _goalPositionRot;
  /**
   * Whether or not the roller must hold position after reaching target
   */
  private final boolean _holdPositionAfter;
  /**
   * Lower roller bound in rotations
   */
  private final double _lowerBoundRot;
  /**
   * Upper roller bound in rotations
   */
  private final double _upperBoundRot;
  /**
   * Whether or not to require roller to be in tolerance
   */
  private final boolean _requireInTolerance;
  /**
   * How long roller must be in tolerance for in seconds
   */
  private final double _requiredInToleranceDurationS;

  /**
   * The maximum velocity of the roller, in rotations per second, during this command
   */
  private double _maxVelocityRPS;
  /**
   * Whether or not the roller has started moving
   */
  private boolean _startedMovement;

  @Override
  public void initialize() {
    _roller.setHoldPosition(false);
    _startedMovement = false;
    _roller.resetToleranceTimer();
  }

  @Override
  public void execute() {
    if (!_startedMovement) {
      _startedMovement = true;
      _roller.setRollerProfile(_goalPositionRot, 0.0, _maxVelocityRPS);
    }

    if (_startedMovement) {
      _roller.followLastProfile();
    }
  }

  @Override
  public void end(boolean interrupted) {
    _roller.setHoldPosition(_holdPositionAfter);
  }

  @Override
  public boolean isFinished() {
    // Command is finished when the profile is finished AND
    // Either the tolerance is bypassed or in tolerance for the desired duration
    return _startedMovement && _roller.isProfileFinished()
        && (!_requireInTolerance
            || _roller.checkToleranceTime(
                _roller.isRollerInTolerance(_lowerBoundRot, _upperBoundRot),
                _requiredInToleranceDurationS
            ));
  }

  /**
   * Constructor with default to hold position after
   *
   * @param roller       Tagalong Subsystem containing a roller microsystem
   * @param goalPosition Goal roller position
   */
  public RollToCmd(T roller, Angle goalPosition) {
    this(roller, goalPosition, true);
  }

  /**
   * Constructor with default to hold position after
   *
   * @param id           The roller integer ID
   * @param roller       Tagalong Subsystem containing a roller microsystem
   * @param goalPosition Goal roller position
   */
  public RollToCmd(int id, T roller, Angle goalPosition) {
    this(id, roller, goalPosition, true);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param roller            Tagalong Subsystem containing a roller microsystem
   * @param goalPosition Goal roller position
   * @param holdPositionAfter If the roller should hold position when the command
   *                          completes
   *
   */
  public RollToCmd(T roller, Angle goalPosition, boolean holdPositionAfter) {
    this(roller, goalPosition, holdPositionAfter, roller.getRoller()._maxVelocityRPS);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                The roller integer ID
   * @param roller            Tagalong Subsystem containing a roller microsystem
   * @param goalPosition Goal roller position
   * @param holdPositionAfter If the roller should hold position when the command
   *                          completes
   *
   */
  public RollToCmd(int id, T roller, Angle goalPosition, boolean holdPositionAfter) {
    this(id, roller, goalPosition, holdPositionAfter, roller.getRoller(id)._maxVelocityRPS);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param roller            Tagalong Subsystem containing a roller microsystem
   * @param goalPosition Goal roller position
   * @param holdPositionAfter If the roller should hold position when the command
   *                          completes
   * @param maxVelocityRPS    The maximum velocity of the roller, in rotations per
   *                          second, during this command
   */

  public RollToCmd(T roller, Angle goalPosition, boolean holdPositionAfter, double maxVelocityRPS) {
    this(
        roller,
        goalPosition,
        holdPositionAfter,
        maxVelocityRPS,
        roller.getRoller()._defaultRollerLowerToleranceRot,
        roller.getRoller()._defaultRollerUpperToleranceRot
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                The roller integer ID
   * @param roller            Tagalong Subsystem containing a roller microsystem
   * @param goalPosition      Goal roller position
   * @param holdPositionAfter If the roller should hold position when the command completes
   * @param maxVelocityRPS    The maximum velocity of the roller, in rotations per second, during
   *     this command
   */

  public RollToCmd(
      int id, T roller, Angle goalPosition, boolean holdPositionAfter, double maxVelocityRPS
  ) {
    this(
        id,
        roller,
        goalPosition,
        holdPositionAfter,
        maxVelocityRPS,
        roller.getRoller(id)._defaultRollerLowerToleranceRot,
        roller.getRoller(id)._defaultRollerUpperToleranceRot
    );
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param roller            Tagalong Subsystem containing a roller microsystem
   * @param goalPosition Goal roller position
   * @param holdPositionAfter If the roller should hold position when the command completes
   * @param maxVelocityRPS    The maximum velocity of the roller, in rotations per second, during
   *     this command
   * @param toleranceRot      The number of rotations short of or beyond the target position the
   *     roller can be while still being considered in tolerance
   */

  public RollToCmd(
      T roller,
      Angle goalPosition,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double toleranceRot
  ) {
    this(roller, goalPosition, holdPositionAfter, maxVelocityRPS, toleranceRot, toleranceRot);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param id                The roller integer ID
   * @param roller            Tagalong Subsystem containing a roller microsystem
   * @param goalPosition Goal roller position
   * @param holdPositionAfter If the roller should hold position when the command completes
   * @param maxVelocityRPS    The maximum velocity of the roller, in rotations per second, during
   *     this command
   * @param toleranceRot      The number of rotations short of or beyond the target position the
   *     roller can be while still being considered in tolerance
   */
  public RollToCmd(
      int id,
      T roller,
      Angle goalPosition,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double toleranceRot
  ) {
    this(id, roller, goalPosition, holdPositionAfter, maxVelocityRPS, toleranceRot, toleranceRot);
  }

  /**
   * Constructor that creates the command with the below parameters.
   *
   * @param roller            Tagalong Subsystem containing a roller microsystem
   * @param goalPosition Goal roller position
   * @param holdPositionAfter If the roller should hold position when the command completes
   * @param maxVelocityRPS    The maximum velocity of the roller, in rotations per second, during
   *     this command
   * @param lowerToleranceRot The number of rotations short of the target position the roller can be
   *     while still being considered in tolerance
   * @param upperToleranceRot The number of rotations beyond the target position the roller can be
   *     while still being considered in tolerance
   */

  public RollToCmd(
      T roller,
      Angle goalPosition,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceRot,
      double upperToleranceRot
  ) {
    this(
        roller,
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
   * @param id                The roller integer ID
   * @param roller            Tagalong Subsystem containing a roller microsystem
   * @param goalPosition Goal roller position
   * @param holdPositionAfter If the roller should hold position when the command
   *                          completes
   * @param maxVelocityRPS    The maximum velocity of the roller, in rotations per
   *                          second, during this command
   * @param lowerToleranceRot The number of rotations short of the target position
   *                          the roller can be
   *                          while still being considered in tolerance
   * @param upperToleranceRot The number of rotations beyond the target position
   *                          the roller can be
   *                          while still being considered in tolerance
   */

  public RollToCmd(
      int id,
      T roller,
      Angle goalPosition,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceRot,
      double upperToleranceRot
  ) {
    this(
        id,
        roller,
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
   * @param roller                       Tagalong Subsystem containing a roller
   *                                     microsystem
   * @param goalPosition Goal roller position
   * @param holdPositionAfter            If the roller should hold position when
   *                                     the command completes
   * @param maxVelocityRPS               The maximum velocity of the roller, in
   *                                     rotations per second, during this
   *                                     command
   * @param upperToleranceRot            The number of rotations beyond the target
   *                                     position the roller can be
   *                                     while still being considered in tolerance
   * @param lowerToleranceRot            The number of rotations short of the
   *                                     target position the roller can be
   *                                     while still being considered in tolerance
   * @param requiredInToleranceDurationS The number of seconds that being in
   *                                     tolerance is required for
   */
  public RollToCmd(
      T roller,
      Angle goalPosition,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceRot,
      double upperToleranceRot,
      double requiredInToleranceDurationS
  ) {
    this(
        roller,
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
   * @param id                           The roller integer ID
   * @param roller                       Tagalong Subsystem containing a roller
   *                                     microsystem
   * @param goalPosition Goal roller position
   * @param holdPositionAfter            If the roller should hold position when
   *                                     the command completes
   * @param maxVelocityRPS               The maximum velocity of the roller, in
   *                                     rotations per second, during this
   *                                     command
   * @param lowerToleranceRot            The number of rotations short of the
   *                                     target position the roller can be
   *                                     while still being considered in tolerance
   * @param upperToleranceRot            The number of rotations beyond the target
   *                                     position the roller can be
   *                                     while still being considered in tolerance
   * @param requiredInToleranceDurationS The number of seconds that being in
   *                                     tolerance is required for
   */
  public RollToCmd(
      int id,
      T roller,
      Angle goalPosition,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceRot,
      double upperToleranceRot,
      double requiredInToleranceDurationS
  ) {
    this(
        id,
        roller,
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
   * @param roller                       Tagalong Subsystem containing a roller
   *                                     microsystem
   * @param goalPosition Goal roller position
   * @param holdPositionAfter            If the roller should hold position when
   *                                     the command completes
   * @param maxVelocityRPS               The maximum velocity of the roller, in
   *                                     rotations per second, during this
   *                                     command
   * @param lowerToleranceRot            The number of rotations short of the
   *                                     target position the roller can be
   *                                     while still being considered in tolerance
   * @param upperToleranceRot            The number of rotations beyond the target
   *                                     position the roller can be
   *                                     while still being considered in tolerance
   * @param requiredInToleranceDurationS The number of seconds that being in
   *                                     tolerance is required for
   */
  public RollToCmd(
      T roller,
      double goalPosition,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceRot,
      double upperToleranceRot,
      double requiredInToleranceDurationS
  ) {
    _roller = roller.getRoller();
    _goalPositionRot = goalPosition;
    _holdPositionAfter = holdPositionAfter;
    _maxVelocityRPS = maxVelocityRPS;
    _lowerBoundRot = _goalPositionRot - Math.abs(lowerToleranceRot);
    _upperBoundRot = _goalPositionRot + Math.abs(upperToleranceRot);
    _requiredInToleranceDurationS = requiredInToleranceDurationS;
    _requireInTolerance = _requiredInToleranceDurationS >= 0.0;

    addRequirements(roller);
  }

  /**
   * Full constructor, allows for double for goal in rare cases of advance use
   * needing a direct way to interact
   *
   * @param id                           The roller integer ID
   * @param roller                       Tagalong Subsystem containing a roller
   *                                     microsystem
   * @param goalPosition Goal roller position
   * @param holdPositionAfter            If the roller should hold position when
   *                                     the command completes
   * @param maxVelocityRPS               The maximum velocity of the roller, in
   *                                     rotations per second, during this
   *                                     command
   * @param lowerToleranceRot            The number of rotations short of the
   *                                     target position the roller can be
   *                                     while still being considered in tolerance
   * @param upperToleranceRot            The number of rotations beyond the target
   *                                     position the roller can be
   *                                     while still being considered in tolerance
   * @param requiredInToleranceDurationS The number of seconds that being in
   *                                     tolerance is required for
   */
  public RollToCmd(
      int id,
      T roller,
      double goalPosition,
      boolean holdPositionAfter,
      double maxVelocityRPS,
      double lowerToleranceRot,
      double upperToleranceRot,
      double requiredInToleranceDurationS
  ) {
    _roller = roller.getRoller(id);
    _goalPositionRot = goalPosition;
    _holdPositionAfter = holdPositionAfter;
    _maxVelocityRPS = maxVelocityRPS;
    _lowerBoundRot = _goalPositionRot - Math.abs(lowerToleranceRot);
    _upperBoundRot = _goalPositionRot + Math.abs(upperToleranceRot);
    _requiredInToleranceDurationS = requiredInToleranceDurationS;
    _requireInTolerance = _requiredInToleranceDurationS >= 0.0;

    addRequirements(roller);
  }
}
