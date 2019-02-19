package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

/**
 * <p>
 * The WiaPropertyType enumeration specifies the type of the value of an item property. Item properties can be found in the Properties collection of a Device or Item object.
 * </p>
 */
public enum WiaPropertyType implements ComEnum {
  /**
   * <p>
   * The value of the property is an unsupported type.
   * </p>
   * <p>
   * The value of this constant is 0
   * </p>
   */
  UnsupportedPropertyType(0),
  /**
   * <p>
   * The value of the property is a Boolean.
   * </p>
   * <p>
   * The value of this constant is 1
   * </p>
   */
  BooleanPropertyType(1),
  /**
   * <p>
   * The value of the property is a Byte.
   * </p>
   * <p>
   * The value of this constant is 2
   * </p>
   */
  BytePropertyType(2),
  /**
   * <p>
   * The value of the property is an Integer.
   * </p>
   * <p>
   * The value of this constant is 3
   * </p>
   */
  IntegerPropertyType(3),
  /**
   * <p>
   * The value of the property is returned as a non-negative Integer.
   * </p>
   * <p>
   * The value of this constant is 4
   * </p>
   */
  UnsignedIntegerPropertyType(4),
  /**
   * <p>
   * The value of the property is a Long.
   * </p>
   * <p>
   * The value of this constant is 5
   * </p>
   */
  LongPropertyType(5),
  /**
   * <p>
   * The value of the property is returned as a non-negative Long.
   * </p>
   * <p>
   * The value of this constant is 6
   * </p>
   */
  UnsignedLongPropertyType(6),
  /**
   * <p>
   * The value of the property is returned as a Long.
   * </p>
   * <p>
   * The value of this constant is 7
   * </p>
   */
  ErrorCodePropertyType(7),
  /**
   * <p>
   * The value of the property is a Large Integer returned as a truncated Long.
   * </p>
   * <p>
   * The value of this constant is 8
   * </p>
   */
  LargeIntegerPropertyType(8),
  /**
   * <p>
   * The value of the property is returned as a truncated non-negative Long.
   * </p>
   * <p>
   * The value of this constant is 9
   * </p>
   */
  UnsignedLargeIntegerPropertyType(9),
  /**
   * <p>
   * The value of the property is a Single.
   * </p>
   * <p>
   * The value of this constant is 10
   * </p>
   */
  SinglePropertyType(10),
  /**
   * <p>
   * The value of the property is a Double.
   * </p>
   * <p>
   * The value of this constant is 11
   * </p>
   */
  DoublePropertyType(11),
  /**
   * <p>
   * The value of the property is a Currency.
   * </p>
   * <p>
   * The value of this constant is 12
   * </p>
   */
  CurrencyPropertyType(12),
  /**
   * <p>
   * The value of the property is a Date.
   * </p>
   * <p>
   * The value of this constant is 13
   * </p>
   */
  DatePropertyType(13),
  /**
   * <p>
   * The value of the property is returned as a Date.
   * </p>
   * <p>
   * The value of this constant is 14
   * </p>
   */
  FileTimePropertyType(14),
  /**
   * <p>
   * The value of the property is returned as a String.
   * </p>
   * <p>
   * The value of this constant is 15
   * </p>
   */
  ClassIDPropertyType(15),
  /**
   * <p>
   * The value of the property is a String.
   * </p>
   * <p>
   * The value of this constant is 16
   * </p>
   */
  StringPropertyType(16),
  /**
   * <p>
   * The value of the property is an Object.
   * </p>
   * <p>
   * The value of this constant is 17
   * </p>
   */
  ObjectPropertyType(17),
  /**
   * <p>
   * The value of the property is returned as a Variant.
   * </p>
   * <p>
   * The value of this constant is 18
   * </p>
   */
  HandlePropertyType(18),
  /**
   * <p>
   * The value of the property is a Variant.
   * </p>
   * <p>
   * The value of this constant is 19
   * </p>
   */
  VariantPropertyType(19),
  /**
   * <p>
   * The value of the property is a Vector object containing Boolean elements.
   * </p>
   * <p>
   * The value of this constant is 101
   * </p>
   */
  VectorOfBooleansPropertyType(101),
  /**
   * <p>
   * The value of the property is a Vector object containing Byte elements.
   * </p>
   * <p>
   * The value of this constant is 102
   * </p>
   */
  VectorOfBytesPropertyType(102),
  /**
   * <p>
   * The value of the property is a Vector object containing Integer elements.
   * </p>
   * <p>
   * The value of this constant is 103
   * </p>
   */
  VectorOfIntegersPropertyType(103),
  /**
   * <p>
   * The value of the property is returned as a Vector object containing non-negative Integer elements.
   * </p>
   * <p>
   * The value of this constant is 104
   * </p>
   */
  VectorOfUnsignedIntegersPropertyType(104),
  /**
   * <p>
   * The value of the property is a Vector object containing Long elements.
   * </p>
   * <p>
   * The value of this constant is 105
   * </p>
   */
  VectorOfLongsPropertyType(105),
  /**
   * <p>
   * The value of the property is returned as a Vector object containing non-negative Long elements.
   * </p>
   * <p>
   * The value of this constant is 106
   * </p>
   */
  VectorOfUnsignedLongsPropertyType(106),
  /**
   * <p>
   * The value of the property is returned as a Vector object containing Long elements.
   * </p>
   * <p>
   * The value of this constant is 107
   * </p>
   */
  VectorOfErrorCodesPropertyType(107),
  /**
   * <p>
   * The value of the property is returned as a Vector object containing truncated Long elements.
   * </p>
   * <p>
   * The value of this constant is 108
   * </p>
   */
  VectorOfLargeIntegersPropertyType(108),
  /**
   * <p>
   * The value of the property is returned as a Vector object containing truncated non-negative Long elements.
   * </p>
   * <p>
   * The value of this constant is 109
   * </p>
   */
  VectorOfUnsignedLargeIntegersPropertyType(109),
  /**
   * <p>
   * The value of the property is a Vector object containing Single elements.
   * </p>
   * <p>
   * The value of this constant is 110
   * </p>
   */
  VectorOfSinglesPropertyType(110),
  /**
   * <p>
   * The value of the property is a Vector object containing Double elements.
   * </p>
   * <p>
   * The value of this constant is 111
   * </p>
   */
  VectorOfDoublesPropertyType(111),
  /**
   * <p>
   * The value of the property is a Vector object containing Currency elements.
   * </p>
   * <p>
   * The value of this constant is 112
   * </p>
   */
  VectorOfCurrenciesPropertyType(112),
  /**
   * <p>
   * The value of the property is a Vector object containing Date elements.
   * </p>
   * <p>
   * The value of this constant is 113
   * </p>
   */
  VectorOfDatesPropertyType(113),
  /**
   * <p>
   * The value of the property is returned as a Vector object containing Date elements.
   * </p>
   * <p>
   * The value of this constant is 114
   * </p>
   */
  VectorOfFileTimesPropertyType(114),
  /**
   * <p>
   * The value of the property is returned as a Vector object containing String elements.
   * </p>
   * <p>
   * The value of this constant is 115
   * </p>
   */
  VectorOfClassIDsPropertyType(115),
  /**
   * <p>
   * The value of the property is a Vector object containing String elements.
   * </p>
   * <p>
   * The value of this constant is 116
   * </p>
   */
  VectorOfStringsPropertyType(116),
  /**
   * <p>
   * The value of the property is a Vector object containing Variant elements.
   * </p>
   * <p>
   * The value of this constant is 119
   * </p>
   */
  VectorOfVariantsPropertyType(119),
  ;

  private final int value;
  WiaPropertyType(int value) { this.value=value; }
  public int comEnumValue() { return value; }
}
