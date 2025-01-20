package tagalong.subsystems.micro;
import static org.junit.jupiter.api.Assertions.*;

import com.ctre.phoenix6.hardware.CANcoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
// not sure which of these
import org.junit.jupiter.api.Test;
import tagalong.devices.Encoders;
import tagalong.devices.Motors;
import tagalong.measurements.*;
import tagalong.subsystems.micro.Pivot;
import tagalong.subsystems.micro.Roller;
import tagalong.subsystems.micro.confs.PivotConf;
import tagalong.subsystems.micro.confs.RollerConf;

public class RollerUnitTest {
  static final double DELTA = 0.0;
  private Roller _roller;
  public RollerConf rollerConf;
  private static final Motors driveMotor = Motors.FALCON500_FOC;
  private static final int driveMotorID = 12;

  static final double rollerSpeed = 0.3; // super super basic test value
  public RollerUnitTest(){};

  @BeforeEach
  void setup() {}

  @AfterEach
  void shutdown() throws Exception {
    _roller.setBrakeMode(true);
  }

  @Test
  public void testRollers() {
    assertEquals(0.0, driveMotor.getVelocity().getValueAsDouble(), DELTA);
  }
}