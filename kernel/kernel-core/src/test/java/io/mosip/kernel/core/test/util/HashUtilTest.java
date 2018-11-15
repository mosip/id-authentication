package io.mosip.kernel.core.test.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import io.mosip.kernel.core.util.HashUtils;
import io.mosip.kernel.core.util.exception.HashUtilException;
/**
 * This is a class for unit testing of various methods of HashUtil class.
 * @version 1.0   10 August 2018
 * @author Jyoti Prakash Nayak
 *
 */
public class HashUtilTest {
	/**
	 * @throws HashUtilException  HashUtil(int initialNumber, int multiplierNumber) 
	 * will throw a MosipHashUtilException due to initialNumber  being an even number.
	 */
	@Test(expected=HashUtilException.class)
	public void testHashUtilconstructorExceptionforInitialNumber() throws HashUtilException {	
		new HashUtils(22,35);
	}
	/**
	 * @throws HashUtilException  HashUtil(int initialNumber, int multiplierNumber) 
	 * will throw a MosipHashUtilException due to multiplierNumber  being an even number.
	 */
	@Test(expected=HashUtilException.class)
	public void testHashUtilconstructorExceptionforMultiplier() throws HashUtilException {	
		new HashUtils(23,44);
	}
	/**
	 * HashUtil.build() method will provide expected result if all accurate input parameters 
	 * have been provided in one particular order
	 * @throws HashUtilException HashUtil(int initialNumber, int multiplierNumber) 
	 * will throw a MosipHashUtilException if the entered number  is an even number.
	 */
	@Test
	public void testBuild() throws HashUtilException {
		
		short sh=121;
		byte b=14;
		boolean[] bool1=null;
		int[] int1=null;
		double[] dob=null;
		String[] str=null;
		byte[]	byt=null;
		short[] sho=null;
		long[] lon=null;
		float[] flo=null;
		char[] chr=null;
		Object obj=null;
		assertEquals(((((((((7919l*7664345821815920749L)+34)*7664345821815920749L)*7664345821815920749L)+1)*
				7664345821815920749L)+ Double.doubleToLongBits(12.3))
				*7664345821815920749L)+Arrays.asList("Ramesh","Suresh").hashCode(), 
				new HashUtils().append(34).append(obj).append(false).append(12.3).append(Arrays.
						asList("Ramesh","Suresh")).build());
		
		assertEquals((((((((((23l*45)+123456789000l)*45)+ 
				Float.floatToIntBits(2.453f))*45)+ sh)*45)+ b)*45)+(long) 'r', 
				new HashUtils(23,45).append(123456789000l).append(2.453f).append(sh).append(b).append('r').build());
		
		assertEquals(23l*45*45*45*45,
				new HashUtils(23, 45).append(str).append(int1).
				append(bool1).append(dob).build());
		
		assertEquals(23l*45*45*45*45*45,
				new HashUtils(23,45).append(byt).append(sho).append(lon).append(flo).append(chr).build());
		
	}
	/**
	 * HashUtil.toHashCode() method will provide expected result if all accurate input parameters 
	 * have been provided in one particular order
	 * @throws HashUtilException HashUtil(int initialNumber, int multiplierNumber) 
	 * will throw a MosipHashUtilException if the entered number  is an even number.
	 */
	@Test
	public void testToHashCode() throws HashUtilException {
		short sh=121;
		 byte b=14;
		Object obj1=new	String[] {"Ra","sh"};
		Object obj2=new int[] {23,46,74};
		Object obj3=new boolean[] {true,false};
		Object obj4=new double[] {12.3,45.7};
		Object obj5=new byte[] {51,60};
		Object obj6=new short[] {121,127};
		Object obj7=new long[] {23,46,74};
		Object obj8=new float[] {20f,23.567f};
		Object obj9=new char[] {'r','e'};
		
		 assertEquals(((((((((((23l*45)+34)*45)+(long)'R')*45)+(long)'a')*45)+(long)'m')*45+1)*45)+Double.
				 doubleToLongBits(12.3) , 
					new HashUtils(23, 45).append(34).append("Ram").append(false).append(12.3).toHashCode());
						
			assertEquals((((((((((23l*45)+123456789000l)*45)+ 
					Float.floatToIntBits(2.453f))*45)+ sh)*45)+ b)*45)+(long) 'r', 
					new HashUtils(23,45).append(123456789000l).append(2.453f).append(sh).append(b).append('r').
					toHashCode());
									
			assertEquals(((((((((((((((((((((23l*45)+(long)'R')*45)+(long)'a')*45)+(long)'s')*45)+(long)'h')*45+
					23)*45)+46)*45)+74)*45)+0)*45)+1)*45)+Double.doubleToLongBits(12.3) )*45)+
					Double.doubleToLongBits(45.7) ,
					new HashUtils(23, 45).append(new String[] {"Ra","sh"}).append(new int[] {23,46,74}).
					append(new boolean[] {true,false}).append(new double[] {12.3,45.7}).toHashCode());
			
			assertEquals((((((((((((((((((((((23l*45)+(long)'R')*45)+(long)'a')*45)+(long)'s')*45)+(long)'h')*45)
					+23)*45)+46)*45)+74)*45)+0)*45)+1)*45)+Double.doubleToLongBits(12.3) )*45)+
	                Double.doubleToLongBits(45.7),
					new HashUtils(23, 45).append(obj1).append(obj2).append(obj3).append(obj4).toHashCode());
			
			assertEquals((((((((((((((((((((((23l*45)+51)*45)+60)*45)+ 121)*45)+ 127)*45)+
					23)*45)+46)*45)+74)*45)+ Float.floatToIntBits(20f))
					*45)+ Float.floatToIntBits(23.567f))*45)+(long) 'r')*45)+(long) 'e',
					new HashUtils(23,45).append(new byte[] {51,60}).append(new short[] {121,127}).append(new long[] 
					{23,46,74}).append(new float[] {20f,23.567f}).append(new char[] {'r','e'}).toHashCode());
			
			assertEquals((((((((((((((((((((((23l*45)+51)*45)+60)*45)+ 121)*45)+ 127)*45)+
					23)*45)+46)*45)+74)*45)+ Float.floatToIntBits(20f))
					*45)+(long) Float.floatToIntBits(23.567f))*45)+(long) 'r')*45)+(long) 'e',
					new HashUtils(23,45).append(obj5).append(obj6).append(obj7).append(obj8).append(obj9).toHashCode());
	}
}
