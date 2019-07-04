package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

@IID("{B4760F13-D9F3-4DF8-94B5-D225F86EE9A1}")
public interface ICommonDialog extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Launches the Windows Scanner and Camera Wizard and returns Nothing. Future versions may return a collection of ImageFile objects.
   * </p>
   * @param device Mandatory IDevice parameter.
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object showAcquisitionWizard(
    IDevice device);


  /**
   * <p>
   * Displays one or more dialog boxes that enable the user to acquire an image from a hardware device for image acquisition and returns an ImageFile object on success, otherwise Nothing
   * </p>
   * @param deviceType Optional parameter. Default value is 0
   * @param intent Optional parameter. Default value is 0
   * @param bias Optional parameter. Default value is 131072
   * @param formatID Optional parameter. Default value is "{00000000-0000-0000-0000-000000000000}"
   * @param alwaysSelectDevice Optional parameter. Default value is false
   * @param useCommonUI Optional parameter. Default value is false
   * @param cancelError Optional parameter. Default value is false
   * @return  Returns a value of type IImageFile
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  IImageFile showAcquireImage(
    @Optional @DefaultValue("0") WiaDeviceType deviceType,
    @Optional @DefaultValue("0") WiaImageIntent intent,
    @Optional @DefaultValue("131072") WiaImageBias bias,
    @Optional @DefaultValue("{00000000-0000-0000-0000-000000000000}") java.lang.String formatID,
    @Optional @DefaultValue("0") boolean alwaysSelectDevice,
    @Optional @DefaultValue("-1") boolean useCommonUI,
    @Optional @DefaultValue("0") boolean cancelError);


  /**
   * <p>
   * Displays a dialog box that enables the user to select a hardware device for image acquisition. Returns the selected Device object on success, otherwise Nothing
   * </p>
   * @param deviceType Optional parameter. Default value is 0
   * @param alwaysSelectDevice Optional parameter. Default value is false
   * @param cancelError Optional parameter. Default value is false
   * @return  Returns a value of type IDevice
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  IDevice showSelectDevice(
    @Optional @DefaultValue("0") WiaDeviceType deviceType,
    @Optional @DefaultValue("0") boolean alwaysSelectDevice,
    @Optional @DefaultValue("0") boolean cancelError);


  /**
   * <p>
   * Displays a dialog box that enables the user to select an item for transfer from a hardware device for image acquisition. Returns the selection as an Items collection on success, otherwise Nothing
   * </p>
   * @param device Mandatory IDevice parameter.
   * @param intent Optional parameter. Default value is 0
   * @param bias Optional parameter. Default value is 131072
   * @param singleSelect Optional parameter. Default value is false
   * @param useCommonUI Optional parameter. Default value is false
   * @param cancelError Optional parameter. Default value is false
   * @return  Returns a value of type IItems
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  IItems showSelectItems(
    IDevice device,
    @Optional @DefaultValue("0") WiaImageIntent intent,
    @Optional @DefaultValue("131072") WiaImageBias bias,
    @Optional @DefaultValue("-1") boolean singleSelect,
    @Optional @DefaultValue("-1") boolean useCommonUI,
    @Optional @DefaultValue("0") boolean cancelError);


  /**
   * <p>
   * Displays the properties dialog box for the specified Device
   * </p>
   * @param device Mandatory IDevice parameter.
   * @param cancelError Optional parameter. Default value is false
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  void showDeviceProperties(
    IDevice device,
    @Optional @DefaultValue("0") boolean cancelError);


  /**
   * <p>
   * Displays the properties dialog box for the specified Item
   * </p>
   * @param item Mandatory IItem parameter.
   * @param cancelError Optional parameter. Default value is false
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(12)
  void showItemProperties(
    IItem item,
    @Optional @DefaultValue("0") boolean cancelError);


  /**
   * <p>
   * Displays a progress dialog box while transferring the specified Item to the local machine. See Item.Transfer for additional information.
   * </p>
   * @param item Mandatory IItem parameter.
   * @param formatID Optional parameter. Default value is "{00000000-0000-0000-0000-000000000000}"
   * @param cancelError Optional parameter. Default value is false
   * @return  Returns a value of type java.lang.Object
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(13)
  @ReturnValue(type=NativeType.VARIANT)
  java.lang.Object showTransfer(
    IItem item,
    @Optional @DefaultValue("{00000000-0000-0000-0000-000000000000}") java.lang.String formatID,
    @Optional @DefaultValue("0") boolean cancelError);


  /**
   * <p>
   * Launches the Photo Printing Wizard with the absolute path of a specific file or Vector of absolute paths to files
   * </p>
   * @param files Mandatory java.lang.Object parameter.
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(14)
  void showPhotoPrintingWizard(
    java.lang.Object files);


  // Properties:
}
