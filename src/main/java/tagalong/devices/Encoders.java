/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.devices;

/**
 * Generic encoder or encoder like sensors identifier enum
 */
public enum Encoders implements CanDeviceInterface {
  /**
   * CTRE CANcoder
   */
  CANCODER(4096), // 12-bit measurement
  /**
   * CTRE PIGEON2 -- Yaw used as encoder value
   */
  PIGEON2_YAW(65536), // 16-bit measurement
  /**
   * CTRE PIGEON2 -- Pitch used as encoder value
   */
  PIGEON2_PITCH(65536), // 16-bit measurement
  /**
   * CTRE PIGEON2 -- Roll used as encoder value
   */
  PIGEON2_ROLL(65536); // 16-bit measurement

  /**
   * Sensor value increments (ticks) per a rotation of the sensor
   */
  public final int _ticksPerRotation;

  /**
   *
   * @param ticksPerRotation Sensor value increments per a rotation of the sensor
   */
  Encoders(int ticksPerRotation) {
    _ticksPerRotation = ticksPerRotation;
  }
}
