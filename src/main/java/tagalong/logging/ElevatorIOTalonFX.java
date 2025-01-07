package tagalong.logging;

import tagalong.subsystems.micro.Elevator;

public class ElevatorIOTalonFX implements ElevatorIO {
  private final Elevator _elevator;
  public ElevatorIOTalonFX(Elevator elevator) {
    _elevator = elevator;
  }
  @Override
  public void updateInputs(ElevatorIOInputs inputs) {
    inputs.elevatorPositionRot = _elevator.getElevatorHeightM();
    inputs.elevatorVelocityRPS = _elevator.getElevatorVelocityMPS();
    inputs.elevatorAppliedVolts = _elevator.getPrimaryMotor().getMotorVoltage().getValueAsDouble();
    inputs.elevatorCurrentAmps = _elevator.getPrimaryMotor().getStatorCurrent().getValueAsDouble();
  }
}
