package tagalong.logging;

import org.littletonrobotics.junction.AutoLog;

/**
 * Interface with pivot hardware
 */
public interface PivotIO {
  /**
   * Set of loggable pivot inputs
   */
  @AutoLog
  public static class PivotIOInputs {
    /**
     * Position of the pivot in rotations
     */
    public double pivotPositionRot = 0.0;
    /**
     * Velocity of the pivot in rotations per second
     */
    public double pivotVelocityRPS = 0.0;
    /**
     * Applied (output) motor voltage of the pivot
     */
    public double pivotAppliedVolts = 0.0;
    /**
     * Current corresponding to the stator windings of the pivot
     */
    public double pivotCurrentAmps = 0.0;
  }

  /**
   * Updates the set of loggable pivot inputs
   *
   * @param inputs pivot inputs
   */
  public default void updateInputs(PivotIOInputs inputs) {}
}
