package io.mosip.registration.device.scanner.wia  ;

/**
 * <p>
 * The WiaDeviceType enumeration specifies the type of device attached to a user's computer. Use the Type property on the DeviceInfo object or the Device object to obtain these values from the device.
 * </p>
 */
public enum WiaDeviceType {
  /**
   * <p>
   * The Device type is unknown.
   * </p>
   * <p>
   * The value of this constant is 0
   * </p>
   */
  UnspecifiedDeviceType, // 0
  /**
   * <p>
   * The Device is a scanner.
   * </p>
   * <p>
   * The value of this constant is 1
   * </p>
   */
  ScannerDeviceType, // 1
  /**
   * <p>
   * The Device is a camera.
   * </p>
   * <p>
   * The value of this constant is 2
   * </p>
   */
  CameraDeviceType, // 2
  /**
   * <p>
   * The Device provides streaming video.
   * </p>
   * <p>
   * The value of this constant is 3
   * </p>
   */
  VideoDeviceType, // 3
}
