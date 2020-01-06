package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

@IID("{41506929-7855-4392-9E6F-98D88513E55D}")
public interface IImageProcess extends Com4jObject {
  // Methods:
  /**
   * <p>
   * A collection of all available filters
   * </p>
   * <p>
   * Getter method for the COM property "FilterInfos"
   * </p>
   * @return  Returns a value of type IFilterInfos
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  IFilterInfos filterInfos();


  @VTID(7)
  @ReturnValue(defaultPropertyThrough={IFilterInfos.class})
  IFilterInfo filterInfos(
    java.lang.Object index);

  /**
   * <p>
   * A collection of the filters to be applied in this process
   * </p>
   * <p>
   * Getter method for the COM property "Filters"
   * </p>
   * @return  Returns a value of type IFilters
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  IFilters filters();


  @VTID(8)
  @ReturnValue(defaultPropertyThrough={IFilters.class})
  IFilter filters(
    int index);

  /**
   * <p>
   * Takes the specified ImageFile and returns the new ImageFile with all the filters applied on success
   * </p>
   * @param source Mandatory IImageFile parameter.
   * @return  Returns a value of type IImageFile
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(9)
  IImageFile apply(
    IImageFile source);


  // Properties:
}
