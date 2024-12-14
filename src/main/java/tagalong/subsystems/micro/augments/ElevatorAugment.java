/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.subsystems.micro.augments;

import tagalong.subsystems.micro.Elevator;

/**
 * Subsystem augment required for all subsystems containing 1 or more elevator
 * microsystems.
 */
public interface ElevatorAugment {
  /**
   *
   * @return The default elevator, usually ID 0
   */
  public Elevator getElevator();

  /**
   *
   * @param id Integer ID of the desired elevator
   * @return The elevator with the given ID
   */
  public Elevator getElevator(int id);
}
