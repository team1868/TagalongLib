## Low Inertia Roller Tuning

### Context

`Applied Voltage = +/- Ks + Kv * desiredVelocity + Ka * desiredAcceleration`

* **Ks** \= the voltage necessary to overcome static friction, applied directionally in line with desired motion  
* **Kv** \= the voltage necessary to maintain 1 rotation per second of velocity  
* **Ka** \= the voltage necessary to generate 1 rotation per second^2 of acceleration  
  * Ka \= 0 and is ignored for reasons discussed in our FeedForward Primer (TODO LINK)

### Stage 1: Find Ks

Use shuffleboard to change the Ks value on the fly without re-deploying code\! The feedforward constants will update every time the robot is disabled and then re-enabled.

1. Put the roller microsystem in feedforward tuning mode by adding it to the static `ffTuningMicrosystems` list in the `TagalongConfigurations` class  
2. 0 all PIDSGVA values in the roller microsystem configuration file  
3. Deploy the robot code  
4. Use the following procedure to find **Ks**  
   - Apply **increasing** voltages to find the maximum voltage that does not create roller movement  
      - The resulting value is Ks  
      - Use shuffleboard to change the Ks value on the fly without re-deploying code\! The feedforward constants will update every time the robot is disabled and then re-enabled.    
      - Because the microsystem is in feedforward tuning mode, Ks will automatically be applied to the motors.

### Stage 2: Find Kv

1. In shuffleboard, set Ks to an arbitrary but safe voltage larger than Ks. This voltage will be referred to as **refVoltage**  
   - Choosing a smart refVoltage is very specific to the weight and friction of the roller system. Assuming there is not a lot of friction in the system, we usually use `Ks + 1` as the reference voltage.  
   - Enable the robot and allow the roller to rotate under the power of the applied refVoltage before disabling.  
      - We encourage teams to use [glass](https://docs.wpilib.org/en/stable/docs/software/dashboards/glass/introduction.html) to graph and view the logged velocity of the roller throughout this period. Shuffleboard can also graph the logged velocity, but it is much harder to view, record values from, and cannot be paused.  
      - Using the logged velocity data, look for a plateau where the velocity of the roller hits and holds a steady maximum. Measure and record that velocity as the **plateauVelocity**  
2. Derive Kv  
   - `refVoltage = Ks + Kv * plateauVelocity`  
   - `Kv = (refVoltage - Ks) / plateauVelocity`

### Stage 3: Finishing Up

1. In the roller microsystem configuration file, populate the `PIDSGVA` values with the Ks and Kv values accordingly  
2. Take the roller microsystem out of the `ffTuningMicrosystems` list  
3. Test the roller using the roller microsystem’s functions that return velocity commands `setRollerRPSCmd()` (TODO link)  
4. Test the roller using the Tagalong base command `RotateToCmd` (TODO link)  
5. Tune and add a P (proportional) factor to the `PIDSGVA` values to help close the loop, making sure to differentiate between the positional `PIDSGVA` in `slot0` and the velocity `PIDSGVA` in `slot1`. (expanded guides that cover this coming soon)