package tagalong.subsystems.micro;
import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import java.nio.file.attribute.GroupPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tagalong.devices.Encoders;
import tagalong.devices.Motors;
import tagalong.measurements.Angle;
import tagalong.subsystems.micro.Pivot;
import tagalong.subsystems.micro.confs.PivotConf;

// are we testing the endocer for pivot or the reefarm pivot itself...

public class PivotUnitTest {
  static final double kTol = 2e-1; // idk what these r for....
  static final double kCANCoderDelayTime = 0.5; // same here
  public Pivot _pivot;
  public PivotConf _pivotConf;
  public final double powerTolerance = 1.0; // CHANGE
  public final double velocityTolerance = 1.0; // change
  public final double angleToleranceDeg = 1.0; // change

  @BeforeEach
  void setUp() {
    _pivot = new Pivot(_pivotConf);
  }

  @AfterEach
  void shutdown() throws Exception {
    _pivot.setBrakeMode(true);
  }

  @Test
  public void testPower(Pivot pivot, double power) {
    pivot.setPivotPower(power);
    assertEquals(powerTolerance, Math.abs(power - pivot.getPivotPower()));
  }

  @Test
  public void testVelocity(Pivot pivot, double velocity) {
    pivot.setPivotVelocity(velocity, false);
    assertEquals(velocityTolerance, Math.abs(Math.abs(velocity - pivot.getPivotVelocity())));
  }

  @Test
  public void testProfileAngle(Pivot pivot, Angle goalPosition) {
    pivot.setPivotProfile(goalPosition);
    assertEquals(angleToleranceDeg, Math.abs(pivot.getPivotPosition() - goalPosition.getDegrees()));
  }
}
