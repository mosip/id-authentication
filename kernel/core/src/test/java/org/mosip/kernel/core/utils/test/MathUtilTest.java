package org.mosip.kernel.core.utils.test;

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
import org.mosip.kernel.core.utils.MathUtil;
import org.mosip.kernel.core.utils.exception.MosipArithmeticException;
import org.mosip.kernel.core.utils.exception.MosipIllegalArgumentException;
import org.mosip.kernel.core.utils.exception.MosipNotANumberException;
import org.mosip.kernel.core.utils.exception.MosipNotFiniteNumberException;
import org.mosip.kernel.core.utils.exception.MosipNotPositiveException;
import org.mosip.kernel.core.utils.exception.MosipNullPointerException;
import org.mosip.kernel.core.utils.exception.MosipNumberIsTooLargeException;

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
		assertThat(MathUtil.getCopyOfArray(a), is(a));
	}

	@Test
	public void getCopyOfArrayTest2() {

		assertThat(MathUtil.getCopyOfArray(a, 4).length, is(4));
		assertThat(MathUtil.getCopyOfArray(a, 4)[3], is(0));
		assertThat(MathUtil.getCopyOfArray(a, 4)[2], is(a[2]));
	}

	@Test
	public void getCopyOfArrayTest3() {
		assertThat(MathUtil.getCopyOfArray(b), is(b));
	}

	@Test
	public void getCopyOfArrayTest4() {

		assertThat(MathUtil.getCopyOfArray(b, 4).length, is(4));
		assertThat(MathUtil.getCopyOfArray(b, 4)[3], is(0.0));
		assertThat(MathUtil.getCopyOfArray(b, 4)[2], is(b[2]));
	}

	@Test
	public void getPowTest1() {

		assertThat(MathUtil.getPow(2.0, 3.0), is(8.0));
	}

	@Test
	public void getPowTest2() {
		assertThat(MathUtil.getPow(2, 3), is(8));
	}

	@Test(expected = MosipNotPositiveException.class)
	public void getPowExceptionTest1() {
		MathUtil.getPow(2, -2);

	}

	@Test(expected = MosipArithmeticException.class)
	public void getPowExceptionTest2() {
		MathUtil.getPow(2, 9999999);
	}

	@Test
	public void getPowTest3() {
		assertThat(MathUtil.getPow(9L, 2), is(81L));
	}

	@Test(expected = MosipNotPositiveException.class)
	public void getPowExceptionTest3() {
		MathUtil.getPow(9L, -2);
	}

	@Test(expected = MosipArithmeticException.class)
	public void getPowExceptionTest4() {
		MathUtil.getPow(99999999L, 9);
	}

	@Test
	public void getPowTest4() {
		BigInteger bi = new BigInteger("2");
		assertThat(MathUtil.getPow(bi, 2), is(bi.pow(2)));
	}

	@Test(expected = MosipNotPositiveException.class)
	public void getPowExceptionTest5() {
		BigInteger bi = new BigInteger("2");
		MathUtil.getPow(bi, -2);
	}

	@Test
	public void getPowTest5() {
		BigInteger bi = new BigInteger("2");
		BigInteger bint = new BigInteger("3");
		BigInteger bi2 = new BigInteger("8");
		assertThat(MathUtil.getPow(bi, bint), is(bi2));
	}

	@Test(expected = MosipNotPositiveException.class)
	public void getPowExceptionTest6() {
		BigInteger bi = new BigInteger("2");
		BigInteger bint = new BigInteger("3");
		bint = BigInteger.ZERO.subtract(bint);
		MathUtil.getPow(bi, bint);
	}

	@Test(expected = MosipNullPointerException.class)
	public void getPowExceptionTest7() {
		MathUtil.getPow(null, null);

	}

	@Test(expected = MosipNullPointerException.class)
	public void getPowExceptionTest8() {
		MathUtil.getPow(null, 2);

	}

	@Test
	public void getSqrttest() {
		assertThat(MathUtil.getSqrt(4.0), is(2.0));
	}

	@Test
	public void getRoundtest() {
		assertThat(MathUtil.getRound(8.897, 1), is(8.9));
	}

	@Test
	public void getAbstest1() {
		assertThat(MathUtil.getAbs(-999), is(999));
	}

	@Test
	public void getAbstest2() {
		assertThat(MathUtil.getAbs(-999.0), is(999.0));
	}

	@Test
	public void getAbstest3() {
		assertThat(MathUtil.getAbs(-999L), is(999L));
	}

	@Test
	public void getAbstest4() {
		assertThat(MathUtil.getAbs(-999F), is(999F));
	}

	@Test
	public void getRoundtest2() {
		assertThat(MathUtil.getRound(4.5), is(5L));
	}

	@Test
	public void getRoundtest3() {
		assertThat(MathUtil.getRound(4.888888F, 0), is(5.0F));
	}

	@Test
	public void getLog10test() {
		assertThat(MathUtil.getLog10(10.0), is(1.0));
	}

	@Test
	public void getLogtest() {
		assertThat(MathUtil.getLog(2.7), is(0.9932517730102834));
	}

	@Test
	public void getFactorialtest() {
		assertThat(MathUtil.getFactorial(3), is(6L));
	}

	@Test(expected = MosipNotPositiveException.class)
	public void getFactorialExceptionTest1() {
		MathUtil.getFactorial(-1);
	}

	@Test(expected = MosipArithmeticException.class)
	public void getFactorialExceptionTest2() {
		MathUtil.getFactorial(999999);
	}

	@Test
	public void getMaxtest() {
		assertThat(MathUtil.getMax(2.0, 3.0), is(3.0));
	}

	@Test
	public void getMaxtest2() {
		assertThat(MathUtil.getMax(2, 3), is(3));
	}

	@Test
	public void getGcdtest() {
		assertThat(MathUtil.getGcd(2, 3), is(1));
	}

	@Test(expected = MosipArithmeticException.class)
	public void getGcdExceptionTest1() {
		MathUtil.getGcd(Integer.MIN_VALUE, Integer.MIN_VALUE);
	}

	@Test
	public void getGcdtest2() {
		assertThat(MathUtil.getGcd(2L, 3L), is(1L));
	}

	@Test(expected = MosipArithmeticException.class)
	public void getGcdExceptionTest2() {
		MathUtil.getGcd(Long.MIN_VALUE, Long.MIN_VALUE);
	}

	@Test
	public void getArrayMaxValueTest1() {
		assertThat(MathUtil.getArrayMaxValue(b), is(3.0));
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getArrayMaxValueExceptionTest1() {
		MathUtil.getArrayMaxValue(null);
	}

	@Test
	public void getArrayMaxValueTest2() {
		assertThat(MathUtil.getArrayMaxValue(b, 1, 2), is(3.0));
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getArrayMaxValueExceptionTest2() {
		MathUtil.getArrayMaxValue(b, -1, 2);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getArrayMaxValueExceptionTest3() {
		MathUtil.getArrayMaxValue(null, 0, 2);
	}

	@Test
	public void getArrayMinValueTest1() {
		assertThat(MathUtil.getArrayMinValue(b), is(1.0));
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getArrayMinValueExceptionTest1() {
		MathUtil.getArrayMinValue(null);
	}

	@Test
	public void getArrayMinValueTest2() {
		assertThat(MathUtil.getArrayMinValue(b, 1, 2), is(2.0));
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getArrayMinValueExceptionTest2() {
		MathUtil.getArrayMinValue(b, -1, 2);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getArrayMinValueExceptionTest3() {
		MathUtil.getArrayMinValue(null, 0, 2);
	}

	@Test
	public void getArrayProductTest1() {
		assertThat(MathUtil.getArrayValuesProduct(b), is(6.0));
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getArrayProductExceptionTest1() {
		MathUtil.getArrayValuesProduct(null);
	}

	@Test
	public void getArrayProductTest2() {
		assertThat(MathUtil.getArrayValuesProduct(b, 0, 2), is(2.0));
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getArrayProductExceptionTest2() {
		MathUtil.getArrayValuesProduct(b, -1, 2);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getArrayProductExceptionTest3() {
		MathUtil.getArrayValuesProduct(null, 1, 2);
	}

	@Test
	public void getArraySumTest1() {
		assertThat(MathUtil.getArrayValuesSum(b), is(6.0));
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getArraySumExceptionTest1() {
		MathUtil.getArrayValuesSum(null);
	}

	@Test
	public void getarraySumTest2() {
		assertThat(MathUtil.getArrayValuesSum(b, 1, 2), is(5.0));
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getarraySumExceptionTest2() {
		MathUtil.getArrayValuesSum(b, -1, 2);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getarraySumExceptionTest3() {
		MathUtil.getArrayValuesSum(null, 1, 2);
	}

	@Test
	public void getRandomtest1() {
		assertThat(MathUtil.getRandom(3.8F, 9.8F), is(instanceOf(Float.class)));
		Float randomFloat = MathUtil.getRandom(3.8F, 9.8F);
		assertEquals(true, 3.8F <= randomFloat && randomFloat <= 9.8F);

	}

	@Test
	public void getRandomtest2() {
		assertThat(MathUtil.getRandom(3, 9), is(instanceOf(Integer.class)));
		int randomFloat = MathUtil.getRandom(3, 9);
		assertEquals(true, 3 <= randomFloat && randomFloat <= 9);
	}

	@Test(expected = MosipNumberIsTooLargeException.class)
	public void getRandomExceptionTest2() {
		MathUtil.getRandom(4, 1);
	}

	@Test
	public void getRandomtest3() {
		assertThat(MathUtil.getRandom(8L, 9L), is(instanceOf(Long.class)));
		long randomFloat = MathUtil.getRandom(8L, 9L);
		assertEquals(true, 8L <= randomFloat && randomFloat <= 9L);
	}

	@Test(expected = MosipNumberIsTooLargeException.class)
	public void getRandomExceptionTest3() {
		MathUtil.getRandom(4L, 1L);
	}

	@Test
	public void getRandomtest4() {
		assertThat(MathUtil.getRandom(8.0, 9.0), is(instanceOf(Double.class)));
		double randomFloat = MathUtil.getRandom(8.0, 9.0);
		assertTrue(8.0 <= randomFloat && randomFloat <= 9.0);
	}

	@Test(expected = MosipNumberIsTooLargeException.class)
	public void getRandomExceptionTest4() {
		MathUtil.getRandom(6.0, 1.0);
	}

	@Test(expected = MosipNotFiniteNumberException.class)
	public void getRandomExceptionTest5() {
		MathUtil.getRandom(6, Double.POSITIVE_INFINITY);
	}

	@Test(expected = MosipNotANumberException.class)
	public void getRandomExceptionTest6() {
		MathUtil.getRandom(6, Double.NaN);
	}

	@Test
	public void isPrimeTest() {
		assertThat(MathUtil.isPrimeOrNot(2), is(true));
	}

	@Test
	public void nextPrimeTest() {
		assertThat(MathUtil.nextPrimeNumber(1), is(2));
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void nextPrimeExceptionTest() {
		MathUtil.nextPrimeNumber(-1);
	}

	@Test
	public void getArrayValueMeanTest() {
		assertThat(MathUtil.getArrayValuesMean(b), is(2.0));
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getArrayValueMeanExceptionTest1() {
		MathUtil.getArrayValuesMean(null);
	}

	@Test
	public void getArrayValueMeanTest2() {
		assertThat(MathUtil.getArrayValuesMean(b, 0, 3), is(2.0));
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getArrayValueMeanExceptionTest2() {
		MathUtil.getArrayValuesMean(b, -1, 3);
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getArrayValueMeanExceptionTest3() {
		MathUtil.getArrayValuesMean(null, 0, 3);
	}

	@Test
	public void getPrimeFactorTest() {
		assertThat(MathUtil.getPrimeFactors(18), is(list));
	}

	@Test(expected = MosipIllegalArgumentException.class)
	public void getPrimeFactorExceptionTest() {
		MathUtil.getPrimeFactors(1);
	}

	@Test
	public void ceilTest() {
		assertThat(MathUtil.ceil(9.2), is(10.0));
	}

	@Test
	public void floorTest() {
		assertThat(MathUtil.floor(9.2), is(9.0));
	}
}
