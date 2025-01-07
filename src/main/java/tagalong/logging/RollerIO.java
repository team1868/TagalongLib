package tagalong.logging;

import org.littletonrobotics.junction.AutoLog;

public interface RollerIO {
  @AutoLog
  public static class RollerIOInputs {
    public double rollerPositionRot = 0.0;
    public double rollerVelocityRPS = 0.0;
    public double rollerAppliedVolts = 0.0;
    public double rollerCurrentAmps = 0.0;
  }

  /** Updates the set of loggable inputs. */
  public default void updateInputs(RollerIOInputs inputs) {}
}
