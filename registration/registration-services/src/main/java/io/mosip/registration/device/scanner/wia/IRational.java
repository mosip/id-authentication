package io.mosip.registration.device.scanner.wia  ;

import com4j.*;

@IID("{3BF1B24A-01A5-4AA3-91F9-25A60B50E49B}")
public interface IRational extends Com4jObject {
  // Methods:
  /**
   * <p>
   * Returns the Rational Value as a Double
   * </p>
   * <p>
   * Getter method for the COM property "Value"
   * </p>
   * @return  Returns a value of type double
   */

  @DISPID(0) //= 0x0. The runtime will prefer the VTID if present
  @VTID(7)
  @DefaultMethod
  double value();


  /**
   * <p>
   * Returns/Sets the Rational Value Numerator
   * </p>
   * <p>
   * Getter method for the COM property "Numerator"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(8)
  int numerator();


  /**
   * <p>
   * Returns/Sets the Rational Value Numerator
   * </p>
   * <p>
   * Setter method for the COM property "Numerator"
   * </p>
   * @param plResult Mandatory int parameter.
   */

  @DISPID(1) //= 0x1. The runtime will prefer the VTID if present
  @VTID(9)
  void numerator(
    int plResult);


  /**
   * <p>
   * Returns/Sets the Rational Value Denominator
   * </p>
   * <p>
   * Getter method for the COM property "Denominator"
   * </p>
   * @return  Returns a value of type int
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(10)
  int denominator();


  /**
   * <p>
   * Returns/Sets the Rational Value Denominator
   * </p>
   * <p>
   * Setter method for the COM property "Denominator"
   * </p>
   * @param plResult Mandatory int parameter.
   */

  @DISPID(2) //= 0x2. The runtime will prefer the VTID if present
  @VTID(11)
  void denominator(
    int plResult);


  // Properties:
}
