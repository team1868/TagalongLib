package tagalong.logging;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;
import tagalong.subsystems.micro.Elevator;

public class ElevatorIOTalonFX implements ElevatorIO {
  private final Elevator _elevator;
  private final StatusSignal<Angle> _elevatorHeight;
  private final StatusSignal<AngularVelocity> _elevatorVelocity;
  private final StatusSignal<Voltage> _elevatorAppliedVolts;
  private final StatusSignal<Current> _elevatorCurrentAmps;

  public ElevatorIOTalonFX(Elevator elevator) {
    _elevator = elevator;
    _elevatorHeight = elevator.getPrimaryMotor().getPosition();
    _elevatorVelocity = elevator.getPrimaryMotor().getVelocity();
    _elevatorAppliedVolts = elevator.getPrimaryMotor().getMotorVoltage();
    _elevatorCurrentAmps = elevator.getPrimaryMotor().getStatorCurrent();
    BaseStatusSignal.setUpdateFrequencyForAll(
        50.0, _elevatorHeight, _elevatorVelocity, _elevatorAppliedVolts, _elevatorCurrentAmps
    );
  }
  @Override
  public void updateInputs(ElevatorIOInputs inputs) {
    BaseStatusSignal.refreshAll(
        _elevatorHeight, _elevatorVelocity, _elevatorAppliedVolts, _elevatorCurrentAmps
    );
    inputs.elevatorHeightM = _elevator.motorToMeters(_elevatorHeight.getValueAsDouble());
    inputs.elevatorVelocityMPS = _elevator.motorToMeters(_elevatorVelocity.getValueAsDouble());
    inputs.elevatorAppliedVolts = _elevatorAppliedVolts.getValueAsDouble();
    inputs.elevatorCurrentAmps = _elevatorCurrentAmps.getValueAsDouble();
  }
}
