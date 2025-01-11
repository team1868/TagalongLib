# TagalongLib: A Companion Library for Composable Subsystems 

Developed by FRC Team #1868 and Girls Scout Troop #62868 the Space Cookies. Named after one of our favorite Girl Scout cookies and in the hope this library tags-a-long with us for years to come\!

## Detailed TagalongLib Information and Resources
[For a feature list, summary of project architecture, detailed tuning guides, and more please refer to our resources hub.](resources/README.md).

[Java API Docs](https://team1868.github.io/TagalongLibDocs/)

## Download & Installation
TagalongLib is a standalone library that packages its dependencies into its release jar. When using TagalongLib as a standalone library, add it to your project using the vendor dependency file `TagalongLib.json` found in the [TagalongLib releases](https://github.com/team1868/TagalongLib/releases). Please also insert the following code snippet into your project's build.gradle:

```
repositories {
  maven {
    url = uri("https://maven.pkg.github.com/team1868/TagalongLib")
    credentials {
        username = "space-cookies-bot"
        password = "\u0067\u0068\u0070\u005f\u0032\u0041\u0058\u0052\u007a\u005a\u006a\u0072\u0054\u0074\u0070\u0047\u0049\u004d\u007a\u0063\u0039\u006d\u0050\u0037\u0056\u007a\u0076\u0079\u006e\u0052\u006c\u0061\u0053\u0043\u0031\u0068\u0068\u004a\u006b\u0055"
    }
  }
}
```

## FRC Robot Code Integration
We encourage everyone to use the [Tagalong Robot Builder](https://github.com/team1868/TagalongRobotBuilder) for automatic generation and integration of Tagalong based subsystems.

<!-- 
FUTURE DEV
## Example Projects and Repositories
-->

## FAQ

### Who should use TagalongLib?

We believe Tagalong has utility for every team, either by speeding up their mechanism testing process or replacing their subsystem core entirely. However, we believe that any team that has not utilized feedforward control with trapezoidal motion profiles effectively in the past would benefit the most from using Tagalong.

### What languages do you support?

We currently only support Java. We don't currently have plans to support C++ or any of the common FRC languages, but we will consider adding additional language support if there is demand for it.

### What kind of hardware do you support?

Right now we only support the brushless motors from CTRE (Falcon, Kraken x60, and Kraken X44) in FOC and non-FOC mode. We struggled to replicate the predictable functionality that we expect of ourselves with REV motors and controller API’s. Ultimately we simply did not feel comfortable providing semi-functional code that could put robots and mechanisms at risk.

### Does Tagalong only support feedforward control?

Tagalong uses closed loop PID to augment feedforward control. If you would like to use profiled PID instead, you can simply set all the feedforward constants to 0\.

<!-- 
FUTURE DEV: List out microsystems and their specialized variants
### What functionalities are currently supported?
-->

### What functionalities are coming soon?
- 2025 vendor package support
- More included configurations for Microsystem subtypes
- Pivot variant without a fused encoder or mechanically zeroed
- Simulation based unit tests
- AdvantageKit logging for RobotBuilder subsystems
- Support for treating generated subsystems as singletons

### Where can I get support for TagalongLib?
Check out our [release notes](https://github.com/team1868/TagalongLib/releases) and [repository issues](https://github.com/team1868/TagalongLib/issues) for your problem! Don’t see your issue there? Open an issue or feature request against [TagalongRobotBuilder](https://github.com/team1868/tagalongRobotBuilder/issues) for issues with generating subsystem code and [TagalongLib](https://github.com/team1868/TagalongLib/issues) for issues related to underlying functionality! 
<!-- FUTURE DEV: Add a common issues document to the resources directory -->

## Contributing
Please see our [guide to contributing to TagalongLib](resources/CONTRIBUTING.md)
<!-- 
FUTURE DEV: Add section covering
## Repository Structure
-->
## Commitment
The Space Cookies understand that it is hard to rely on and build on top of new open source projects without commitments for long term support. At this time, we are willing to commit to improving and maintaining these projects until the new control system is released – [currently slated for the 2027 FRC season](https://community.firstinspires.org/future-robot-control-system-update).

## Versioning
Due to the required pairing of specific WPILib versions and other dependency versions to a TagalongLib version, the version number uses the following format:

> `(WPILibVersion in format YYYY.MajorMinor).MMDD`

For example, a TagalongLib release on January 1st, 2025 using WPILib version 2024.1.0 would be version `2025.10.0101`.
