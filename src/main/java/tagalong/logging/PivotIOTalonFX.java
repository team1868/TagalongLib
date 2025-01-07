package tagalong.logging;

import tagalong.subsystems.micro.Pivot;

public class PivotIOTalonFX implements PivotIO {
  private final Pivot _pivot;
  public PivotIOTalonFX(Pivot pivot) {
    _pivot = pivot;
  }
  @Override
  public void updateInputs(PivotIOInputs inputs) {
    inputs.pivotPositionRot = _pivot.getPivotPosition();
    inputs.pivotVelocityRPS = _pivot.getPivotVelocity();
    inputs.pivotAppliedVolts = _pivot.getPrimaryMotor().getMotorVoltage().getValueAsDouble();
    inputs.pivotCurrentAmps = _pivot.getPrimaryMotor().getStatorCurrent().getValueAsDouble();
  }
}
