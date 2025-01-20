package tagalong.subsystems.micro;
import static org.junit.jupiter.api.Assertions.*;

import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tagalong.devices.Encoders;
import tagalong.devices.Motors;
import tagalong.subsystems.micro.Elevator;
import tagalong.subsystems.micro.confs.ElevatorConf;

// a work in progress plspls ignore for now if u see this its kind of all over the place - caitlin

// TODO: add values for instance variables
// TODO: make tolerance possible for over and under target pos
// TODO: fix methods
// TODO: figure out assertEquals logic
// TODO: test

public class ElevatorUnitTests {
  public double _tolerance = 0.0; // stub
  public Elevator _elevator;
  public ElevatorConf _elevatorConf;
  private double _goalPosition;
  private double _maxVelocityMPS;

  protected TalonFX _driveMotor;
  public int _riveMotorID = 12; // stub

  public ElevatorUnitTests() {}

  @BeforeEach
  void setup() {
    _elevator = new Elevator(_elevatorConf);
  }

  @AfterEach
  void shutdown() throws Exception {
    _elevator.setBrakeMode(true);
  }

  @Test
  void doesntWorkWhenClosed() {
    _elevator.setBrakeMode(true);
    _elevator.setElevatorProfile(_goalPosition, _maxVelocityMPS);
    assertEquals(
        _driveMotor.getPosition().getValueAsDouble(), Math.abs(_tolerance - _goalPosition)
    );
  }

  @Test
  void worksWhenOpen() {
    _elevator.setElevatorProfile(_goalPosition, _maxVelocityMPS);
    _elevator.setElevatorHeight(0.5);
    assertEquals(_driveMotor.getPosition(), Math.abs(_tolerance - _goalPosition));
  }

  @Test
  void retractTest() {
    _elevator.setBrakeMode(true);
    assertEquals(_driveMotor.getPosition(), Math.abs(_tolerance - _goalPosition));
  }

  @Test
  void deployTest() {
    _elevator.setElevatorProfile(_goalPosition, _maxVelocityMPS);
    assertEquals(_driveMotor.getPosition(), Math.abs(_tolerance - _goalPosition));
  }
}