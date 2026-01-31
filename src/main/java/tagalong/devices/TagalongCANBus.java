package tagalong.devices;

import com.ctre.phoenix6.CANBus;
import java.util.Map;

/**
 * Tagalong CANBus manager instance manager, only CTRE devices are currently registered or supported
 */
public class TagalongCANBus {
  /**
   * Map of all instances with a CTRE device registered to them
   */
  private static final Map<String, CANBus> _ctreCANBus = Map.of();

  /**
   * Gets or registers a CTRE CANBus instance with the given name
   *
   * @param canBusName Name of the CANBus
   * @return CTRE CANBus instance
   */
  public static CANBus getOrRegisterPhoenixCANBus(String canBusName) {
    CANBus bus = _ctreCANBus.get(canBusName);
    if (bus == null) {
      bus = new CANBus(canBusName);
      _ctreCANBus.put(canBusName, bus);
    }
    return bus;
  }

  /**
   * Gets the map of all registered CTRE CANBus instances
   *
   * @return Map of CANBus instances
   */
  public static Map<String, CANBus> getPhoenixCANBusMap() {
    return _ctreCANBus;
  }
}
