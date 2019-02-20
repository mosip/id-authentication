package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

@IID("{F4243B65-3F63-4D99-93CD-86B6D62C5EB2}")
public interface IImageFile extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Returns the FormatID for this file type
   * </p>
   * <p>
   * Getter method for the COM property "FormatID"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(7)
  java.lang.String formatID();


  /**
   * <p>
   * Returns the file extension for this image file type
   * </p>
   * <p>
   * Getter method for the COM property "FileExtension"
   * </p>
   * @return  Returns a value of type java.lang.String
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(8)
  java.lang.String fileExtension();


  /**
   * <p>
   * Returns the raw image file as a Vector of Bytes
   * </p>
   * <p>
   * Getter method for the COM property "FileData"
   * </p>
   * @return  Returns a value of type IVector
   */

  @DISPID(3) //= 0x3. The runtime will prefer the VTID if present
  @VTID(9)
  IVector fileData();


  @VTID(9)
  @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={IVector.class})
  java.lang.Object fileData(
    int index);

  /**
   * <p>
   * Returns the raw image bits as a Vector of Long values
   * </p>
   * <p>
   * Getter method for the COM property "ARGBData"
   * </p>
   * @return  Returns a value of type IVector
   */

  @DISPID(4) //= 0x4. The runtime will prefer the VTID if present
  @VTID(10)
  IVector argbData();


  @VTID(10)
  @ReturnValue(type=NativeType.VARIANT,defaultPropertyThrough={IVector.class})
  java.lang.Object argbData(
    int index);

  /**
   * <p>
   * Returns the Height of the image in pixels
   * </p>
   * <p>
   * Getter method for the COM property "Height"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(5) //= 0x5. The runtime will prefer the VTID if present
  @VTID(11)
  int height();


  /**
   * <p>
   * Returns the Width of the image in pixels
   * </p>
   * <p>
   * Getter method for the COM property "Width"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(6) //= 0x6. The runtime will prefer the VTID if present
  @VTID(12)
  int width();


  /**
   * <p>
   * Returns the Horizontal pixels per inch of the image
   * </p>
   * <p>
   * Getter method for the COM property "HorizontalResolution"
   * </p>
   * @return  Returns a value of type double
   */

  @DISPID(7) //= 0x7. The runtime will prefer the VTID if present
  @VTID(13)
  double horizontalResolution();


  /**
   * <p>
   * Returns the Vertical pixels per inch of the image
   * </p>
   * <p>
   * Getter method for the COM property "VerticalResolution"
   * </p>
   * @return  Returns a value of type double
   */

  @DISPID(8) //= 0x8. The runtime will prefer the VTID if present
  @VTID(14)
  double verticalResolution();


  /**
   * <p>
   * Returns the depth of the pixels of the image in bits per pixel
   * </p>
   * <p>
   * Getter method for the COM property "PixelDepth"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(9) //= 0x9. The runtime will prefer the VTID if present
  @VTID(15)
  int pixelDepth();


  /**
   * <p>
   * Indicates if the pixel data is an index into a palette or the actual color data
   * </p>
   * <p>
   * Getter method for the COM property "IsIndexedPixelFormat"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(10) //= 0xa. The runtime will prefer the VTID if present
  @VTID(16)
  boolean isIndexedPixelFormat();


  /**
   * <p>
   * Indicates if the pixel format has an alpha component
   * </p>
   * <p>
   * Getter method for the COM property "IsAlphaPixelFormat"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(11) //= 0xb. The runtime will prefer the VTID if present
  @VTID(17)
  boolean isAlphaPixelFormat();


  /**
   * <p>
   * Indicates if the pixel format is extended (16 bits/channel)
   * </p>
   * <p>
   * Getter method for the COM property "IsExtendedPixelFormat"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(12) //= 0xc. The runtime will prefer the VTID if present
  @VTID(18)
  boolean isExtendedPixelFormat();


  /**
   * <p>
   * Indicates whether the image is animated
   * </p>
   * <p>
   * Getter method for the COM property "IsAnimated"
   * </p>
   * @return  Returns a value of type boolean
   */

  @DISPID(13) //= 0xd. The runtime will prefer the VTID if present
  @VTID(19)
  boolean isAnimated();


  /**
   * <p>
   * Returns the number of frames in the image
   * </p>
   * <p>
   * Getter method for the COM property "FrameCount"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(14) //= 0xe. The runtime will prefer the VTID if present
  @VTID(20)
  int frameCount();


  /**
   * <p>
   * Returns/Sets the current frame in the image
   * </p>
   * <p>
   * Getter method for the COM property "ActiveFrame"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(15) //= 0xf. The runtime will prefer the VTID if present
  @VTID(21)
  int activeFrame();


  /**
   * <p>
   * Returns/Sets the current frame in the image
   * </p>
   * <p>
   * Setter method for the COM property "ActiveFrame"
   * </p>
   * @param plResult Mandatory int parameter.
   */

  @DISPID(15) //= 0xf. The runtime will prefer the VTID if present
  @VTID(22)
  void activeFrame(
    int plResult);


  /**
   * <p>
   * A collection of all properties for this image
   * </p>
   * <p>
   * Getter method for the COM property "Properties"
   * </p>
   * @return  Returns a value of type IProperties
   */

  @DISPID(16) //= 0x10. The runtime will prefer the VTID if present
  @VTID(23)
  IProperties properties();


  @VTID(23)
  @ReturnValue(defaultPropertyThrough={IProperties.class})
  IProperty properties(
    java.lang.Object index);

  /**
   * <p>
   * Loads the ImageFile object with the specified File
   * </p>
   * @param filename Mandatory java.lang.String parameter.
   */

  @DISPID(17) //= 0x11. The runtime will prefer the VTID if present
  @VTID(24)
  void loadFile(
    java.lang.String filename);


  /**
   * <p>
   * Save the ImageFile object to the specified File
   * </p>
   * @param filename Mandatory java.lang.String parameter.
   */

  @DISPID(18) //= 0x12. The runtime will prefer the VTID if present
  @VTID(25)
  void saveFile(
    java.lang.String filename);


  // Properties:
}
