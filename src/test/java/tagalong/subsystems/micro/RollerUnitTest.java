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
import tagalong.subsystems.micro.Roller;
import tagalong.subsystems.micro.confs.RollerConf;

public class RollerUnitTest {
  static final double DELTA = 0.0;
  public Roller _roller;
  public RollerConf _rollerConf;

  public final double powerTolerance = 1.0; // change
  public final double velocityTolerance = 1.0; // change
  public final double rotationTolerance = 1.0; // change
  public final double angleToleranceRot = 1.0;

  @BeforeEach
  void setUp() {
    _roller = new Roller(_rollerConf);
  }

  @AfterEach
  void shutdown() throws Exception {
    _roller.setBrakeMode(true); // need for rollers?
  }

  @Test
  public void testPower(Roller roller, double power) {
    roller.setRollerPower(power);
    assertEquals(powerTolerance, Math.abs(roller.getRollerPower() - power));
  }

  @Test
  public void testVelocity(Roller roller, double velocity) {
    roller.setRollerVelocity(velocity, false); // change w/ FF?
    assertEquals(velocityTolerance, Math.abs(roller.getRollerVelocity() - velocity));
  }

  @Test
  public void testProfileRot(Roller roller, double goalPositionRot) {
    roller.setRollerProfile(goalPositionRot);
    assertEquals(rotationTolerance, Math.abs(roller.getRollerPosition()) - goalPositionRot);
  }

  @Test
  public void testProfileAngle(Roller roller, Angle goalAngle) {
    roller.setRollerProfile(goalAngle);
    assertEquals(
        angleToleranceRot, Math.abs(roller.getRollerPosition() - goalAngle.getRotations())
    );
  }

  @Test
  public void testProfileRotComplex(
      Roller roller,
      double goalPositionRot,
      double goalVelocityMPS,
      double maxVelocityMPS,
      double maxAccelerationMPS2,
      boolean setCurrentState
  ) {
    roller.setRollerProfile(
        goalPositionRot, goalVelocityMPS, maxVelocityMPS, maxAccelerationMPS2, true
    );
    assertEquals(rotationTolerance, Math.abs(goalPositionRot - roller.getRollerPosition()));
  }
  public void testProfileAngComplex(
      Roller roller,
      double goalPositionRot,
      double goalVelocityMPS,
      double maxVelocityMPS,
      double maxAccelerationMPS2,
      boolean setCurrentState
  ) {
    roller.setRollerProfile(
        goalPositionRot, goalVelocityMPS, maxVelocityMPS, maxAccelerationMPS2, true
    );
    assertEquals(rotationTolerance, Math.abs(goalPositionRot - roller.getRollerPosition()));
  }
}