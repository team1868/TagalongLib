package tagalong.logging;

import java.lang.Cloneable;
import java.lang.Override;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

/**
 * Logger for elevator inputs
 */
public class ElevatorIOInputsAutoLogged
    extends ElevatorIO.ElevatorIOInputs implements LoggableInputs, Cloneable {
  @Override
  public void toLog(LogTable table) {
    table.put("ElevatorHeightM", elevatorHeightM, "meters");
    table.put("ElevatorVelocityMPS", elevatorVelocityMPS, "meters/second");
    table.put("ElevatorAppliedVolts", elevatorAppliedVolts, "volts");
    table.put("ElevatorCurrentAmps", elevatorCurrentAmps, "amps");
  }

  @Override
  public void fromLog(LogTable table) {
    elevatorHeightM = table.get("ElevatorHeightM", elevatorHeightM);
    elevatorVelocityMPS = table.get("ElevatorVelocityMPS", elevatorVelocityMPS);
    elevatorAppliedVolts = table.get("ElevatorAppliedVolts", elevatorAppliedVolts);
    elevatorCurrentAmps = table.get("ElevatorCurrentAmps", elevatorCurrentAmps);
  }

  /**
   * Returns a copy of ElevatorIOInputsAutoLogged
   */
  public ElevatorIOInputsAutoLogged clone() {
    ElevatorIOInputsAutoLogged copy = new ElevatorIOInputsAutoLogged();
    copy.elevatorHeightM = this.elevatorHeightM;
    copy.elevatorVelocityMPS = this.elevatorVelocityMPS;
    copy.elevatorAppliedVolts = this.elevatorAppliedVolts;
    copy.elevatorCurrentAmps = this.elevatorCurrentAmps;
    return copy;
  }
}
