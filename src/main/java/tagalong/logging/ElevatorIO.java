package tagalong.logging;

import org.littletonrobotics.junction.AutoLog;

public interface ElevatorIO {
  @AutoLog
  public static class ElevatorIOInputs {
    public double elevatorPositionRot = 0.0;
    public double elevatorVelocityRPS = 0.0;
    public double elevatorAppliedVolts = 0.0;
    public double elevatorCurrentAmps = 0.0;
  }

  /** Updates the set of loggable inputs. */
  public default void updateInputs(ElevatorIOInputs inputs) {}
}
