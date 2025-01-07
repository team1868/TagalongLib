package tagalong.logging;

import tagalong.subsystems.micro.Roller;

public class RollerIOTalonFX implements RollerIO {
  private final Roller _roller;
  public RollerIOTalonFX(Roller roller) {
    _roller = roller;
  }
  @Override
  public void updateInputs(RollerIOInputs inputs) {
    inputs.rollerPositionRot = _roller.getRollerPosition();
    inputs.rollerVelocityRPS = _roller.getRollerVelocity();
    inputs.rollerAppliedVolts = _roller.getPrimaryMotor().getMotorVoltage().getValueAsDouble();
    inputs.rollerCurrentAmps = _roller.getPrimaryMotor().getStatorCurrent().getValueAsDouble();
  }
}
