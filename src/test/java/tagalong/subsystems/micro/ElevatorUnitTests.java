package tagalong.subsystems.micro;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tagalong.devices.Encoders;
import tagalong.devices.Motors;
import tagalong.measurements.Height;
import tagalong.subsystems.micro.Elevator;
import tagalong.subsystems.micro.confs.ElevatorConf;

public class ElevatorUnitTests {
  public double kTol = 2e-1; // stub
  static final double kCANCoderDelayTime = 0.5; // stub
  public ElevatorConf _elevatorConf;
  public Elevator _elevator;
  private final double _heightTolerance = 1.0; // stub
  private final double _velocityTolerance = 1.0; // stub

  @BeforeEach
  void setup() {
    _elevator = new Elevator(_elevatorConf);
  }

  @AfterEach
  void shutdown() throws Exception {
    _elevator.setBrakeMode(true);
  }

  @Test
  public void testHeight(Elevator elevator, double height) {
    elevator.setElevatorHeight(height);
    assertEquals(_heightTolerance, Math.abs(height - elevator.getElevatorHeightM()));
  }
  @Test
  public void testVelocity(Elevator elevator, double velocity, boolean withFF) {
    elevator.setElevatorVelocity(velocity, withFF);
    assertEquals(
        _velocityTolerance, Math.abs(Math.abs(velocity - elevator.getElevatorVelocityMPS()))
    );
  }
  @Test
  public void testProfile(Elevator elevator, double height) {
    elevator.setElevatorProfile(height);
    assertEquals(_heightTolerance, Math.abs(elevator.getElevatorHeightM()) - height);
  }
  @Test
  public void testProfileComplex(
      Elevator elevator,
      double height,
      double goalVelocityMPS,
      double maxVelocityMPS,
      double maxAccelerationMPS2,
      boolean setCurrentState
  ) {
    elevator.setElevatorProfile(height, goalVelocityMPS, maxVelocityMPS, maxAccelerationMPS2, true);
    assertEquals(_heightTolerance, Math.abs(height - elevator.getElevatorHeightM()));
  }
}