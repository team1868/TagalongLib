/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.controls;

import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.Slot1Configs;
import com.ctre.phoenix6.configs.Slot2Configs;
import com.ctre.phoenix6.configs.SlotConfigs;

/**
 * Wrapper class for PID + FeedForward coefficients for generic reuse in CTRE
 * Devices
 */
public class PIDSGVAConstants extends PIDConstants {
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

  private final SlotConfigs genericSlotConfigs;

  /**
   *
   * @param p P Factor
   * @param i I Factor
   * @param d D Factor
   * @param s S Factor
   * @param g G Factor
   * @param v V Factor
   * @param a A Factor
   */
  public PIDSGVAConstants(double p, double i, double d, double s, double g, double v, double a) {
    super(p, i, d);
    this.s = s;
    this.g = g;
    this.v = v;
    this.a = a;
    genericSlotConfigs =
        new SlotConfigs().withKP(p).withKI(i).withKD(d).withKS(s).withKG(g).withKV(v).withKA(a);
  }

  /**
   *
   * @return Values as CTRE Device Slot 0 configuration
   */
  public Slot0Configs toCTRESlot0Configuration() {
    return Slot0Configs.from(genericSlotConfigs);
  }

  /**
   *
   * @return Values as CTRE Device Slot 1 configuration
   */
  public Slot1Configs toCTRESlot1Configuration() {
    return Slot1Configs.from(genericSlotConfigs);
  }

  /**
   *
   * @return Values as CTRE Device Slot 2 configuration
   */
  public Slot2Configs toCTRESlot2Configuration() {
    return Slot2Configs.from(genericSlotConfigs);
  }
}
