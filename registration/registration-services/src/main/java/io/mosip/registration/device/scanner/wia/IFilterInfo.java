package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

@IID("{EFD1219F-8229-4B30-809D-8F6D83341569}")
public interface IFilterInfo extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Returns the FilterInfo Name
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
   * Returns a technical Description of what the filter does and how to use it in a filter chain
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
   * Returns the FilterID for this filter
   * </p>
   * <p>
   * Getter method for the COM property "FilterID"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  java.lang.String filterID();


  // Properties:
}
