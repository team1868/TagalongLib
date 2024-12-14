## Tagalong "Base" Commands

TagalongLib includes pre-written commands that cover all core functionality in the package tagalong.commands.base.

> IMPORTANT: Tagalong base commands require the subsystem containing the microsystem being controlled. When running multiple Tagalong base commands that require the same subsystem in parallel you **MUST** use the tagalong `anonymize()` decorator to remove the subsystem requirement from the base command.  
>
> To ensure that the composed command still requires the subsystem in the end, do one of the following:
>
> - Leave at least one base command in the parallel group with the subsystem required  
> - `anonymize()` all base commands in the parallel group and [manually add the requirement](https://github.wpilib.org/allwpilib/docs/release/java/edu/wpi/first/wpilibj2/command/Command.html#addRequirements\(edu.wpi.first.wpilibj2.command.Subsystem...\))
>
<!-- > [Examples can be found below](./INCLUDED_COMMANDS.md#command-composition-examples) -->

### Positional control

* Move To Commands (`ElevateToCmd`, `PivotToCmd`, `RollToCmd`)  
  * Move to commands use FF augmented by PID to move the roller to the specified location while respecting positional and trapezoidal limits.  
* Move X (`ElevateXCmd`, `PivotXCmd`, `RollXCmd`)  
  * Move X commands move the microsystem by a specified amount relative to its current position  
* Dynamic (`ElevateToDynamic`, `PivotToDynamicAbsolute`, `PivotToDynamic`, `RollToDynamic`)  
  * Dynamic commands use external inputs to continuously adjust the target position of the microsystem  
* Move To Absolute (`PivotToAbsoluteCmd`)  
  * Used for pivot microsystems with \>360 degrees of rotation. This mode moves the pivot to the specified location, but applies a modulus to the rotation (i.e. treating 0 degrees and 360 degrees as equivalent positions). It then chooses the shortest path to that location rather than potentially rotating more 180 degrees to end up in the same position.

### Velocity Control

Only the roller microsystem has built in methods that return velocity control commands via FF, PID, or percent power control.

* Start end style commands with no ending condition, but ensure the rollers stop when the command is interrupted  
  * startEndRollerRPSCmd – set rotations per second, but use pure PID  
  * startEndRollerRPSWithFFCmd – set rotations per second, but use feedforward to augment the pid  
  * startEndRollerPowerCmd – set percent power  
* Corresponding simple commands that do not stop the motor when they are eventually interrupted  
  * setRollerRPSCmd  
  * setRollerRPSWithFFCmd  
  * setRollerPowerCmd

### Specialized Control  
  * Elevator Zero  
    * Finds and sets the elevator 0 by carefully stalling out the elevator at the bottom of the mechanism’s range of motion. The stall is then detected and the integrated motor encoder position is set to 0 accordingly.  
  * Aim At (PivotAimAtCmd, RollerAimAtCmd, PivotAimAtCompCmd, RollerAimAtCompCmd)  
    * Commands that continuously points the microsystem at a given 2d target.  
    * With Yaw Compensation  

<!-- 
FUTURE DEV
## Command Composition Examples
-->