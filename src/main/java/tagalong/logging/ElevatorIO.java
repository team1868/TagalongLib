package tagalong.logging;

import org.littletonrobotics.junction.AutoLog;

/**
 * Interface with elevator hardware
 */
public interface ElevatorIO {
  /**
   * Set of loggable elevator inputs
   */
  @AutoLog
  public static class ElevatorIOInputs {
    /**
     * Height of the elevator in meters
     */
    public double elevatorHeightM = 0.0;
    /**
     * Velocity of the elevator in meters per second
     */
    public double elevatorVelocityMPS = 0.0;
    /**
     * Applied (output) motor voltage of the elevator
     */
    public double elevatorAppliedVolts = 0.0;
    /**
     * Current corresponding to the stator windings of the elevator
     */
    public double elevatorCurrentAmps = 0.0;
  }

  /**
   * Updates the set of loggable elevator inputs
   *
   * @param inputs elevator inputs
   */
  public default void updateInputs(ElevatorIOInputs inputs) {}
}
