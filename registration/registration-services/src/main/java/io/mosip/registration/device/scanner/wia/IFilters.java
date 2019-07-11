package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

@IID("{C82FFED4-0A8D-4F85-B90A-AC8E720D39C1}")
public interface IFilters extends Com4jObject,Iterable<Com4jObject> {
  // Methods:
  /**
   * <p>
   * Returns the specified item in the collection by position or FilterID
   * </p>
   * <p>
   * Getter method for the COM property "Item"
   * </p>
   * @param index Mandatory int parameter.
   * @return  Returns a value of type IFilter
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(7)
  @DefaultMethod
  IFilter item(
    int index);


  /**
   * <p>
   * Returns the number of members in the collection
   * </p>
   * <p>
   * Getter method for the COM property "Count"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(8)
  int count();


  /**
   * <p>
   * Getter method for the COM property "_NewEnum"
   * </p>
   */

  @DISPID(-4) //= 0xfffffffc. The runtime will prefer the VTID if present
  @VTID(9)
  java.util.Iterator<Com4jObject> iterator();

  /**
   * <p>
   * Appends/Inserts a new Filter of the specified FilterID into a Filter collection
   * </p>
   * @param filterID Mandatory java.lang.String parameter.
   * @param index Optional parameter. Default value is 0
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(10)
  void add(
    java.lang.String filterID,
    @Optional @DefaultValue("0") int index);


  /**
   * <p>
   * Removes the designated filter
   * </p>
   * @param index Mandatory int parameter.
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(11)
  void remove(
    int index);


  // Properties:
}
