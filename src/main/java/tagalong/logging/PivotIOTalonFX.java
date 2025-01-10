package tagalong.logging;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;
import tagalong.subsystems.micro.Pivot;

/**
 * Collection of pivot TalonFX data
 */
public class PivotIOTalonFX implements PivotIO {
  /**
   * Signal for pivot position in rotations
   */
  private final StatusSignal<Angle> _pivotPosition;
  /**
   * Signal for pivot velocity in rotations per second
   */
  private final StatusSignal<AngularVelocity> _pivotVelocity;
  /**
   * Signal for pivot applied (output) motor voltage
   */
  private final StatusSignal<Voltage> _pivotAppliedVolts;
  /**
   * Signal for pivot current corresponding to the stator windings
   */
  private final StatusSignal<Current> _pivotCurrentAmps;

  /**
   * Constructs a layer with the below pivot TalonFX data
   *
   * @param pivot microsystem
   */
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
