/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.measurements;

import edu.wpi.first.math.geometry.Pose2d;

/**
 * Alliance specific Pose2d wrapper class
 */
public class AlliancePose2d {
  /**
   * Blue and Red alliance specific 2-dimensional poses
   */
  public final Pose2d blue, red;

  /**
   *
   * @param blue 2d coordinates for blue alliance side
   * @param red  2d coordinates for red alliance side
   */
  public AlliancePose2d(Pose2d blue, Pose2d red) {
    this.blue = blue;
    this.red = red;
  }

  /**
   *
   * @param isRed True if on the red alliance
   * @return Specified alliance's Pose2d
   */
  public Pose2d get(boolean isRed) {
    return isRed ? red : blue;
  }
}
