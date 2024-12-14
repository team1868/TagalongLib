## Elevator Tuning

### Context

`Applied Voltage = +/- Ks + Kg + Kv * desiredVelocity + Ka * desiredAcceleration`

* **Ks** \= the voltage necessary to overcome static friction, applied directionally in line with desired motion  
* **Kg** \= voltage necessary to compensate for gravity pulling the elevator down  
* **Kv** \= the voltage necessary to maintain 1 meter per second of velocity  
* **Ka** \= the voltage necessary to generate 1 meter per second^2 of acceleration  
  * Ka \= 0 and is ignored for reasons discussed in our FeedForward Primer (TODO LINK)

### Stage 1: Find Kg and Ks

Use shuffleboard to change the Ks value on the fly without re-deploying code\! The feedforward constants will update every time the robot is disabled and then re-enabled.

1. Put the elevator microsystem in feedforward tuning mode by adding it to the static `ffTuningMicrosystems` list in the `TagalongConfigurations` class  
2. 0 all PIDSGVA values in the elevator microsystem configuration file  
3. Deploy the robot code  
4. Use the following procedure to find **Vmax and Vmin**  
   - Pull the elevator into the middle of the travel  
   - Apply **increasing** voltages to find the maximum voltage that does not create movement upwards  
      - `Vmax = Ks + Kg`  
      - Use shuffleboard to change the Ks value on the fly without re-deploying code\! The feedforward constants will update every time the robot is disabled and then re-enabled.    
      - Because the microsystem is in feedforward tuning mode, Ks will automatically be applied to the motors.  
   - Apply **decreasing** voltages to find the minimum voltage that does not create movement at the refAngle  
      - `Vmin = -Ks + Kg`  
   - Note that depending on the design, Vmin can be positive or negative and, in some extreme cases, Vmax can be negative.  
5. Derive Ks and Kg  
   - `Vmax = Ks + Kg`  
   - `Vmin = -Ks + Kg`  
   - `Kg = (Vmin + Vmin) / 2`  
   - `Ks = (Vmax - Vmin) / 2`

### Stage 2: Verify Kg and find Kv

1. In shuffleboard, set Ks to the Kg value derived in the previous step  
   - Ensure the other `PIDSGVA` values are set to 0  
2. Enable the robot and manually move the elevator up and down  
   - It should require roughly the same force to move the elevator up or down and, assuming there is not significant friction in the system, feel roughly neutral.  
3. Disable the robot and, in shuffleboard, set `Ks` to an arbitrary but safe voltage larger than `Ks + Kg`. This voltage will be referred to as **refVoltage**  
   - Choosing a smart refVoltage is very specific to the weight and friction of the elevator. Assuming there is not a lot of friction in the system, we usually use `Ks + Kg + 1` as the reference voltage.  
   - Enable the robot and allow the elevator to extend under the power of the applied refVoltage before disabling. Make sure to disable the robot before the elevator hits the end of its range\!  
      - We encourage teams to use [glass](https://docs.wpilib.org/en/stable/docs/software/dashboards/glass/introduction.html) to graph and view the logged velocity of the elevator throughout this period. Shuffleboard can also graph the logged velocity, but it is much harder to view, record values from, and cannot be paused.  
      - Using the logged velocity data, look for a plateau where the velocity of the elevator hits and holds a steady maximum. Measure and record that velocity as the **plateauVelocity**  
4. Derive Kv  
   - `refVoltage = Ks + Kg + Kv * plateauVelocity`  
   - `Kv = (refVoltage - Ks - Kg) / plateauVelocity`

### Stage 3: Finishing Up

1. In the elevator microsystem configuration file, populate the `PIDSGVA` values with the `Ks`, `Kv`, and `Kg` values accordingly  
2. Take the elevator microsystem out of the `ffTuningMicrosystems` list  
3. Test the elevator using the Tagalong base command `ElevateToCmd` (TODO link)  
4. Tune and add a P (proportional) factor to the `PIDSGVA` values to help close the loop. (expanded guides that cover this coming soon)