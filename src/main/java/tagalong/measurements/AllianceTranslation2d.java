/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.measurements;

import edu.wpi.first.math.geometry.Translation2d;

/**
 * Alliance specific Translation2d wrapper class
 */
public class AllianceTranslation2d {
  /**
   * Blue and Red alliance specific 2-dimensional translations
   */
  public final Translation2d blue, red;

  /**
   *
   * @param blue Blue 2d translation
   * @param red  Red 2d translation
   */
  public AllianceTranslation2d(Translation2d blue, Translation2d red) {
    this.blue = blue;
    this.red = red;
  }

  /**
   * @param isRed True if on red alliance
   * @return Specified alliance's Translation2d
   */
  public Translation2d get(boolean isRed) {
    return isRed ? red : blue;
  }
}
