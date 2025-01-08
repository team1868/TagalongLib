package tagalong.logging;

import java.lang.Cloneable;
import java.lang.Override;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.inputs.LoggableInputs;

public class PivotIOInputsAutoLogged
    extends PivotIO.PivotIOInputs implements LoggableInputs, Cloneable {
  @Override
  public void toLog(LogTable table) {
    table.put("PivotPositionRot", pivotPositionRot);
    table.put("PivotVelocityRPS", pivotVelocityRPS);
    table.put("PivotAppliedVolts", pivotAppliedVolts);
    table.put("PivotCurrentAmps", pivotCurrentAmps);
  }

  @Override
  public void fromLog(LogTable table) {
    pivotPositionRot = table.get("PivotPositionRot", pivotPositionRot);
    pivotVelocityRPS = table.get("PivotVelocityRPS", pivotVelocityRPS);
    pivotAppliedVolts = table.get("PivotAppliedVolts", pivotAppliedVolts);
    pivotCurrentAmps = table.get("PivotCurrentAmps", pivotCurrentAmps);
  }

  public PivotIOInputsAutoLogged clone() {
    PivotIOInputsAutoLogged copy = new PivotIOInputsAutoLogged();
    copy.pivotPositionRot = this.pivotPositionRot;
    copy.pivotVelocityRPS = this.pivotVelocityRPS;
    copy.pivotAppliedVolts = this.pivotAppliedVolts;
    copy.pivotCurrentAmps = this.pivotCurrentAmps;
    return copy;
  }
}
