## Arm Tuning

This guide assumes the magnet offset in the cancoder has already been properly configured

### Context

`Applied Voltage = +/- Ks + Kg * cos(armAngle + comOffset) + Kv * desiredVelocity + Ka * desiredAcceleration`

* **Ks** \= the voltage necessary to overcome static friction, applied directionally in line with desired motion  
* **Kg** \= voltage necessary to compensate for gravity when arm is horizontal  
* **Kv** \= the voltage necessary to maintain 1 rotation per second of velocity  
* **Ka** \= the voltage necessary to generate 1 rotation per second^2 of acceleration  
  * Ka \= 0 and is ignored for reasons discussed in our FeedForward Primer (TODO LINK)  
* **armAngle** \= the arm’s current angle  
* **comOffset** \= the center of mass’s angle when the arm is at its horizontal position  
  * This is not strictly necessary, but the offset is mechanically defined and allows the arm to be placed in an easily measurable position when determining the 0-position

### Stage 1: Find Kg, Ks, and comOffset

Use shuffleboard to change the Ks value on the fly without re-deploying code\! The feedforward constants will update every time the robot is disabled and then re-enabled. In the case of the pivot microsystem, when in feedforward tuning mode experimental voltages are automatically applied to assist in tuning the arm. This includes a dynamic voltage application that applies voltage dynamically

1. Put the pivot microsystem in feedforward tuning mode by adding it to the static `ffTuningMicrosystems` list in the `TagalongConfigurations` class  
2. 0 all PIDSGVA values in the pivot microsystem configuration file  
3. Deploy the robot code  
4. Use the following procedure to find **Vmax and Vmin**  
   - Choose an **easily recreatable** arm angle and record the angle. We will call refer to this angle as **refAngle**  
      - The refAngle **must be identical** each time  
      - We recommend using a digital level that can be found cheaply online or at hardware stores. In a pinch a phone can be used, but we do not recommend it because the camera, buttons, and case create an inconsistent measurement surface.  
   - Apply **increasing** voltages to find the maximum voltage that does not create movement at the refAngle  
      - Use shuffleboard to change the Ks value on the fly without re-deploying code\! The feedforward constants will update every time the robot is disabled and then re-enabled.    
      - Because the microsystem is in feedforward tuning mode, `Ks + Kg * cos(refAngle + comOffset)` will automatically be applied to the motors.  
   - Apply **decreasing** voltages to find the minimum voltage that does not create movement at the refAngle  
      - `Vmin = -Ks + Kg * cos(refAngle + comOffset)`  
5. Derive Ks  
   - `Kg * cos(refAngle + comOffset)` cancels out when finding Ks  
      - `Vmax - Ks = Kg * cos(refAngle + comOffset)`  
      - `Vmin + Ks = Kg * cos(refAngle + comOffset)`  
      - `Vmax - Ks = Vmin + Ks`  
      - `Ks = (Vmax - Vmin) / 2`  
6. Derive Kg and comOffset  
   - Substituting for Ks, leaves the following system of equations  
      - `Kg * cos(refAngle + comOffset) = Vmax - Ks`  
      - `Kg * cos(refAngle + comOffset) = Vmin + Ks`  
   - Use a calculator to solve for Kg and comOffset

### Stage 2: Verify Kg, Ks, and comOffset

1. Use the following procedure to find Kv components  
   - In the pivot microsystem configuration file  
      - Set Kg and Ks as G and S in the `PIDSGVA` constants. At least slot 0, though we recommend all 3 slot configurations.  
   - Set the `ffOffsetValue` configuration to the comOffset value, make sure to also change `ffOffsetUnit` to match your measurement units  
   - Redeploy robot code, making sure to keep the microsystem in the `ffTuningMicrosystems` list  
   - In shuffleboard, zero the PIDSGVA values except for G which should be set to the derived value  
   - With the robot enabled, **carefully** manually rotate the arm to verify it feels consistent and "neutral" throughout its range of motion  
   - Disable the robot and, in shuffleboard, set Ks to an arbitrary but safe voltage larger than Ks. This voltage will be referred to as **refVoltage**  
      - Choosing a smart refVoltage is very specific to the weight, friction, and range of the arm. Assuming there is not a lot of friction in the system, we usually use a value several times larger than Ks.  
   - Enable the robot and allow the arm to rotate under the power of the applied refVoltage before disabling. Make sure to disable the robot before the arm hits the end of its range\!  
      - We encourage teams to use [glass](https://docs.wpilib.org/en/stable/docs/software/dashboards/glass/introduction.html) to graph and view the logged velocity of the arm throughout this period. Shuffleboard can also graph the logged velocity, but it is much harder to view, record values from, and cannot be paused.  
      - Using the logged velocity data, look for a plateau where the velocity of the arm hits and holds a steady maximum. Measure and record that velocity as the **plateauVelocity**  
2. Derive Kv  
   - `AppliedVoltage = Ks + Kg * cos(armAngle + comOffset) + Kv * plateauVelocity`  
   - `AppliedVoltage = refVoltage +Kg * cos(armAngle + comOffset)`  
      - `Kg * cos(armAngle + comOffset)` is applied automatically while a pivot is in feedforward tuning mode.  
   - `refVoltage = Ks + Kv * plateauVelocity`  
      - `Kg * cos(armAngle + comOffset)` terms cancel out  
   - `Kv = (refVoltage - Ks) / plateauVelocity`

### Stage 3: Finishing Up

1. In the pivot microsystem configuration file, populate the `PIDSGVA` values with the Ks, Kv, and Kg values accordingly  
2. Take the arm microsystem out of the `ffTuningMicrosystems` list  
3. Test the arm using the Tagalong base command `PivotToCmd` (TODO link)  
4. Tune and add a P (proportional) factor to the `PIDSGVA` values to help close the loop. (expanded guides that cover this and setting the encoder magnet offset coming soon)