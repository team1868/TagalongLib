package tagalong.logging;

import java.lang.Cloneable;
import java.lang.Override;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

public class RollerIOInputsAutoLogged
    extends RollerIO.RollerIOInputs implements LoggableInputs, Cloneable {
  @Override
  public void toLog(LogTable table) {
    table.put("RollerPositionRot", rollerPositionRot);
    table.put("RollerVelocityRPS", rollerVelocityRPS);
    table.put("RollerAppliedVolts", rollerAppliedVolts);
    table.put("RollerCurrentAmps", rollerCurrentAmps);
  }

  @Override
  public void fromLog(LogTable table) {
    rollerPositionRot = table.get("RollerPositionRot", rollerPositionRot);
    rollerVelocityRPS = table.get("RollerVelocityRPS", rollerVelocityRPS);
    rollerAppliedVolts = table.get("RollerAppliedVolts", rollerAppliedVolts);
    rollerCurrentAmps = table.get("RollerCurrentAmps", rollerCurrentAmps);
  }

  public RollerIOInputsAutoLogged clone() {
    RollerIOInputsAutoLogged copy = new RollerIOInputsAutoLogged();
    copy.rollerPositionRot = this.rollerPositionRot;
    copy.rollerVelocityRPS = this.rollerVelocityRPS;
    copy.rollerAppliedVolts = this.rollerAppliedVolts;
    copy.rollerCurrentAmps = this.rollerCurrentAmps;
    return copy;
  }
}
