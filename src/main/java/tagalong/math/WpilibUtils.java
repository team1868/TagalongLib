package tagalong.math;

import edu.wpi.first.math.geometry.Pose2d;

/**
 * Utility functions for interacting with WPI Lib classes
 */
public class WpilibUtils {
  /**
   * Converts Pose 2d into an array
   *
   * @param pose Pose2d
   * @param arr  Array to set with Pose2d values
   */
  public static void poseToArray(Pose2d pose, double[] arr) {
    arr[0] = pose.getX();
    arr[1] = pose.getY();
    arr[2] = pose.getRotation().getDegrees();
  }
}
