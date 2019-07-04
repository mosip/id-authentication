package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

/**
 * <p>
 * The WiaImageIntent enumeration helps specify what type of data the image is intended to represent.
 * </p>
 */
public enum WiaImageIntent implements ComEnum {
  /**
   * <p>
   * No intent specified.
   * </p>
   * <p>
   * The value of this constant is 0
   * </p>
   */
  UnspecifiedIntent(0),
  /**
   * <p>
   * The image is a color illustration.
   * </p>
   * <p>
   * The value of this constant is 1
   * </p>
   */
  ColorIntent(1),
  /**
   * <p>
   * The image is grayscale data.
   * </p>
   * <p>
   * The value of this constant is 2
   * </p>
   */
  GrayscaleIntent(2),
  /**
   * <p>
   * The image is a text image such as a fax or scanned document.
   * </p>
   * <p>
   * The value of this constant is 4
   * </p>
   */
  TextIntent(4),
  ;

  private final int value;
  WiaImageIntent(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
