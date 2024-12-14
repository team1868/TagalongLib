/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.controls;

import edu.wpi.first.math.controller.ArmFeedforward;
import edu.wpi.first.math.controller.ElevatorFeedforward;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;

/**
 * Wrapper class for FeedForward coefficients for generic reuse or conversion
 * between multiple devices
 */
public class FeedforwardConstants {
  /**
   * FeedForward coefficient S or static in volts
   */
  public final double s;
  /**
   * FeedForward coefficient G or gravity in volts
   */
  public final double g;
  /**
   * FeedForward coefficient V or velocity in volts per unit of velocity
   */
  public final double v;
  /**
   * FeedForward coefficient A or acceleration in volts per unit of acceleration
   */
  public final double a;

  /**
   * @return Constants converted to an ElevatorFeedforward
   */
  public ElevatorFeedforward getElevatorFeedforward() {
    return new ElevatorFeedforward(s, g, v, a);
  }

  /**
   * @return Constants converted to an ArmFeedforward
   */
  public ArmFeedforward getArmFeedforward() {
    return new ArmFeedforward(s, g, v, a);
  }

  /**
   *
   * @return Constants converted to a SimpleMotorFeedforward
   */
  public SimpleMotorFeedforward getSimpleMotorFeedforward() {
    return new SimpleMotorFeedforward(s, v, a);
  }

  /**
   *
   * @param s FeedForward coefficient S or static in volts
   * @param g FeedForward coefficient G or gravity in volts
   * @param v FeedForward coefficient V or velocity in volts per unit of velocity
   * @param a FeedForward coefficient A or acceleration in volts per unit of
   *          acceleration
   */
  public FeedforwardConstants(double s, double g, double v, double a) {
    this.s = s;
    this.g = g;
    this.v = v;
    this.a = a;
  }
}
