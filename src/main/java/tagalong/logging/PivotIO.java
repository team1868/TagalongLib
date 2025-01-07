package tagalong.logging;

import org.littletonrobotics.junction.AutoLog;

public interface PivotIO {
  @AutoLog
  public static class PivotIOInputs {
    public double pivotPositionRot = 0.0;
    public double pivotVelocityRPS = 0.0;
    public double pivotAppliedVolts = 0.0;
    public double pivotCurrentAmps = 0.0;
  }

  /** Updates the set of loggable inputs. */
  public default void updateInputs(PivotIOInputs inputs) {}

  // /** Run open loop at the specified voltage. */
  // public default void setVoltage(double leftVolts, double rightVolts) {}

  // /** Run closed loop at the specified velocity. */
  // public default void setVelocity(
  //     double leftRadPerSec, double rightRadPerSec, double leftFFVolts, double rightFFVolts
  // ) {}
}
