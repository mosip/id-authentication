package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

@IID("{851E9802-B338-4AB3-BB6B-6AA57CC699D0}")
public interface IFilter extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Returns the Filter Name
   * </p>
   * <p>
   * Getter method for the COM property "Name"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  java.lang.String name();


  /**
   * <p>
   * Returns a Description of what the filter does
   * </p>
   * <p>
   * Getter method for the COM property "Description"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  java.lang.String description();


  /**
   * <p>
   * Returns the FilterID for this Filter
   * </p>
   * <p>
   * Getter method for the COM property "FilterID"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  java.lang.String filterID();


  /**
   * <p>
   * A collection of all properties for this filter
   * </p>
   * <p>
   * Getter method for the COM property "Properties"
   * </p>
   * @return  Returns a value of type IProperties
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  IProperties properties();


  @VTID(10)
  @ReturnValue(defaultPropertyThrough={IProperties.class})
  IProperty properties(
    java.lang.Object index);

  // Properties:
}
