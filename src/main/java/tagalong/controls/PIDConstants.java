/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.controls;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import tagalong.TagalongConfiguration;

/**
 * PID wrapper class for coefficients that are generically reused in multiple
 * controllers.
 */
public class PIDConstants {
  /**
   * Proportional, integral, and derivative coefficient
   */
  public final double p, i, d;

  /**
   *
   * @param p Proportional coefficient
   * @param i Integral coefficient
   * @param d Derivative coefficient
   */
  public PIDConstants(double p, double i, double d) {
    this.p = p;
    this.i = i;
    this.d = d;
  }

  /**
   *
   * @return PID values converted to an un-profiled PID controller
   */
  public PIDConstants getUnprofiledController() {
    return new PIDConstants(p, i, d);
  }

  /**
   *
   * @param constraints Profile constraints
   * @return PID values converted to an profiled PID controller
   */
  public ProfiledPIDController getProfiledController(TrapezoidProfile.Constraints constraints) {
    return new ProfiledPIDController(
        p,
        i,
        d,
        new TrapezoidProfile.Constraints(constraints.maxVelocity, constraints.maxAcceleration),
        TagalongConfiguration.LOOP_PERIOD_S
    );
  }
}
