package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

@IID("{7CF694C0-F589-451C-B56E-398B5855B05E}")
public interface IDeviceCommand extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Returns the commandID for this Command
   * </p>
   * <p>
   * Getter method for the COM property "CommandID"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  java.lang.String commandID();


  /**
   * <p>
   * Returns the command Name
   * </p>
   * <p>
   * Getter method for the COM property "Name"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  java.lang.String name();


  /**
   * <p>
   * Returns the command Description
   * </p>
   * <p>
   * Getter method for the COM property "Description"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  java.lang.String description();


  // Properties:
}
