/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.subsystems.micro.augments;

import tagalong.subsystems.micro.Pivot;

/**
 * Subsystem augment required for all subsystems containing 1 or more pivot
 * microsystems.
 */
public interface PivotAugment {
  /**
   *
   * @return The default pivot, usually ID 0
   */
  public Pivot getPivot();

  /**
   *
   * @param id Integer ID of the desired pivot
   * @return The pivot with the given ID
   */
  public Pivot getPivot(int id);
}
