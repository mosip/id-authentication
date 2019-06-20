package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

/**
 * <p>
 * The WiaSubType enumeration specifies more detail about the property value. Use the SubType property on the Property object to obtain these values for the property.
 * </p>
 */
public enum WiaSubType {
  /**
   * <p>
   * This property has no subtype.
   * </p>
   * <p>
   * The value of this constant is 0
   * </p>
   */
  UnspecifiedSubType, // 0
  /**
   * <p>
   * This property takes a range of values from SubTypeMin to SubTypeMax in SubTypeStep increments.
   * </p>
   * <p>
   * The value of this constant is 1
   * </p>
   */
  RangeSubType, // 1
  /**
   * <p>
   * This property takes one of a list of values from SubTypeValues.
   * </p>
   * <p>
   * The value of this constant is 2
   * </p>
   */
  ListSubType, // 2
  /**
   * <p>
   * This property takes a flag composed of bits listed in SubTypeValues. Flag values are combined using the OR operation.
   * </p>
   * <p>
   * The value of this constant is 3
   * </p>
   */
  FlagSubType, // 3
}
