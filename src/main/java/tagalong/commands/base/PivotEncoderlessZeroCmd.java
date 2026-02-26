/**
 * Copyright 2024-2026 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */
package tagalong.commands.base;

import edu.wpi.first.wpilibj.Timer;
import tagalong.commands.TagalongCommand;
import tagalong.subsystems.TagalongSubsystemBase;
import tagalong.subsystems.micro.Pivot;
import tagalong.subsystems.micro.augments.PivotAugment;

/**
 * Command that finds the elevator zero position and sets the encoder position
 * to zero.
 */
public class PivotEncoderlessZeroCmd<T extends TagalongSubsystemBase & PivotAugment>
    extends TagalongCommand {
  /**
   * Pivot subsystem.
   */
  private final Pivot _pivot;
  /**
   * Pivot zeroing power in volts
   */
  private final double _powerV;
  /**
   * Angular distance traveled where the mechanism is still considered stalled
   */
  private final double _stallToleranceRot;
  /**
   * Time stalled before the system is considered zeroed in seconds
   */
  private final double _zeroingDurationS;
  /**
   * The angle of the pivot when initialized, in rotations.
   */
  private double _prevAngleRot;
  /**
   * The angle of the pivot when against the hardstop
   */
  private double _endAngleRot;
  /**
   * Timer to track the stall duration to ensure a true bottom
   */
  private Timer stallTimer = new Timer();

  /**
   * Construct the command according to the below parameters.
   *
   * @param pivot the pivot subsystem
   * @param hardstopPositionRot the angle of the pivot when against the hardstop
   * @param powerV speed and direction to drive the pivot into the hardstop
   * @param stallToleranceRot angular distance traveled where the mechanism is still considered
   *     stalled
   * @param stallDurationS time stalled before the system is considered zeroed in seconds
   */
  public PivotEncoderlessZeroCmd(
      T pivot,
      double hardstopPositionRot,
      double powerV,
      double stallToleranceRot,
      double stallDurationS
  ) {
    _pivot = pivot.getPivot();
    _powerV = powerV;
    _stallToleranceRot = stallToleranceRot;
    _zeroingDurationS = stallDurationS;
    addRequirements(pivot);
  }

  /**
   * Construct the command according to the below parameters.
   *
   * @param id       Integer ID of the elevator microsystem inside the Tagalong
   *                 Subsystem
   * @param pivot the pivot subsystem
   * @param hardstopPositionRot the angle of the pivot when against the hardstop
   * @param power speed and direction to drive the pivot into the hardstop
   * @param stallDurationS time stalled before the system is considered zeroed in seconds
   */
  public PivotEncoderlessZeroCmd(
      int id,
      T pivot,
      double hardstopPositionRot,
      double powerV,
      double stallToleranceRot,
      double stallDurationS
  ) {
    _pivot = pivot.getPivot(id);
    _powerV = powerV;
    _stallToleranceRot = stallToleranceRot;
    _zeroingDurationS = stallDurationS;
    addRequirements(pivot);
  }

  @Override
  public void initialize() {
    _pivot.setPrimaryVolts(_powerV);
    _prevAngleRot = _pivot.getPivotPosition();
    stallTimer.reset();
  }

  @Override
  public void execute() {
    double currentAngleRot = _pivot.getPivotPosition();
    if (Math.abs(_prevAngleRot - currentAngleRot) > _stallToleranceRot) {
      stallTimer.reset();
    } else {
      stallTimer.start();
    }
    _prevAngleRot = currentAngleRot;
  }

  @Override
  public void end(boolean interrupted) {
    _pivot.setPrimaryPower(0.0);
    if (!interrupted)
      _pivot.setPivotPosition(_endAngleRot);
  }

  @Override
  public boolean isFinished() {
    return stallTimer.hasElapsed(_zeroingDurationS);
  }
}
