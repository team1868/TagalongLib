/**
 * Copyright 2024 The Space Cookies : Girl Scout Troop #62868 and FRC Team #1868
 * Open Source Software; you may modify and/or share it under the terms of
 * the 3-Clause BSD License found in the root directory of this project.
 */

package tagalong.math;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import org.junit.jupiter.api.Test;

public class GeometricUtilsTest {
  @Test
  public void inRangeTest() {
    assertEquals(
        true,
        GeometricUtils.inRange(
            new Pose2d(3.0, 5.0, Rotation2d.fromDegrees(0)),
            Rotation2d.fromDegrees(40.0),
            new Pose2d(3.0, 5.0, Rotation2d.fromDegrees(40.0)),
            0.01,
            Math.PI / 2056.0
        )
    );
  }
}
