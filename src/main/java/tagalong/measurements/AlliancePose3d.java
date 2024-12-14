/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.measurements;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;

/**
 * Alliance specific Pose3d wrapper class
 */
public class AlliancePose3d {
  /**
   * Blue and Red alliance specific 3-dimensional poses
   */
  public final Pose3d blue, red;

  /**
   *
   * @param blue 3d coordinates for blue alliance side
   * @param red  3d coordinates for red alliance side
   */
  public AlliancePose3d(Pose3d blue, Pose3d red) {
    this.blue = blue;
    this.red = red;
  }

  /**
   *
   * @param isRed True if on red alliance
   * @return Specified alliance's Pose3d
   */
  public Pose3d get(boolean isRed) {
    return isRed ? red : blue;
  }

  /**
   *
   * @param isRed True if on red alliance
   * @return Specified alliance's pose3d converted to translation2d
   */
  public Translation2d getTranslation2d(boolean isRed) {
    return get(isRed).getTranslation().toTranslation2d();
  }
}
