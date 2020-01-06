package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

/**
 * <p>
 * A DeviceEvent's type is composed of bits from the WiaEventFlags enumeration. You can test a DeviceEvent's type by using the AND operation with DeviceEvent.Type and a member from the WiaEventFlags enumeration.
 * </p>
 */
public enum WiaEventFlag implements ComEnum {
  /**
   * <p>
   * Indicates that the DeviceEvent is intended to notify an application that is already running that this event has occurred.
   * </p>
   * <p>
   * The value of this constant is 1
   * </p>
   */
  NotificationEvent(1),
  /**
   * <p>
   * Indicates that the DeviceEvent can, if necessary, launch an application if this event occurs.
   * </p>
   * <p>
   * The value of this constant is 2
   * </p>
   */
  ActionEvent(2),
  ;

  private final int value;
  WiaEventFlag(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
