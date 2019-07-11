package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

/**
 * <p>
 * The WiaImagePropertyType enumeration specifies the type of the value of an image property. Image properties can be found in the Properties collection of an ImageFile object.
 * </p>
 */
public enum WiaImagePropertyType implements ComEnum {
  /**
   * <p>
   * The value of the image property is undefined returned as a Byte.
   * </p>
   * <p>
   * The value of this constant is 1000
   * </p>
   */
  UndefinedImagePropertyType(1000),
  /**
   * <p>
   * The value of the image property is a Byte.
   * </p>
   * <p>
   * The value of this constant is 1001
   * </p>
   */
  ByteImagePropertyType(1001),
  /**
   * <p>
   * The value of the image property is a String.
   * </p>
   * <p>
   * The value of this constant is 1002
   * </p>
   */
  StringImagePropertyType(1002),
  /**
   * <p>
   * The value of the image property is returned as a non-negative Integer.
   * </p>
   * <p>
   * The value of this constant is 1003
   * </p>
   */
  UnsignedIntegerImagePropertyType(1003),
  /**
   * <p>
   * The value of the image property is a Long.
   * </p>
   * <p>
   * The value of this constant is 1004
   * </p>
   */
  LongImagePropertyType(1004),
  /**
   * <p>
   * The value of the image property is returned as a non-negative Long.
   * </p>
   * <p>
   * The value of this constant is 1005
   * </p>
   */
  UnsignedLongImagePropertyType(1005),
  /**
   * <p>
   * The value of the image property is returned as a Rational Object.
   * </p>
   * <p>
   * The value of this constant is 1006
   * </p>
   */
  RationalImagePropertyType(1006),
  /**
   * <p>
   * The value of the image property is returned as a Rational Object.
   * </p>
   * <p>
   * The value of this constant is 1007
   * </p>
   */
  UnsignedRationalImagePropertyType(1007),
  /**
   * <p>
   * The value of the image property is returned as a Vector object containing Byte elements.
   * </p>
   * <p>
   * The value of this constant is 1100
   * </p>
   */
  VectorOfUndefinedImagePropertyType(1100),
  /**
   * <p>
   * The value of the image property is a Vector object containing Byte elements.
   * </p>
   * <p>
   * The value of this constant is 1101
   * </p>
   */
  VectorOfBytesImagePropertyType(1101),
  /**
   * <p>
   * The value of the image property is returned as a Vector object containing Integer elements.
   * </p>
   * <p>
   * The value of this constant is 1102
   * </p>
   */
  VectorOfUnsignedIntegersImagePropertyType(1102),
  /**
   * <p>
   * The value of the image property is a Vector object containing Long elements.
   * </p>
   * <p>
   * The value of this constant is 1103
   * </p>
   */
  VectorOfLongsImagePropertyType(1103),
  /**
   * <p>
   * The value of the image property is returned as a Vector object containing Long elements.
   * </p>
   * <p>
   * The value of this constant is 1104
   * </p>
   */
  VectorOfUnsignedLongsImagePropertyType(1104),
  /**
   * <p>
   * The value of the image property is returned as a Vector object containing Rational object elements.
   * </p>
   * <p>
   * The value of this constant is 1105
   * </p>
   */
  VectorOfRationalsImagePropertyType(1105),
  /**
   * <p>
   * The value of the image property is returned as a Vector object containing Rational object elements.
   * </p>
   * <p>
   * The value of this constant is 1106
   * </p>
   */
  VectorOfUnsignedRationalsImagePropertyType(1106),
  ;

  private final int value;
  WiaImagePropertyType(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
