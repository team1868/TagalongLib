/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.commands;

import edu.wpi.first.wpilibj2.command.Command;

/**
 * Tagalong extension of the WPILib Command class, adding utilities that allow for easier
 * composition of library commands
 */
public class TagalongCommand extends Command {
  /**
   * Clears the requirement list for this command. Used to enable the composition of multiple
   * Tagalong base commands
   * @return The command with its requirements removed
   */
  public TagalongCommand anonymize() {
    this.getRequirements().clear();
    return this;
  }
}
