package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

@IID("{706038DC-9F4B-4E45-88E2-5EB7D665B815}")
public interface IProperty extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Returns/Sets the Property Value
   * </p>
   * <p>
   * Getter method for the COM property "Value"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(7)
  @DefaultMethod
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object value();


  /**
   * <p>
   * Returns/Sets the Property Value
   * </p>
   * <p>
   * Setter method for the COM property "Value"
   * </p>
   * @param pvResult Mandatory java.lang.Object parameter.
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(8)
  @DefaultMethod
  void value(
    java.lang.Object pvResult);


  /**
   * <p>
   * Returns the Property Name
   * </p>
   * <p>
   * Getter method for the COM property "Name"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(10)
  java.lang.String name();


  /**
   * <p>
   * Returns the PropertyID of this Property
   * </p>
   * <p>
   * Getter method for the COM property "PropertyID"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(11)
  int propertyID();


  /**
   * <p>
   * Returns either a WiaPropertyType or a WiaImagePropertyType
   * </p>
   * <p>
   * Getter method for the COM property "Type"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(12)
  int type();


  /**
   * <p>
   * Indicates whether the Property Value is read only
   * </p>
   * <p>
   * Getter method for the COM property "IsReadOnly"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(13)
  boolean isReadOnly();


  /**
   * <p>
   * Indicates whether the Property Value is a vector
   * </p>
   * <p>
   * Getter method for the COM property "IsVector"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(14)
  boolean isVector();


  /**
   * <p>
   * Returns the SubType of the Property, if any
   * </p>
   * <p>
   * Getter method for the COM property "SubType"
   * </p>
   * @return  Returns a value of type WiaSubType
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(15)
  WiaSubType subType();


  /**
   * <p>
   * Returns the default Property Value if the SubType is not UnspecifiedSubType
   * </p>
   * <p>
   * Getter method for the COM property "SubTypeDefault"
   * </p>
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(16)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object subTypeDefault();


  /**
   * <p>
   * Returns a Vector of valid Property Values if the SubType is ListSubType or valid flag Values that can be ored together if the SubType is FlagSubType
   * </p>
   * <p>
   * Getter method for the COM property "SubTypeValues"
   * </p>
   * @return  Returns a value of type IVector
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(17)
  IVector subTypeValues();


  @VTID(17)
  @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={IVector.class})
  java.lang.Object subTypeValues(
    int index);

  /**
   * <p>
   * Returns the minimum valid Property Value if the SubType is RangeSubType
   * </p>
   * <p>
   * Getter method for the COM property "SubTypeMin"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(9) //= 0x9. The runtime will prefer the VTID if present
  @VTID(18)
  int subTypeMin();


  /**
   * <p>
   * Returns the maximum valid Property Value if the SubType is RangeSubType
   * </p>
   * <p>
   * Getter method for the COM property "SubTypeMax"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(19)
  int subTypeMax();


  /**
   * <p>
   * Returns the step increment of Property Values if the SubType is RangeSubType
   * </p>
   * <p>
   * Getter method for the COM property "SubTypeStep"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(11) //= 0xb. The runtime will prefer the VTID if present
  @VTID(20)
  int subTypeStep();


  // Properties:
}
