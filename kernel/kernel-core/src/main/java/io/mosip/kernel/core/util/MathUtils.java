/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.mosip.kernel.core.util;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.primes.Primes;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.Precision;

import io.mosip.kernel.core.exception.ArithmeticException;
import io.mosip.kernel.core.exception.IllegalArgumentException;
import io.mosip.kernel.core.exception.NullPointerException;
import io.mosip.kernel.core.util.constant.MathUtilConstants;
import io.mosip.kernel.core.util.exception.NotANumberException;
import io.mosip.kernel.core.util.exception.NotFiniteNumberException;
import io.mosip.kernel.core.util.exception.NotPositiveException;
import io.mosip.kernel.core.util.exception.NumberIsTooLargeException;

/**
 * Utilities for Mathematical operations.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
public final class MathUtils {

	/**
	 * Private Constructor for MathUtil Class
	 */
	private MathUtils() {

	}

	/**
	 * Raise an int to an int power.
	 * 
	 * @param num
	 *            Number to raise.
	 * @param exp
	 *            exponent (must be positive or zero)
	 * @return num^exp
	 * @throws NotPositiveException
	 *             if exp is less than 0.
	 * @throws ArithmeticException
	 *             if the result would overflow.
	 */
	public static int getPow(final int num, int exp) {
		try {
			return ArithmeticUtils.pow(num, exp);

		} catch (org.apache.commons.math3.exception.NotPositiveException e) {

			throw new NotPositiveException(MathUtilConstants.NOTPOSITIVE_ERROR_CODE.getErrorCode(),
					MathUtilConstants.NOTPOSITIVE_ERROR_CODE.getEexceptionMessage(), e.getCause());

		} catch (MathArithmeticException e) {

			throw new ArithmeticException(MathUtilConstants.ARITHMETIC_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ARITHMETIC_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Power function. Compute num^exp.
	 * 
	 * @param num
	 *            a double
	 * @param exp
	 *            a double
	 * @return double
	 */
	public static double getPow(double num, double exp) {

		return FastMath.pow(num, exp);

	}

	/**
	 * Raise a long to an int power.
	 * 
	 * @param num
	 *            Number to raise.
	 * @param exp
	 *            Exponent (must be positive or zero).
	 * @return num^exp
	 * @throws NotPositiveException
	 *             if exp is less than 0.
	 * @throws ArithmeticException
	 *             if the result would overflow.
	 */
	public static long getPow(long num, int exp) {
		try {
			return ArithmeticUtils.pow(num, exp);
		} catch (org.apache.commons.math3.exception.NotPositiveException e) {
			throw new NotPositiveException(MathUtilConstants.NOTPOSITIVE_ERROR_CODE.getErrorCode(),
					MathUtilConstants.NOTPOSITIVE_ERROR_CODE.getEexceptionMessage(), e.getCause());
		} catch (MathArithmeticException e) {
			throw new ArithmeticException(MathUtilConstants.ARITHMETIC_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ARITHMETIC_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * @param num
	 *            Number to raise.
	 * @param exp
	 *            Exponent (must be positive or zero).
	 * @return num^exp
	 * @throws NotPositiveException
	 *             if exp is less than 0.
	 */
	public static BigInteger getPow(BigInteger num, int exp) {
		try {
			return ArithmeticUtils.pow(num, exp);
		} catch (org.apache.commons.math3.exception.NotPositiveException e) {
			throw new NotPositiveException(MathUtilConstants.NOTPOSITIVE_ERROR_CODE.getErrorCode(),
					MathUtilConstants.NOTPOSITIVE_ERROR_CODE.getEexceptionMessage(), e.getCause());
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(MathUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					MathUtilConstants.NULL_POINTER_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}

	}

	/**
	 * Raise a BigInteger to a BigInteger power.
	 * 
	 * @param num
	 *            Number to raise.
	 * @param exp
	 *            Exponent (must be positive or zero).
	 * @return num^exp
	 * @throws NotPositiveException
	 *             if exp is less than 0.
	 */
	public static BigInteger getPow(BigInteger num, BigInteger exp) {
		try {
			return ArithmeticUtils.pow(num, exp);
		} catch (org.apache.commons.math3.exception.NotPositiveException e) {
			throw new NotPositiveException(MathUtilConstants.NOTPOSITIVE_ERROR_CODE.getErrorCode(),
					MathUtilConstants.NOTPOSITIVE_ERROR_CODE.getEexceptionMessage(), e.getCause());
		} catch (java.lang.NullPointerException e) {
			throw new NullPointerException(MathUtilConstants.NULL_POINTER_ERROR_CODE.getErrorCode(),
					MathUtilConstants.NULL_POINTER_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Generate random number
	 * 
	 * @param lowerlimit
	 *            starting value
	 * @param upperlimit
	 *            maximum value
	 * @return random number between lowerlimit and upperlimit.
	 * @throws NumberIsTooLargeException
	 *             -if lower is greater than or equal to upper
	 */
	public static int getRandom(final int lowerlimit, final int upperlimit) {
		try {
			return new RandomDataGenerator().nextInt(lowerlimit, upperlimit);
		} catch (org.apache.commons.math3.exception.NumberIsTooLargeException e) {
			throw new NumberIsTooLargeException(MathUtilConstants.NUMBER_IS_TOO_LARGE_ERROR_CODE.getErrorCode(),
					MathUtilConstants.NUMBER_IS_TOO_LARGE_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}

	}

	/**
	 * Generate random number
	 * 
	 * @param lowerlimit
	 *            starting value
	 * @param upperlimit
	 *            maximum value
	 * @return random number between lowerlimit and upperlimit.
	 * @throws NumberIsTooLargeException
	 *             -if lower is greater than or equal to upper
	 */
	public static long getRandom(final long lowerlimit, final long upperlimit) {
		try {
			return new RandomDataGenerator().nextLong(lowerlimit, upperlimit);
		} catch (org.apache.commons.math3.exception.NumberIsTooLargeException e) {
			throw new NumberIsTooLargeException(MathUtilConstants.NUMBER_IS_TOO_LARGE_ERROR_CODE.getErrorCode(),
					MathUtilConstants.NUMBER_IS_TOO_LARGE_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Generate random number
	 * 
	 * @param lowerlimit
	 *            starting value
	 * @param upperlimit
	 *            maximum value
	 * @return random number between lowerlimit and upperlimit
	 * @throws NumberIsTooLargeException
	 *             -if lower is greater than or equal to upper
	 * @throws NotFiniteNumberException
	 *             if one of the bounds is infinite
	 * @throws NotANumberException
	 *             if one of the bounds is NaN
	 */
	public static double getRandom(final double lowerlimit, final double upperlimit) {
		try {
			return new RandomDataGenerator().nextUniform(lowerlimit, upperlimit);
		} catch (org.apache.commons.math3.exception.NumberIsTooLargeException e) {
			throw new NumberIsTooLargeException(MathUtilConstants.NUMBER_IS_TOO_LARGE_ERROR_CODE.getErrorCode(),
					MathUtilConstants.NUMBER_IS_TOO_LARGE_ERROR_CODE.getEexceptionMessage(), e.getCause());
		} catch (org.apache.commons.math3.exception.NotFiniteNumberException e) {
			throw new NotFiniteNumberException(MathUtilConstants.NOT_FINITE_NUMBER_ERROR_CODE.getErrorCode(),
					MathUtilConstants.NOT_FINITE_NUMBER_ERROR_CODE.getEexceptionMessage(), e.getCause());
		} catch (org.apache.commons.math3.exception.NotANumberException e) {
			throw new NotANumberException(MathUtilConstants.NOT_A_NUMBER_ERROR_CODE.getErrorCode(),
					MathUtilConstants.NOT_A_NUMBER_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Generate random number
	 * 
	 * @param lowerlimit
	 *            starting value
	 * @param upperlimit
	 *            maximum value
	 * @return random number between lowerlimit and upperlimit
	 */
	public static float getRandom(final float lowerlimit, final float upperlimit) {

		float randomFloat = new RandomDataGenerator().getRandomGenerator().nextFloat();
		return lowerlimit + randomFloat * (upperlimit - lowerlimit);
	}

	/**
	 * Compute the square root of a number.
	 * 
	 * @param num
	 *            number on which evaluation is done
	 * @return square root of num.
	 */
	public static final double getSqrt(final double num) {
		return FastMath.sqrt(num);
	}

	/**
	 * Compute the maximum of two values
	 * 
	 * @param firstnumber
	 *            first value
	 * @param secondnumber
	 *            second value
	 * @return secondnumber if firstnumber is lesser or equal to secondnumber,
	 *         firstnumber otherwise
	 */
	public static final int getMax(final int firstnumber, final int secondnumber) {
		return FastMath.max(firstnumber, secondnumber);
	}

	/**
	 * Compute the maximum of two values
	 * 
	 * @param firstnumber
	 *            first value
	 * @param secondnumber
	 *            second value
	 * @return secondnumber if firstnumber is lesser or equal to secondnumber,
	 *         firstnumber otherwise
	 */
	public static final double getMax(double firstnumber, double secondnumber) {
		return FastMath.max(firstnumber, secondnumber);
	}

	/**
	 * Rounds the given value to the specified number of decimal places.
	 * 
	 * @param num
	 *            Value to round.
	 * @param place
	 *            Number of digits to the right of the decimal point.
	 * @return the rounded value.
	 */
	public static final double getRound(double num, int place) {
		return Precision.round(num, place);
	}

	/**
	 * Rounds the given value to the specified number of decimal places.
	 * 
	 * @param num
	 *            Value to round.
	 * @param place
	 *            Number of digits to the right of the decimal point.
	 * @return the rounded value.
	 */
	public static final float getRound(float num, int place) {
		return Precision.round(num, place);
	}

	/**
	 * Get the closest long to num.
	 * 
	 * @param num
	 *            number from which closest long is requested
	 * @return closest long to num
	 */
	public static final long getRound(double num) {
		return FastMath.round(num);
	}

	/**
	 * Absolute value.
	 * 
	 * @param num
	 *            number from which absolute value is requested.
	 * @return abs(num)
	 */
	public static final double getAbs(double num) {
		return FastMath.abs(num);
	}

	/**
	 * Absolute value.
	 * 
	 * @param num
	 *            number from which absolute value is requested.
	 * @return abs(num)
	 */
	public static final float getAbs(float num) {
		return FastMath.abs(num);
	}

	/**
	 * Absolute value.
	 * 
	 * @param num
	 *            number from which absolute value is requested.
	 * @return abs(num)
	 */
	public static final long getAbs(long num) {
		return FastMath.abs(num);
	}

	/**
	 * Absolute value.
	 * 
	 * @param num
	 *            number from which absolute value is requested.
	 * @return abs(num)
	 */
	public static final int getAbs(int num) {
		return FastMath.abs(num);
	}

	/**
	 * Evaluate Factorial
	 * 
	 * @param number
	 *            argument
	 * @return n!
	 * @throws NotPositiveException
	 *             -If number is not positive
	 * @throws ArithmeticException
	 *             -If number is greator than 20 and result is too large to fit in
	 *             long type.
	 */
	public static final long getFactorial(final int number) {
		try {
			return CombinatoricsUtils.factorial(number);
		} catch (org.apache.commons.math3.exception.NotPositiveException e) {
			throw new NotPositiveException(MathUtilConstants.NOTPOSITIVE_ERROR_CODE.getErrorCode(),
					MathUtilConstants.NOTPOSITIVE_ERROR_CODE.getEexceptionMessage(), e.getCause());
		} catch (MathArithmeticException e) {
			throw new ArithmeticException(MathUtilConstants.ARITHMETIC_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ARITHMETIC_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Computes the greatest common divisor of the absolute value of two numbers,
	 * using a modified version of the "binary gcd" method.
	 * 
	 * @param firstnumber
	 *            Number.
	 * @param secondnumber
	 *            Number.
	 * @return the greatest common divisor (never negative).
	 * @throws ArithmeticException
	 *             if the result cannot be represented as a non-negative integer
	 *             value.
	 */
	public static final int getGcd(int firstnumber, int secondnumber) {
		try {
			return ArithmeticUtils.gcd(firstnumber, secondnumber);
		} catch (MathArithmeticException e) {
			throw new ArithmeticException(MathUtilConstants.ARITHMETIC_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ARITHMETIC_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Gets the greatest common divisor of the absolute value of two numbers, using
	 * the "binary gcd" method which avoids division and modulo operations
	 * 
	 * @param firstnumber
	 *            Number.
	 * @param secondnumber
	 *            Number.
	 * @return the greatest common divisor, never negative.
	 * @throws ArithmeticException
	 *             if the result cannot be represented as a non-negative long type
	 *             value.
	 */
	public static final long getGcd(long firstnumber, long secondnumber) {
		try {
			return ArithmeticUtils.gcd(firstnumber, secondnumber);
		} catch (MathArithmeticException e) {
			throw new ArithmeticException(MathUtilConstants.ARITHMETIC_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ARITHMETIC_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Natural logarithm.
	 * 
	 * @param num
	 *            a double
	 * @return log(x)
	 */
	public static final double getLog(double num) {
		return FastMath.log(num);
	}

	/**
	 * Compute the base 10 logarithm.
	 * 
	 * @param num
	 *            a number
	 * @return log10(x)
	 */
	public static final double getLog10(double num) {
		return FastMath.log10(num);
	}

	/**
	 * Creates a copy of source array
	 * 
	 * @param source
	 *            Array to be copied.
	 * @return the copied array.
	 */
	public static int[] getCopyOfArray(int[] source) {
		return MathArrays.copyOf(source);
	}

	/**
	 * Creates a copy of source array.
	 * 
	 * @param source
	 *            Array to be copied.
	 * @param length
	 *            Number of entries to copy. If smaller then the source length, the
	 *            copy will be truncated, if larger it will padded with zeroes.
	 * @return the copied array.
	 */
	public static int[] getCopyOfArray(int[] source, int length) {
		return MathArrays.copyOf(source, length);
	}

	/**
	 * Creates a copy of source array
	 * 
	 * @param source
	 *            Array to be copied.
	 * @return the copied array.
	 */
	public static double[] getCopyOfArray(double[] source) {
		return MathArrays.copyOf(source);
	}

	/**
	 * Creates a copy of source array
	 * 
	 * @param source
	 *            Array to be copied.
	 * @param length
	 *            Number of entries to copy. If smaller then the source length, the
	 *            copy will be truncated, if larger it will padded with zeroes.
	 * @return the copied array.
	 */
	public static double[] getCopyOfArray(double[] source, int length) {
		return MathArrays.copyOf(source, length);
	}

	/**
	 * Returns the maximum of the entries in the input array, or
	 * <code>Double.NaN</code> if the array is empty.
	 * 
	 * @param arr
	 *            the input array
	 * @return the maximum of the values or Double.NaN if the array is empty
	 * @throws IllegalArgumentException
	 *             if the array is null
	 */
	public static double getArrayMaxValue(double[] arr) {
		try {
			return StatUtils.max(arr);
		} catch (MathIllegalArgumentException e) {
			throw new IllegalArgumentException(MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Returns the maximum of the entries in the specified portion of the input
	 * array, or <code>Double.NaN</code> if the designated subarray is empty
	 * 
	 * @param arr
	 *            the input array
	 * @param startindex
	 *            index of the first array element to include
	 * @param length
	 *            the number of elements to include
	 * @return the maximum of the values or Double.NaN if length = 0
	 * @throws IllegalArgumentException
	 *             if the array is null or the array index parameters are not valid
	 */
	public static double getArrayMaxValue(double[] arr, int startindex, int length) {
		try {
			return StatUtils.max(arr, startindex, length);
		} catch (MathIllegalArgumentException e) {
			throw new IllegalArgumentException(MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Returns the minimum of the entries in the input array, or
	 * <code>Double.NaN</code> if the array is empty.
	 * 
	 * @param arr
	 *            the input array
	 * @return the minimum of the values or Double.NaN if the array is empty
	 * @throws IllegalArgumentException
	 *             if the array is null
	 */
	public static double getArrayMinValue(double[] arr) {
		try {
			return StatUtils.min(arr);
		} catch (MathIllegalArgumentException e) {
			throw new IllegalArgumentException(MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Returns the minimum of the entries in the specified portion of the input
	 * array, or <code>Double.NaN</code> if the designated subarray is empty.
	 * 
	 * @param arr
	 *            the input array
	 * @param startindex
	 *            index of the first array element to include
	 * @param length
	 *            the number of elements to include
	 * @return the minimum of the values or Double.NaN if length = 0
	 * @throws IllegalArgumentException
	 *             if the array is null or the array index parameters are not valid
	 */
	public static double getArrayMinValue(double[] arr, int startindex, int length) {
		try {
			return StatUtils.min(arr, startindex, length);
		} catch (MathIllegalArgumentException e) {
			throw new IllegalArgumentException(MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}

	}

	/**
	 * Returns the sum of the values in the input array, or <code>Double.NaN</code>
	 * if the array is empty.
	 * 
	 * @param arr
	 *            array of values to sum
	 * @return the sum of the values or <code>Double.NaN</code> if the array is
	 *         empty
	 * @throws IllegalArgumentException
	 *             if the array is null
	 */
	public static double getArrayValuesSum(double[] arr) {
		try {
			return StatUtils.sum(arr);
		} catch (MathIllegalArgumentException e) {
			throw new IllegalArgumentException(MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}

	}

	/**
	 * Returns the sum of the entries in the specified portion of the input array,
	 * or <code>Double.NaN</code> if the designated subarray is empty.
	 * 
	 * @param arr
	 *            the input array
	 * @param startindex
	 *            index of the first array element to include
	 * @param length
	 *            the number of elements to include
	 * @return the sum of the values or Double.NaN if length = 0
	 * @throws IllegalArgumentException
	 *             if the array is null or the array index parameters are not valid
	 */
	public static double getArrayValuesSum(double[] arr, int startindex, int length) {
		try {
			return StatUtils.sum(arr, startindex, length);
		} catch (MathIllegalArgumentException e) {
			throw new IllegalArgumentException(MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}

	}

	/**
	 * Returns the product of the entries in the input array, or
	 * <code>Double.NaN</code> if the array is empty.
	 * 
	 * @param arr
	 *            the input array
	 * @return the product of the values or Double.NaN if the array is empty
	 * @throws IllegalArgumentException
	 *             if the array is null
	 */
	public static double getArrayValuesProduct(double[] arr) {
		try {
			return StatUtils.product(arr);
		} catch (MathIllegalArgumentException e) {
			throw new IllegalArgumentException(MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Returns the product of the entries in the specified portion of the input
	 * array, or <code>Double.NaN</code> if the designated subarray is empty.
	 * 
	 * @param arr
	 *            the input array
	 * @param startindex
	 *            index of the first array element to include
	 * @param length
	 *            the number of elements to include
	 * @return the product of the values or Double.NaN if length = 0
	 * @throws IllegalArgumentException
	 *             if the array is null or the array index parameters are not valid
	 */
	public static double getArrayValuesProduct(double[] arr, int startindex, int length) {
		try {
			return StatUtils.product(arr, startindex, length);
		} catch (MathIllegalArgumentException e) {
			throw new IllegalArgumentException(MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Returns the arithmetic mean of the entries in the input array, or
	 * <code>Double.NaN</code> if the array is empty.
	 * 
	 * @param arr
	 *            the input array
	 * @return the mean of the values or Double.NaN if the array is empty
	 * @throws IllegalArgumentException
	 *             if the array is null
	 */
	public static double getArrayValuesMean(double[] arr) {
		try {
			return StatUtils.mean(arr);
		} catch (MathIllegalArgumentException e) {
			throw new IllegalArgumentException(MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * @param arr
	 *            the input array
	 * @param startindex
	 *            index of the first array element to include
	 * @param length
	 *            the number of elements to include
	 * @return the mean of the values or Double.NaN if length = 0
	 * @throws IllegalArgumentException
	 *             if the array is null or the array index parameters are not valid
	 */
	public static double getArrayValuesMean(double[] arr, int startindex, int length) {
		try {
			return StatUtils.mean(arr, startindex, length);
		} catch (MathIllegalArgumentException e) {
			throw new IllegalArgumentException(MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Primality test: tells if the argument is a (provable) prime or not.
	 * 
	 * @param num
	 *            number to test.
	 * @return true if num is prime.
	 */
	public static boolean isPrimeOrNot(int num) {
		return Primes.isPrime(num);
	}

	/**
	 * Return the smallest prime greater than or equal to n.
	 * 
	 * @param num
	 *            a positive number.
	 * @return the smallest prime greater than or equal to n.
	 * @throws IllegalArgumentException
	 *             if num is less than 0.
	 */
	public static int nextPrimeNumber(int num) {
		try {
			return Primes.nextPrime(num);
		} catch (MathIllegalArgumentException e) {
			throw new IllegalArgumentException(MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Prime factors decomposition
	 * 
	 * @param num
	 *            number to factorize: must be &ge; 2
	 * @return list of prime factors of num
	 * @throws IllegalArgumentException
	 *             if num is less than 2.
	 */
	public static List<Integer> getPrimeFactors(int num) {
		try {
			return Primes.primeFactors(num);
		} catch (MathIllegalArgumentException e) {
			throw new IllegalArgumentException(MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getErrorCode(),
					MathUtilConstants.ILLEGALARGUMENT_ERROR_CODE.getEexceptionMessage(), e.getCause());
		}
	}

	/**
	 * Get the smallest whole number larger than number.
	 * 
	 * @param num
	 *            number from which ceil is requested
	 * @return a double number c such that c is an integer is greator than equal to
	 *         num and num is greator than c-1.0
	 */
	public static double ceil(double num) {
		return FastMath.ceil(num);
	}

	/**
	 * Get the largest whole number smaller than number.
	 * 
	 * @param num
	 *            number from which floor is requested
	 * @return a double number f such that f is an integer f is less than or equal
	 *         to num and num is less than f + 1.0
	 */
	public static double floor(double num) {
		return FastMath.floor(num);
	}

}
