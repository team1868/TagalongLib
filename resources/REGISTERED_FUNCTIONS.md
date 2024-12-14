## Registered Functions

Tagalong Microsystems trigger the majority of their logic through functions registered in the containing subsystem. Some of the subsystem functions containing registered functions are subsequently called in the RobotContainer and Robot classes. Be very careful when modifying or moving calls to registered functions in order to avoid blocking or modifying microsystem functionality.

### periodic()
Microsystem `periodic()` called in the subsystem `periodic()` function which is automatically called by the WPILib command based infrastructure. Called every command cycle regardless of if the robot is enabled or disabled.

Detects motor and sensor power loss, reconfiguring the sensor and enforcing safe behavior. Will also follow the last motion profile if requested, in most cases this is used to hold the mechanisms position after the profile is complete.

### onEnable()
Microsystem `onEnable()` called in the subsystem `onEnable()`, which is called in the RobotContainer classes `onEnable()`, which is finally called in Robot class' `disabledExit()` function. Triggers when the robot moves into an autonomous or teleoperated enabled state.

Changes the brake mode of the motors and updates values from shuffleboard depending on the mode the microsystem is in. If in feedforward tuning mode, a constant voltage will be applied to the motors to tune different motion constants.

### onDisable()
Microsystem `onDisable()` called in the subsystem `onDisable()`, which is called in the RobotContainer classes `onDisable()`, which is finally called in Robot class' `disabledInit()` function. Triggers when the robot moves from any enabled state to disabled

Changes the brake mode of the motors accordingly.

### simulationInit()
Microsystem `simulationInit()` called in the subsystem `simulationInit()` function which is automatically called by the WPILib command based infrastructure. Called during code boot if in desktop mode with no real robot detected to ensure that the robot processor isn’t encumbered with these steps when a physical robot is being run.

Initialization actions for simulation including setting up the simulation animations that represent the microsystem.

### simulationPeriodic()
Microsystem `simulationPeriodic()` called in the subsystem `simulationPeriodic()` function which is automatically called by the WPILib command based infrastructure. The core of the simulation and called every command cycle.

Steps the physics models forward and updates the shuffleboard animations accordingly. When the microsystem is simulated and modeled, the resulting values are written back to the simulated hardware so command logic can flow.
