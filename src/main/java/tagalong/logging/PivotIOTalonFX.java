package tagalong.logging;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;
import tagalong.subsystems.micro.Pivot;

public class PivotIOTalonFX implements PivotIO {
  private final StatusSignal<Angle> _pivotPosition;
  private final StatusSignal<AngularVelocity> _pivotVelocity;
  private final StatusSignal<Voltage> _pivotAppliedVolts;
  private final StatusSignal<Current> _pivotCurrentAmps;

  public PivotIOTalonFX(Pivot pivot) {
    _pivotPosition = pivot.getPrimaryMotor().getPosition();
    _pivotVelocity = pivot.getPrimaryMotor().getVelocity();
    _pivotAppliedVolts = pivot.getPrimaryMotor().getMotorVoltage();
    _pivotCurrentAmps = pivot.getPrimaryMotor().getStatorCurrent();
    BaseStatusSignal.setUpdateFrequencyForAll(
        50.0, _pivotPosition, _pivotVelocity, _pivotAppliedVolts, _pivotCurrentAmps
    );
  }
  @Override
  public void updateInputs(PivotIOInputs inputs) {
    BaseStatusSignal.refreshAll(
        _pivotPosition, _pivotVelocity, _pivotAppliedVolts, _pivotCurrentAmps
    );
    inputs.pivotPositionRot = _pivotPosition.getValueAsDouble();
    inputs.pivotVelocityRPS = _pivotVelocity.getValueAsDouble();
    inputs.pivotAppliedVolts = _pivotAppliedVolts.getValueAsDouble();
    inputs.pivotCurrentAmps = _pivotCurrentAmps.getValueAsDouble();
  }
}
