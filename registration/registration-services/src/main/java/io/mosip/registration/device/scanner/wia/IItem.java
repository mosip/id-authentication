package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

@IID("{68F2BF12-A755-4E2B-9BCD-37A22587D078}")
public interface IItem extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Returns the ItemID for this Item
   * </p>
   * <p>
   * Getter method for the COM property "ItemID"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  java.lang.String itemID();


  /**
   * <p>
   * A collection of all properties for this item
   * </p>
   * <p>
   * Getter method for the COM property "Properties"
   * </p>
   * @return  Returns a value of type IProperties
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  IProperties properties();


  @VTID(8)
  @ReturnValue(defaultPropertyThrough={IProperties.class})
  IProperty properties(
    java.lang.Object index);

  /**
   * <p>
   * A collection of all child items for this item
   * </p>
   * <p>
   * Getter method for the COM property "Items"
   * </p>
   * @return  Returns a value of type IItems
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  IItems items();


  @VTID(9)
  @ReturnValue(defaultPropertyThrough={IItems.class})
  IItem items(
    int index);

  /**
   * <p>
   * A collection of all supported format types for this item
   * </p>
   * <p>
   * Getter method for the COM property "Formats"
   * </p>
   * @return  Returns a value of type IFormats
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  IFormats formats();


  @VTID(10)
  @ReturnValue(defaultPropertyThrough={IFormats.class})
  java.lang.String formats(
    int index);

  /**
   * <p>
   * A collection of all commands for this item
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
   * Returns the underlying IWiaItem interface for this Item object
   * </p>
   * <p>
   * Getter method for the COM property "WiaItem"
   * </p>
   * @return  Returns a value of type com4j.Com4jObject
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(12)
  com4j.Com4jObject wiaItem();


  /**
   * <p>
   * Returns an ImageFile object, in this version, in the format specified in FormatID if supported, otherwise using the preferred format for this imaging device. Future versions may return a collection of ImageFile objects.
   * </p>
   * @param formatID Optional parameter. Default value is "{00000000-0000-0000-0000-000000000000}"
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(13)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object transfer(
    @Optional @DefaultValue("{00000000-0000-0000-0000-000000000000}") java.lang.String formatID);


  /**
   * <p>
   * Issues the command specified by CommandID. CommandIDs are device dependent. Valid CommandIDs for this Item are contained in the Commands collection.
   * </p>
   * @param commandID Mandatory java.lang.String parameter.
   * @return  Returns a value of type IItem
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(14)
  IItem executeCommand(
    java.lang.String commandID);


  // Properties:
}
