package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

@IID("{2A99020A-E325-4454-95E0-136726ED4818}")
public interface IDeviceInfo extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Returns the DeviceID for this Device
   * </p>
   * <p>
   * Getter method for the COM property "DeviceID"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  java.lang.String deviceID();


  /**
   * <p>
   * Returns the Type of Device
   * </p>
   * <p>
   * Getter method for the COM property "Type"
   * </p>
   * @return  Returns a value of type WiaDeviceType
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  WiaDeviceType type();


  /**
   * <p>
   * A collection of all properties for this imaging device that are applicable when the device is not connected
   * </p>
   * <p>
   * Getter method for the COM property "Properties"
   * </p>
   * @return  Returns a value of type IProperties
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  IProperties properties();


  @VTID(9)
  @ReturnValue(defaultPropertyThrough={IProperties.class})
  IProperty properties(
    java.lang.Object index);

  /**
   * <p>
   * Establish a connection with this device and return a Device object
   * </p>
   * @return  Returns a value of type IDevice
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  IDevice connect();


  // Properties:
}
