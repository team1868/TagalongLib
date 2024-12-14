/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.measurements;

/**
 * Rotational unit interface
 */
public interface Angle {
  /**
   *
   * @return Angle in degrees
   */
  public double getDegrees();

  /**
   *
   * @return Angle in radians
   */
  public double getRadians();

  /**
   *
   * @return Angle in rotations
   */
  public double getRotations();
}
