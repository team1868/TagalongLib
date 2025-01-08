package tagalong.logging;

import java.lang.Cloneable;
import java.lang.Override;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

public class ElevatorIOInputsAutoLogged
    extends ElevatorIO.ElevatorIOInputs implements LoggableInputs, Cloneable {
  @Override
  public void toLog(LogTable table) {
    table.put("ElevatorHeightM", elevatorHeightM);
    table.put("ElevatorVelocityMPS", elevatorVelocityMPS);
    table.put("ElevatorAppliedVolts", elevatorAppliedVolts);
    table.put("ElevatorCurrentAmps", elevatorCurrentAmps);
  }

  @Override
  public void fromLog(LogTable table) {
    elevatorHeightM = table.get("ElevatorHeightM", elevatorHeightM);
    elevatorVelocityMPS = table.get("ElevatorVelocityMPS", elevatorVelocityMPS);
    elevatorAppliedVolts = table.get("ElevatorAppliedVolts", elevatorAppliedVolts);
    elevatorCurrentAmps = table.get("ElevatorCurrentAmps", elevatorCurrentAmps);
  }

  public ElevatorIOInputsAutoLogged clone() {
    ElevatorIOInputsAutoLogged copy = new ElevatorIOInputsAutoLogged();
    copy.elevatorHeightM = this.elevatorHeightM;
    copy.elevatorVelocityMPS = this.elevatorVelocityMPS;
    copy.elevatorAppliedVolts = this.elevatorAppliedVolts;
    copy.elevatorCurrentAmps = this.elevatorCurrentAmps;
    return copy;
  }
}
