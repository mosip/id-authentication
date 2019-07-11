package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

@IID("{73856D9A-2720-487A-A584-21D5774E9D0F}")
public interface IDeviceManager extends Com4jObject {
  // Methods:
  /**
   * <p>
   * A collection of all imaging devices connected to this computer
   * </p>
   * <p>
   * Getter method for the COM property "DeviceInfos"
   * </p>
   * @return  Returns a value of type IDeviceInfos
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  IDeviceInfos deviceInfos();


  @VTID(7)
  @ReturnValue(defaultPropertyThrough={IDeviceInfos.class})
  IDeviceInfo deviceInfos(
    java.lang.Object index);

  /**
   * <p>
   * Registers the specified EventID for the specified DeviceID. If DeviceID is "*" then OnEvent will be called whenever the event specified occurs for any device. Otherwise, OnEvent will only be called if the event specified occurs on the device specified.
   * </p>
   * @param eventID Mandatory java.lang.String parameter.
   * @param deviceID Optional parameter. Default value is "*"
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  void registerEvent(
    java.lang.String eventID,
    @Optional @DefaultValue("*") java.lang.String deviceID);


  /**
   * <p>
   * Unregisters the specified EventID for the specified DeviceID. UnregisterEvent should only be called for EventID and DeviceID for which you called RegisterEvent.
   * </p>
   * @param eventID Mandatory java.lang.String parameter.
   * @param deviceID Optional parameter. Default value is "*"
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  void unregisterEvent(
    java.lang.String eventID,
    @Optional @DefaultValue("*") java.lang.String deviceID);


  /**
   * <p>
   * Registers the specified Command to launch when the specified EventID for the specified DeviceID occurs. Command can be either a ClassID or the full path name and the appropriate command-line arguments needed to invoke the application.
   * </p>
   * @param command Mandatory java.lang.String parameter.
   * @param name Mandatory java.lang.String parameter.
   * @param description Mandatory java.lang.String parameter.
   * @param icon Mandatory java.lang.String parameter.
   * @param eventID Mandatory java.lang.String parameter.
   * @param deviceID Optional parameter. Default value is "*"
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  void registerPersistentEvent(
    java.lang.String command,
    java.lang.String name,
    java.lang.String description,
    java.lang.String icon,
    java.lang.String eventID,
    @Optional @DefaultValue("*") java.lang.String deviceID);


  /**
   * <p>
   * Unregisters the specified Command for the specified EventID for the specified DeviceID. UnregisterPersistentEvent should only be called for the Command, Name, Description, Icon, EventID and DeviceID for which you called RegisterPersistentEvent.
   * </p>
   * @param command Mandatory java.lang.String parameter.
   * @param name Mandatory java.lang.String parameter.
   * @param description Mandatory java.lang.String parameter.
   * @param icon Mandatory java.lang.String parameter.
   * @param eventID Mandatory java.lang.String parameter.
   * @param deviceID Optional parameter. Default value is "*"
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  void unregisterPersistentEvent(
    java.lang.String command,
    java.lang.String name,
    java.lang.String description,
    java.lang.String icon,
    java.lang.String eventID,
    @Optional @DefaultValue("*") java.lang.String deviceID);


  // Properties:
}
