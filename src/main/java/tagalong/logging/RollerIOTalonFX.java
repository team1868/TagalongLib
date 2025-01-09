package tagalong.logging;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;
import tagalong.subsystems.micro.Roller;

public class RollerIOTalonFX implements RollerIO {
  private final StatusSignal<Angle> _rollerPosition;
  private final StatusSignal<AngularVelocity> _rollerVelocity;
  private final StatusSignal<Voltage> _rollerAppliedVolts;
  private final StatusSignal<Current> _rollerCurrentAmps;

  public RollerIOTalonFX(Roller roller) {
    _rollerPosition = roller.getPrimaryMotor().getPosition();
    _rollerVelocity = roller.getPrimaryMotor().getVelocity();
    _rollerAppliedVolts = roller.getPrimaryMotor().getMotorVoltage();
    _rollerCurrentAmps = roller.getPrimaryMotor().getStatorCurrent();
    BaseStatusSignal.setUpdateFrequencyForAll(
        50.0, _rollerPosition, _rollerVelocity, _rollerAppliedVolts, _rollerCurrentAmps
    );
  }
  @Override
  public void updateInputs(RollerIOInputs inputs) {
    BaseStatusSignal.refreshAll(
        _rollerPosition, _rollerVelocity, _rollerAppliedVolts, _rollerCurrentAmps
    );
    inputs.rollerPositionRot = _rollerPosition.getValueAsDouble();
    inputs.rollerVelocityRPS = _rollerVelocity.getValueAsDouble();
    inputs.rollerAppliedVolts = _rollerAppliedVolts.getValueAsDouble();
    inputs.rollerCurrentAmps = _rollerCurrentAmps.getValueAsDouble();
  }
}
