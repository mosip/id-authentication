package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

@IID("{3714EAC4-F413-426B-B1E8-DEF2BE99EA55}")
public interface IDevice extends Com4jObject {
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
   * A collection of all properties for this imaging device
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
   * A collection of all items for this imaging device
   * </p>
   * <p>
   * Getter method for the COM property "Items"
   * </p>
   * @return  Returns a value of type IItems
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  IItems items();


  @VTID(10)
  @ReturnValue(defaultPropertyThrough={IItems.class})
  IItem items(
    int index);

  /**
   * <p>
   * A collection of all commands for this imaging device
   * </p>
   * <p>
   * Getter method for the COM property "Commands"
   * </p>
   * @return  Returns a value of type IDeviceCommands
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  IDeviceCommands commands();


  @VTID(11)
  @ReturnValue(defaultPropertyThrough={IDeviceCommands.class})
  IDeviceCommand commands(
    int index);

  /**
   * <p>
   * A collection of all events for this imaging device
   * </p>
   * <p>
   * Getter method for the COM property "Events"
   * </p>
   * @return  Returns a value of type IDeviceEvents
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(12)
  IDeviceEvents events();


  @VTID(12)
  @ReturnValue(defaultPropertyThrough={IDeviceEvents.class})
  IDeviceEvent events(
    int index);

  /**
   * <p>
   * Returns the underlying IWiaItem interface for this Device object
   * </p>
   * <p>
   * Getter method for the COM property "WiaItem"
   * </p>
   * @return  Returns a value of type com4j.Com4jObject
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(13)
  com4j.Com4jObject wiaItem();


  /**
   * <p>
   * Returns the Item object specified by ItemID if it exists
   * </p>
   * @param itemID Mandatory java.lang.String parameter.
   * @return  Returns a value of type IItem
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(14)
  IItem getItem(
    java.lang.String itemID);


  /**
   * <p>
   * Issues the command specified by CommandID to the imaging device. CommandIDs are device dependent. Valid CommandIDs for this Device are contained in the Commands collection.
   * </p>
   * @param commandID Mandatory java.lang.String parameter.
   * @return  Returns a value of type IItem
   */

  @DISPID(9) //= 0x9. The runtime will prefer the VTID if present
  @VTID(15)
  IItem executeCommand(
    java.lang.String commandID);


  // Properties:
}
