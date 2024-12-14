/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.subsystems.micro.augments;

import tagalong.subsystems.micro.Roller;

/**
 * Subsystem augment required for all subsystems containing 1 or more roller
 * microsystems.
 */
public interface RollerAugment {
  /**
   *
   * @return The default roller, usually ID 0
   */
  public Roller getRoller();

  /**
   *
   * @param id Integer ID of the desired roller
   * @return The roller with the given ID
   */
  public Roller getRoller(int id);
}
