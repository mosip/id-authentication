package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

@IID("{696F2367-6619-49BD-BA96-904DC2609990}")
public interface IVector extends Com4jObject,Iterable<Com4jObject> {
  // Methods:
  /**
   * <p>
   * Returns/Sets the specified item in the vector by position
   * </p>
   * <p>
   * Getter method for the COM property "Item"
   * </p>
   * @param index Mandatory int parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(7)
  @DefaultMethod
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object item(
    int index);


  /**
   * <p>
   * Returns/Sets the specified item in the vector by position
   * </p>
   * <p>
   * Setter method for the COM property "Item"
   * </p>
   * @param index Mandatory int parameter.
   * @param pResult Mandatory java.lang.Object parameter.
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(8)
  @DefaultMethod
  void item(
    int index,
    java.lang.Object pResult);


  /**
   * <p>
   * Returns the number of members in the vector
   * </p>
   * <p>
   * Getter method for the COM property "Count"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(10)
  int count();


  /**
   * <p>
   * If the Vector of Bytes contains an image file, then Width and Height are ignored. Otherwise a Vector of Bytes must be RGB data and a Vector of Longs must be ARGB data. Returns a Picture object on success. See the ImageFile method for more details.
   * </p>
   * <p>
   * Getter method for the COM property "Picture"
   * </p>
   * @param width Optional parameter. Default value is 0
   * @param height Optional parameter. Default value is 0
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(11)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object picture(
    @Optional @DefaultValue("0") int width,
    @Optional @DefaultValue("0") int height);


  /**
   * <p>
   * Used to get the Thumbnail property of an ImageFile which is an image file, The thumbnail property of an Item which is RGB data, or creating an ImageFile from raw ARGB data. Returns an ImageFile object on success. See the Picture method for more details.
   * </p>
   * <p>
   * Getter method for the COM property "ImageFile"
   * </p>
   * @param width Optional parameter. Default value is 0
   * @param height Optional parameter. Default value is 0
   * @return  Returns a value of type IImageFile
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(12)
  IImageFile imageFile(
    @Optional @DefaultValue("0") int width,
    @Optional @DefaultValue("0") int height);


  /**
   * <p>
   * Returns/Sets the Vector of Bytes as an array of bytes
   * </p>
   * <p>
   * Getter method for the COM property "BinaryData"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(13)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object binaryData();


  /**
   * <p>
   * Returns/Sets the Vector of Bytes as an array of bytes
   * </p>
   * <p>
   * Setter method for the COM property "BinaryData"
   * </p>
   * @param pvResult Mandatory java.lang.Object parameter.
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(14)
  void binaryData(
    java.lang.Object pvResult);


  /**
   * <p>
   * Returns a Vector of Bytes as a String
   * </p>
   * <p>
   * Getter method for the COM property "String"
   * </p>
   * @param unicode Optional parameter. Default value is false
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(15)
  java.lang.String string(
    @Optional @DefaultValue("-1") boolean unicode);


  /**
   * <p>
   * Returns/Sets the Vector of Integers from a Date
   * </p>
   * <p>
   * Getter method for the COM property "Date"
   * </p>
   * @return  Returns a value of type java.util.Date
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(16)
  java.util.Date date();


  /**
   * <p>
   * Returns/Sets the Vector of Integers from a Date
   * </p>
   * <p>
   * Setter method for the COM property "Date"
   * </p>
   * @param pdResult Mandatory java.util.Date parameter.
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(17)
  void date(
    java.util.Date pdResult);


  /**
   * <p>
   * Getter method for the COM property "_NewEnum"
   * </p>
   */

  @DISPID(-4) //= 0xfffffffc. The runtime will prefer the VTID if present
  @VTID(18)
  java.util.Iterator<Com4jObject> iterator();

  /**
   * <p>
   * If Index is not zero, Inserts a new element into the Vector collection before the specified Index. If Index is zero, Appends a new element to the Vector collection.
   * </p>
   * @param value Mandatory java.lang.Object parameter.
   * @param index Optional parameter. Default value is 0
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(19)
  void add(
    java.lang.Object value,
    @Optional @DefaultValue("0") int index);


  /**
   * <p>
   * Removes the designated element and returns it if successful
   * </p>
   * @param index Mandatory int parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(20)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object remove(
    int index);


  /**
   * <p>
   * Removes all elements.
   * </p>
   */

  @DISPID(9) //= 0x9. The runtime will prefer the VTID if present
  @VTID(21)
  void clear();


  /**
   * <p>
   * Stores the string Value into the Vector of Bytes including the NULL terminator. Value may be truncated unless Resizable is True. The string will be stored as an ANSI string unless Unicode is True, in which case it will be stored as a Unicode string.
   * </p>
   * @param value Mandatory java.lang.String parameter.
   * @param resizable Optional parameter. Default value is false
   * @param unicode Optional parameter. Default value is false
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(22)
  void setFromString(
    java.lang.String value,
    @Optional @DefaultValue("-1") boolean resizable,
    @Optional @DefaultValue("-1") boolean unicode);


  // Properties:
}
