## Tuning Guides

<!-- 
FUTURE DEV:
### General PID Tuning Guide 
-->

### Mechanism Specific Guides

Please see the following guides for a more specific step-by-step walkthrough of tuning specific systems.

#### [Low Inertia Roller Tuning](./LOW_INERTIA_ROLLER_TUNING.md)

Simplest tuning case with no gravitational component (e.g. intake or handoff rollers)

<!--
#### High Inertia Roller Tuning

Coming soon\! (e.g. Flywheels and rollers with lots of internal friction)
-->

#### [Elevator Tuning](./ELEVATOR_TUNING.md)

Simple linear mechanism adding a gravitational component to the feedforward tuning.

#### [Arm Tuning](./ARM_TUNING.md)

The arm is a special case, where `Kg` is dependent on the arm’s positions. When the arm is horizontal, gravity is in full effect and the full `Kg` voltage is applied. When the arm is vertical, the arm is fully supported by the axis of rotation and no voltage is applied.

The feedforward equation becomes:

&emsp; `Voltage = (+/- Ks) + Kg * cos(theta) + Kv * velocity + Ka * acceleration`

Where theta is the angle of the arm and horizontal is the 0 position for the arm.

### A Primer on FeedForward Control

FeedForward control used in the context of Tagalong takes advantage of voltage roughly corresponding to the velocity of a DC motor. We suggest using an experimental approach to tuning the feedforward constants (also known as `gains`) found in the following [Feedforward Control](https://docs.wpilib.org/en/stable/docs/software/advanced-controls/introduction/introduction-to-feedforward.html#introduction-to-dc-motor-feedforward) equation:

> **Voltage** \= `(+/- Ks) + Kg + Kv * velocity + Ka * acceleration`  
> **Ks** \= the voltage necessary to overcome static friction, applied directionally in line with desired motion  
> **Kg** \= the voltage necessary to counteract gravity  
> **Kv** \= the voltage necessary to maintain a unit of velocity  
> **Ka** \= the voltage necessary to generate a unit of acceleration


The following is a generalized approach to tuning the feedforward constants, that is adapted to specifics of each mechanism in the tuning guides.

#### Step 1
We focus on tuning `Kg` and `Ks` by considering states such that `velocity = 0` and `acceleration = 0` to simplify the motor voltage equation by eliminating the `Kv` and `Ka` components. Conveniently, these states are when the mechanism is stationary\!

We set up a system of equations where `Vmax` is the maximum voltage input that achieves no mechanism motion and `Vmin` is the minimum voltage input that achieves no mechanism motion.

We know that `Kg + Ks` yields `Vmax` because the `Kg + Ks` by definition takes the mechanism right to the threshold of movement. The same applies for `Kg - Ks` for the opposite direction. It is worth noting that `Vmin`` can be negative depending on the mechanism.

> **Vmax** \= `Kg + Ks`   
> **Vmin** \= `Kg - Ks`  

Using the system of equations to eliminate a variable, we find that  
> **Kg** \= `(Vmax + Vmin) / 2`  
> **Ks** \= `(Vmax - Vmin) / 2` 

#### Step 2
To find `Kv` we can simply apply a certain voltage to the motor(s) and record the mechanism’s velocity. This takes advantage of the logarithmic velocity graph as the voltage available to accelerate the system decreases while the voltage necessary to maintain the current velocity increases. Eventually the system hits a steady state, maintaining a `plateauVelocity`. We can solve for `Kv` using the following equation with the `Ks` and `Kv` values solved for in step one.

> **AppliedVoltage** \= `Ks + Kg + Kv * plateauVelocity`  
> **Kv** \= `(AppliedVoltage - Ks - Kg) / plateauVelocity`

We can verify the above results by observing the plateau velocity at different voltage levels to verify the Kv result.


#### Step 3
Finally, we could apply the same principles to solve for `Ka` – applying a certain voltage to the motor(s), recording the mechanism’s acceleration, and plugging those values in along with everything we solved for above to calculate Ka. However, `Ka` is usually very small and dominated by the `Ks + Kg + Kv * velocity` terms. In our experience, the noisy and latency sensitive velocity measurements from sensors combined with the difficulty of tuning a correct `Ka` component adds more issues than it fixes. We unilaterally **discourage** inexperienced teams from using a non-zero `Ka` value without significant guidance.

#### Step 4
With perfectly tuned gains, a mechanism would follow a trapezoidal profile exactly. Unfortunately robot-to-robot contact, a mechanism hitting the field wall, a chain that slowly loosens over the course of the season are all unmapped forces and all too common in FRC. PID is used to compensate for those forces. More specifically, we recommend using a pure Proportional controller as the Integral and Derivative controllers are redundant with feedforward and usually add error into the system. Unlike pure PID control, tuning the PID that augments feedforward can be done extremely aggressively and with a "good enough" attitude because the system should theoretically already reach the target position or velocity.
