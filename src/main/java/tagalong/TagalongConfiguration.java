/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong;

import edu.wpi.first.wpilibj.TimedRobot;
import java.util.ArrayList;
import java.util.List;

/**
 * Tagalong configurations, must be configured before subsystems are constructed
 * on boot.
 */
public class TagalongConfiguration {
  /**
   * The command schedulers loop time, the following line of code must be put into Robot.java's
   * constructor if using a non-standard loop time!
   *
   * ```
   * TagalongConfiguration.LOOP_PERIOD_S = this.getPeriod();
   * ```
   */
  public static double LOOP_PERIOD_S = TimedRobot.kDefaultPeriod;

  public static final boolean IS_REPLAY = false;

  /**
   * Add microsystem names to shuffleboardMicrosystems list if they should be
   * logged via shuffleboard entries.
   */
  public static final List<String> shuffleboardMicrosystems = new ArrayList<>();
  /**
   * Add microsystem names to pidTuningMicrosystems list to put them into PID
   * tuning mode and logged on shuffleboard accordingly.
   */
  public static final List<String> pidTuningMicrosystems = new ArrayList<>();
  /**
   * Add microsystem names to pidTuningMicrosystems list to put them into
   * FeedForward tuning mode and logged on shuffleboard accordingly.
   */
  public static final List<String> ffTuningMicrosystems = new ArrayList<>();
}
