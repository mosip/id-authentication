package io.mosip.kernel.core.test.util;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.mosip.kernel.core.exception.ArithmeticException;
import io.mosip.kernel.core.exception.IllegalArgumentException;
import io.mosip.kernel.core.exception.NullPointerException;
import io.mosip.kernel.core.util.MathUtils;
import io.mosip.kernel.core.util.exception.NotANumberException;
import io.mosip.kernel.core.util.exception.NotFiniteNumberException;
import io.mosip.kernel.core.util.exception.NotPositiveException;
import io.mosip.kernel.core.util.exception.NumberIsTooLargeException;

public class MathUtilTest {
	int a[] = new int[3];
	double b[] = new double[3];
	List<Integer> list = new ArrayList<Integer>();

	@Before
	public void setUp() {

		a[0] = 1;
		a[1] = 2;
		a[2] = 3;
		b[0] = 1.0;
		b[1] = 2.0;
		b[2] = 3.0;
		list = Arrays.asList(2, 3, 3);
	}

	@Test
	public void getCopyOfArrayTest1() {
		assertThat(MathUtils.getCopyOfArray(a), is(a));
	}

	@Test
	public void getCopyOfArrayTest2() {

		assertThat(MathUtils.getCopyOfArray(a, 4).length, is(4));
		assertThat(MathUtils.getCopyOfArray(a, 4)[3], is(0));
		assertThat(MathUtils.getCopyOfArray(a, 4)[2], is(a[2]));
	}

	@Test
	public void getCopyOfArrayTest3() {
		assertThat(MathUtils.getCopyOfArray(b), is(b));
	}

	@Test
	public void getCopyOfArrayTest4() {

		assertThat(MathUtils.getCopyOfArray(b, 4).length, is(4));
		assertThat(MathUtils.getCopyOfArray(b, 4)[3], is(0.0));
		assertThat(MathUtils.getCopyOfArray(b, 4)[2], is(b[2]));
	}

	@Test
	public void getPowTest1() {

		assertThat(MathUtils.getPow(2.0, 3.0), is(8.0));
	}

	@Test
	public void getPowTest2() {
		assertThat(MathUtils.getPow(2, 3), is(8));
	}

	@Test(expected = NotPositiveException.class)
	public void getPowExceptionTest1() {
		MathUtils.getPow(2, -2);

	}

	@Test(expected = ArithmeticException.class)
	public void getPowExceptionTest2() {
		MathUtils.getPow(2, 9999999);
	}

	@Test
	public void getPowTest3() {
		assertThat(MathUtils.getPow(9L, 2), is(81L));
	}

	@Test(expected = NotPositiveException.class)
	public void getPowExceptionTest3() {
		MathUtils.getPow(9L, -2);
	}

	@Test(expected = ArithmeticException.class)
	public void getPowExceptionTest4() {
		MathUtils.getPow(99999999L, 9);
	}

	@Test
	public void getPowTest4() {
		BigInteger bi = new BigInteger("2");
		assertThat(MathUtils.getPow(bi, 2), is(bi.pow(2)));
	}

	@Test(expected = NotPositiveException.class)
	public void getPowExceptionTest5() {
		BigInteger bi = new BigInteger("2");
		MathUtils.getPow(bi, -2);
	}

	@Test
	public void getPowTest5() {
		BigInteger bi = new BigInteger("2");
		BigInteger bint = new BigInteger("3");
		BigInteger bi2 = new BigInteger("8");
		assertThat(MathUtils.getPow(bi, bint), is(bi2));
	}

	@Test(expected = NotPositiveException.class)
	public void getPowExceptionTest6() {
		BigInteger bi = new BigInteger("2");
		BigInteger bint = new BigInteger("3");
		bint = BigInteger.ZERO.subtract(bint);
		MathUtils.getPow(bi, bint);
	}

	@Test(expected = NullPointerException.class)
	public void getPowExceptionTest7() {
		MathUtils.getPow(null, null);

	}

	@Test(expected = NullPointerException.class)
	public void getPowExceptionTest8() {
		MathUtils.getPow(null, 2);

	}

	@Test
	public void getSqrttest() {
		assertThat(MathUtils.getSqrt(4.0), is(2.0));
	}

	@Test
	public void getRoundtest() {
		assertThat(MathUtils.getRound(8.897, 1), is(8.9));
	}

	@Test
	public void getAbstest1() {
		assertThat(MathUtils.getAbs(-999), is(999));
	}

	@Test
	public void getAbstest2() {
		assertThat(MathUtils.getAbs(-999.0), is(999.0));
	}

	@Test
	public void getAbstest3() {
		assertThat(MathUtils.getAbs(-999L), is(999L));
	}

	@Test
	public void getAbstest4() {
		assertThat(MathUtils.getAbs(-999F), is(999F));
	}

	@Test
	public void getRoundtest2() {
		assertThat(MathUtils.getRound(4.5), is(5L));
	}

	@Test
	public void getRoundtest3() {
		assertThat(MathUtils.getRound(4.888888F, 0), is(5.0F));
	}

	@Test
	public void getLog10test() {
		assertThat(MathUtils.getLog10(10.0), is(1.0));
	}

	@Test
	public void getLogtest() {
		assertThat(MathUtils.getLog(2.7), is(0.9932517730102834));
	}

	@Test
	public void getFactorialtest() {
		assertThat(MathUtils.getFactorial(3), is(6L));
	}

	@Test(expected = NotPositiveException.class)
	public void getFactorialExceptionTest1() {
		MathUtils.getFactorial(-1);
	}

	@Test(expected = ArithmeticException.class)
	public void getFactorialExceptionTest2() {
		MathUtils.getFactorial(999999);
	}

	@Test
	public void getMaxtest() {
		assertThat(MathUtils.getMax(2.0, 3.0), is(3.0));
	}

	@Test
	public void getMaxtest2() {
		assertThat(MathUtils.getMax(2, 3), is(3));
	}

	@Test
	public void getGcdtest() {
		assertThat(MathUtils.getGcd(2, 3), is(1));
	}

	@Test(expected = ArithmeticException.class)
	public void getGcdExceptionTest1() {
		MathUtils.getGcd(Integer.MIN_VALUE, Integer.MIN_VALUE);
	}

	@Test
	public void getGcdtest2() {
		assertThat(MathUtils.getGcd(2L, 3L), is(1L));
	}

	@Test(expected = ArithmeticException.class)
	public void getGcdExceptionTest2() {
		MathUtils.getGcd(Long.MIN_VALUE, Long.MIN_VALUE);
	}

	@Test
	public void getArrayMaxValueTest1() {
		assertThat(MathUtils.getArrayMaxValue(b), is(3.0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getArrayMaxValueExceptionTest1() {
		MathUtils.getArrayMaxValue(null);
	}

	@Test
	public void getArrayMaxValueTest2() {
		assertThat(MathUtils.getArrayMaxValue(b, 1, 2), is(3.0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getArrayMaxValueExceptionTest2() {
		MathUtils.getArrayMaxValue(b, -1, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getArrayMaxValueExceptionTest3() {
		MathUtils.getArrayMaxValue(null, 0, 2);
	}

	@Test
	public void getArrayMinValueTest1() {
		assertThat(MathUtils.getArrayMinValue(b), is(1.0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getArrayMinValueExceptionTest1() {
		MathUtils.getArrayMinValue(null);
	}

	@Test
	public void getArrayMinValueTest2() {
		assertThat(MathUtils.getArrayMinValue(b, 1, 2), is(2.0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getArrayMinValueExceptionTest2() {
		MathUtils.getArrayMinValue(b, -1, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getArrayMinValueExceptionTest3() {
		MathUtils.getArrayMinValue(null, 0, 2);
	}

	@Test
	public void getArrayProductTest1() {
		assertThat(MathUtils.getArrayValuesProduct(b), is(6.0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getArrayProductExceptionTest1() {
		MathUtils.getArrayValuesProduct(null);
	}

	@Test
	public void getArrayProductTest2() {
		assertThat(MathUtils.getArrayValuesProduct(b, 0, 2), is(2.0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getArrayProductExceptionTest2() {
		MathUtils.getArrayValuesProduct(b, -1, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getArrayProductExceptionTest3() {
		MathUtils.getArrayValuesProduct(null, 1, 2);
	}

	@Test
	public void getArraySumTest1() {
		assertThat(MathUtils.getArrayValuesSum(b), is(6.0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getArraySumExceptionTest1() {
		MathUtils.getArrayValuesSum(null);
	}

	@Test
	public void getarraySumTest2() {
		assertThat(MathUtils.getArrayValuesSum(b, 1, 2), is(5.0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getarraySumExceptionTest2() {
		MathUtils.getArrayValuesSum(b, -1, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getarraySumExceptionTest3() {
		MathUtils.getArrayValuesSum(null, 1, 2);
	}

	@Test
	public void getRandomtest1() {
		assertThat(MathUtils.getRandom(3.8F, 9.8F), is(instanceOf(Float.class)));
		Float randomFloat = MathUtils.getRandom(3.8F, 9.8F);
		assertEquals(true, 3.8F <= randomFloat && randomFloat <= 9.8F);

	}

	@Test
	public void getRandomtest2() {
		assertThat(MathUtils.getRandom(3, 9), is(instanceOf(Integer.class)));
		int randomFloat = MathUtils.getRandom(3, 9);
		assertEquals(true, 3 <= randomFloat && randomFloat <= 9);
	}

	@Test(expected = NumberIsTooLargeException.class)
	public void getRandomExceptionTest2() {
		MathUtils.getRandom(4, 1);
	}

	@Test
	public void getRandomtest3() {
		assertThat(MathUtils.getRandom(8L, 9L), is(instanceOf(Long.class)));
		long randomFloat = MathUtils.getRandom(8L, 9L);
		assertEquals(true, 8L <= randomFloat && randomFloat <= 9L);
	}

	@Test(expected = NumberIsTooLargeException.class)
	public void getRandomExceptionTest3() {
		MathUtils.getRandom(4L, 1L);
	}

	@Test
	public void getRandomtest4() {
		assertThat(MathUtils.getRandom(8.0, 9.0), is(instanceOf(Double.class)));
		double randomFloat = MathUtils.getRandom(8.0, 9.0);
		assertTrue(8.0 <= randomFloat && randomFloat <= 9.0);
	}

	@Test(expected = NumberIsTooLargeException.class)
	public void getRandomExceptionTest4() {
		MathUtils.getRandom(6.0, 1.0);
	}

	@Test(expected = NotFiniteNumberException.class)
	public void getRandomExceptionTest5() {
		MathUtils.getRandom(6, Double.POSITIVE_INFINITY);
	}

	@Test(expected = NotANumberException.class)
	public void getRandomExceptionTest6() {
		MathUtils.getRandom(6, Double.NaN);
	}

	@Test
	public void isPrimeTest() {
		assertThat(MathUtils.isPrimeOrNot(2), is(true));
	}

	@Test
	public void nextPrimeTest() {
		assertThat(MathUtils.nextPrimeNumber(1), is(2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void nextPrimeExceptionTest() {
		MathUtils.nextPrimeNumber(-1);
	}

	@Test
	public void getArrayValueMeanTest() {
		assertThat(MathUtils.getArrayValuesMean(b), is(2.0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getArrayValueMeanExceptionTest1() {
		MathUtils.getArrayValuesMean(null);
	}

	@Test
	public void getArrayValueMeanTest2() {
		assertThat(MathUtils.getArrayValuesMean(b, 0, 3), is(2.0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getArrayValueMeanExceptionTest2() {
		MathUtils.getArrayValuesMean(b, -1, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getArrayValueMeanExceptionTest3() {
		MathUtils.getArrayValuesMean(null, 0, 3);
	}

	@Test
	public void getPrimeFactorTest() {
		assertThat(MathUtils.getPrimeFactors(18), is(list));
	}

	@Test(expected = IllegalArgumentException.class)
	public void getPrimeFactorExceptionTest() {
		MathUtils.getPrimeFactors(1);
	}

	@Test
	public void ceilTest() {
		assertThat(MathUtils.ceil(9.2), is(10.0));
	}

	@Test
	public void floorTest() {
		assertThat(MathUtils.floor(9.2), is(9.0));
	}
}
