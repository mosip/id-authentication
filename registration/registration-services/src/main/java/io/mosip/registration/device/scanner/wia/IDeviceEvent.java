package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

@IID("{80D0880A-BB10-4722-82D1-07DC8DA157E2}")
public interface IDeviceEvent extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Returns the EventID for this Event
   * </p>
   * <p>
   * Getter method for the COM property "EventID"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  java.lang.String eventID();


  /**
   * <p>
   * Returns the Type of this Event
   * </p>
   * <p>
   * Getter method for the COM property "Type"
   * </p>
   * @return  Returns a value of type WiaEventFlag
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  WiaEventFlag type();


  /**
   * <p>
   * Returns the event Name
   * </p>
   * <p>
   * Getter method for the COM property "Name"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  java.lang.String name();


  /**
   * <p>
   * Returns the event Description
   * </p>
   * <p>
   * Getter method for the COM property "Description"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  java.lang.String description();


  // Properties:
}
