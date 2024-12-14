package tagalong.subsystems.micro;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import tagalong.subsystems.micro.confs.MicrosystemConf;

public class MicrosystemConfigurationTest {
  @Test
  public void gearRatioTest() {
    int[][] testRatio = new int[][] {new int[] {14, 48}, new int[] {24, 48}};
    assertEquals(6.857142857142857, MicrosystemConf.calculateGearRatio(testRatio));
  }
}
