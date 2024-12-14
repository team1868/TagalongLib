
## TagalongLib: A Companion Library for Composable FRC Subsystems 

### Microsystems: Reusable Subsystem Components

Tagalong breaks down subsystem functionality into 3 "microsystems"

* Rollers – unbounded rotational systems like flywheels, intake rollers, index rollers  
* Pivots – physically constrained rotational systems like arms, turrets, and 4-bar linkages  
* Elevators – linearly extending systems like elevators, telescopes, and hoods

Each of these components can be added to a subsystem any number of times. Microsystems within a subsystem are individually addressed and configured. Microsystems also register functions into the encompassing subsystem – allowing each microsystem to operate independently while managing its own control loops.

### Motivation

From our experience developing code across numerous seasons, we have noticed common architectural and logical issues. As part of our outreach initiatives, we have had the opportunity to mentor and assist many teams facing similar issues when augmenting existing codebases like the provided Kitbot or Everybot code. After some research and experimentation throughout the 2024 season, we have developed a library that implemented core FRC functionality and would solve many of the following key concerns and problems:

- Unpredictable mechanism control  
- Disorganized constants files containing unidentifiable variables and mixed units  
- Constantly deploying new code to test small changes in value, which could be streamlined with reusable disable logic  
- Rewriting and debugging the same functionality every year, potentially introducing new bugs  
- Ineffectively utilizing simulation to verify code, causing overreliance on a completed robot and dangerous testing, potentially causing robot damage 

While Tagalong started out as a project to benefit our team, we now see it as a launching point that will help raise the bar of FRC robot code.

## Links
- [Tagalong RobotBuilder Repository](https://github.com/team1868/TagalongRobotBuilder)
- [TagalongLib Java Docs]()
- [TagalongLib Features](./README.md#tagalong-microsystem-features)
- [TagalongLib Registered Functions](./REGISTERED_FUNCTIONS.md)
- [TagalongLib Base Commands](./INCLUDED_COMMANDS.md)
- [TagalongLib Microsystem Tuning Guides](./guides/README.md)
- [Recommended Tooling](./TOOLING.md)

## Tagalong Microsystem Features
Tagalong was developed with a focus on utilizing the tools and practices professional software engineers use.

#### Reusable and Modular

Avoid reinventing the wheel and rewriting the same mechanism functionalities each season\! Compose a subsystem and then add sensors or other hardware as needed, allowing more time for command composition and autonomous development. Writing your robot code from scratch won’t get you a [5-note auto less then a week into the build season](https://www.chiefdelphi.com/t/1678-five-note-auto/449823).

#### Microsystem Simulation

Each microsystem has mechanism simulation and visualization of the simulation included, enabling testing and verification of robot code logic without a physical robot. There are even simulation specific configurations in case the simulated system does not match the physical system and needs a modified set of tuning constants.

#### Unit Testing and Debugging

Unit tests are continually being developed and added, currently in development are unit tests for commands and microsystems that use simulation to verify functionality. These tests ensure that users can trust the library functionality and reduce the scope of debugging. For example, when facing an issue programmers can focus on command logic and how the code interfaces with TagalongLib without having to dissect potential bugs in the control logic at the same time.

#### Unit Consistency and Resolution

Within TagalongLib values are consistently handled in **meters, rotations, and seconds**. Functions and variables clearly indicate units in their names and some utility functions have multiple versions for different input units. In user facing files like microsystem configurations, the user may specify the units for individual configuration variables with automatic conversion to Tagalong units done automatically and **at boot time**. Running these standardization conversions once at boot avoids issues found in other Java units implementations that cause garbage collection mid match.

#### Logging Mode

A toggle-able mode for debugging and logging that captures all data representing microsystem state is included in all microsystems. Enable this mode for a particular microsystem by adding its name to the list `shuffleboardMicrosystems` in the `TagalongConfiguration` class. While [SmartDashboard](https://docs.wpilib.org/en/stable/docs/software/dashboards/smartdashboard/smartdashboard-intro.html) is functional and provides a much more approachable user interface, we encourage teams to use [glass](https://docs.wpilib.org/en/stable/docs/software/dashboards/glass/introduction.html) when trying to monitor or graph the system state.

> Note: Due to how FRC event networking works, you should do your best to limit the number of microsystems logging via the network tables and shuffleboard while on the field at competition.

#### Tuning Modes

Discrete modes for PID and feedforward tuning make microsystem tuning easy. Put microsystems into these modes by adding their names into the `pidTuningMicrosystems` and/or `ffTuningMicrosystems` lists inside the `TagalongConfiguration` class. These modes allow you to update the corresponding robot configurations by changing the value on shuffleboard and then re-enabling the robot. Whenever these modes are active, the microsystem’s debugging mode is also activated so that test results can be logged and monitored.

#### Single File Configurations

Rather than having configurations and constants in a single file with hundreds, if not thousands, of lines -- each microsystem has its own configuration file that lays out all configurations in an easy to read, find, and compare format.

#### Logical Defaults

All configuration files include logical defaults for the specific microsystem and hardware. This helps avoid motor burnout issues, brownouts on the field, and more reliable control while still enabling user control and customization.

#### Safety Lockouts and Reset Detection

During robot initialization, each microsystem motor and sensor is verified before configuration to ensure the device is running the correct configurations and any sensors are properly synchronized to the physical state. If the device is not seen, usually due to a wiring issue with CAN or power, the microsystem has the ability to disable itself or even prevent robot code from booting for robot safety. During a match, if a motor resets due to a temporary loss of power the motor is reconfigured and the motor is stopped to avoid unpredictable and dangerous movements.

#### Subsystem or Individual Microsystem Disablement

Specific subsystems can be selectively disabled in RobotVersions by providing a `null` in place of the subsystem configuration. The same can be done for individual microsystems within a functional subsystem by replacing the microsystem configuration within that particular subsystem configuration. For individual microsystems, there’s even the ability to disable a subsystem on the fly in response to a loss of signal from motors, sensors, or even driver input. In the disabled state, microsystems and subsystems have default behavior that allows commands to complete and avoid blocking other logical steps.

#### Distinct Robot Versions

Competition and practice robots are never functionally identical, sometimes the practice bot is missing a full mechanism relative to the competition robot or is just a drive base from a previous year. Regardless of the mechanical state, it is valuable to be able to develop on multiple robots simultaneously without the fear of breaking competition robot code. Tagalong achieves this through granular subsystem and microsystem disablement and separate configuration files. This enables robots with different physical characteristics to run with the same code and share logical code paths while ensuring changes in a robot's tuning and configuration do not affect other robot’s behavior.

#### Strong Typing

Strong typing is used to help avoid issues like arguments in the wrong order and mistyped setpoint constants. While Java is hardly the only strongly typed language, passing everything as a raw double is easy and the default when writing code on the fly. Tagalong base commands use strongly typed setpoints curated in their own file and strongly type robot configurations ensure properly matched microsystem configurations.