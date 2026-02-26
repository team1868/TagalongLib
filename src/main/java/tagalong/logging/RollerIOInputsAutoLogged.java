package tagalong.logging;

import java.lang.Cloneable;
import java.lang.Override;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

/**
 * Logger for roller inputs
 */
public class RollerIOInputsAutoLogged
    extends RollerIO.RollerIOInputs implements LoggableInputs, Cloneable {
  @Override
  public void toLog(LogTable table) {
    table.put("RollerPositionRot", rollerPositionRot, "rotations");
    table.put("RollerVelocityRPS", rollerVelocityRPS, "rps");
    table.put("RollerAppliedVolts", rollerAppliedVolts, "volts");
    table.put("RollerCurrentAmps", rollerCurrentAmps, "amps");
  }

  @Override
  public void fromLog(LogTable table) {
    rollerPositionRot = table.get("RollerPositionRot", rollerPositionRot);
    rollerVelocityRPS = table.get("RollerVelocityRPS", rollerVelocityRPS);
    rollerAppliedVolts = table.get("RollerAppliedVolts", rollerAppliedVolts);
    rollerCurrentAmps = table.get("RollerCurrentAmps", rollerCurrentAmps);
  }

  /**
   * Returns a copy of RollerIOInputsAutoLogged
   */
  public RollerIOInputsAutoLogged clone() {
    RollerIOInputsAutoLogged copy = new RollerIOInputsAutoLogged();
    copy.rollerPositionRot = this.rollerPositionRot;
    copy.rollerVelocityRPS = this.rollerVelocityRPS;
    copy.rollerAppliedVolts = this.rollerAppliedVolts;
    copy.rollerCurrentAmps = this.rollerCurrentAmps;
    return copy;
  }
}
