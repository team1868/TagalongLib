package tagalong.logging;

import org.littletonrobotics.junction.AutoLog;

/**
 * Interface with roller hardware
 */
public interface RollerIO {
  /**
   * Set of loggable roller inputs
   */
  @AutoLog
  public static class RollerIOInputs {
    /**
     * Position of the roller in rotations
     */
    public double rollerPositionRot = 0.0;
    /**
     * Velocity of the roller in rotations per second
     */
    public double rollerVelocityRPS = 0.0;
    /**
     * Applied (output) motor voltage of the roller
     */
    public double rollerAppliedVolts = 0.0;
    /**
     * Current corresponding to the stator windings of the roller
     */
    public double rollerCurrentAmps = 0.0;
  }

  /**
   * Updates the set of loggable roller inputs
   *
   * @param inputs roller inputs
   */
  public default void updateInputs(RollerIOInputs inputs) {}
}
