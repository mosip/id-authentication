package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

/**
 * <p>
 * The WiaImageBias enumeration helps specify what type of data the image is intended to represent.
 * </p>
 */
public enum WiaImageBias implements ComEnum {
  /**
   * <p>
   * Use a lower quality scan to minimize the size of the file that contains the image.
   * </p>
   * <p>
   * The value of this constant is 65536
   * </p>
   */
  MinimizeSize(65536),
  /**
   * <p>
   * Use a higher quality scan to maximize the quality of the image.
   * </p>
   * <p>
   * The value of this constant is 131072
   * </p>
   */
  MaximizeQuality(131072),
  ;

  private final int value;
  WiaImageBias(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
