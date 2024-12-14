/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Base class for all Tagalong Subsystems containing multiple microsystems
 */
public class TagalongSubsystemBase extends SubsystemBase {
  /**
   * True if the subsystem is disabled in the config files
   */
  public boolean _configuredDisable;
  /* -------- Subsystem specific disablement -------- */
  /**
   * Disablement state, for dynamic disablement configured on the fly by robot code
   */
  protected boolean _isSubsystemDisabled = true;

  /**
   * Constructs a generic subsystem with universal features
   *
   * @param conf Subsystem configuration containing all microsystem confs and any other necessary
   *     configs
   */
  public TagalongSubsystemBase(Object conf) {
    // In the future we need a more discrete function configuring this
    _configuredDisable = conf == null;
    _isSubsystemDisabled = _configuredDisable;

    // FUTURE DEV: Inject this here rather than robot builder
    // configShuffleboard();
  }

  /**
   * @param disable
   *                Sets the subsystemDisabled and configuredDisable variables to
   *                disable.
   */
  public void setDisabled(boolean disable) {
    _isSubsystemDisabled = disable;
    _configuredDisable = disable;
  }

  /**
   * @return True if the subsystem is disabled
   */
  public boolean isSubsystemDisabled() {
    return _isSubsystemDisabled;
  }

  /* -------- IO and config functions -------- */
  /**
   * Updates shuffleboard
   */
  protected void updateShuffleboard() {}

  // FUTURE DEV: Remove, or at the very least do not require as a registered function that
  // incorporates microsystem config shuffleboard values configShuffleboard();
  /**
   * Called once on robot start to configure all shuffleboard entries.
   */
  protected void configShuffleboard() {}

  /**
   * Triggers whenever the robot is autonomous or teleop enabled.
   * Calls all the onEnable functions of the contained Tagalong Microsystems and
   * takes any subsystem
   * specific actions.
   */
  public void onEnable() {}

  /**
   * Triggers whenever the robot is disabled.
   * Calls all the onDisable functions of the contained Tagalong Microsystems and
   * takes any subsystem
   * specific actions.
   */
  public void onDisable() {}

  /**
   * Called once on robot boot to initialize simulations.
   * Calls all the simulationInit functions of the contained Tagalong Microsystems
   * and takes any subsystem specific actions.
   */
  public void simulationInit() {}

  /**
   *
   * Called periodically on robot to update simulations.
   * Calls all the simulationInit functions of the contained Tagalong Microsystems
   * and takes any
   * subsystem specific actions.
   */
  public void simulationPeriodic() {}

  /**
   * Called once on robot to configure motors.
   * Calls all the configMotor() functions of the contained Tagalong Microsystems
   * and takes any
   * subsystem specific actions.
   */
  public void configMotor() {}

  /**
   * @return Subsystem status -- True if subsystem is not disabled
   */
  public boolean checkInitStatus() {
    return !_isSubsystemDisabled;
  }
}
