package io.mosip.authentication.service.impl.indauth.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import io.mosip.authentication.core.dto.indauth.AuthError;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.AuthUsageDataBit;
import io.mosip.authentication.service.impl.indauth.builder.AuthResponseBuilder;
import io.mosip.authentication.service.impl.indauth.builder.AuthStatusInfoBuilder;
import io.mosip.authentication.service.impl.indauth.builder.DemoAuthType;
import io.mosip.authentication.service.impl.indauth.builder.BitwiseInfo;

/**
 * {@code AuthResponseBuilderTest} - Test class for {@link AuthResponseBuilder}
 * 
 * @author Loganathan Sekar
 */
public class AuthResponseBuilderTest {

	@Autowired
	Environment env;
	
	@Test
	public void testAuthUsageBitsUnique() {
		Map<String, Long> bitsCountMap = Arrays.stream(AuthUsageDataBit.values())
				.map(bit -> bit.getHexNum() + "-" + bit.getBitIndex())
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		assertEquals(AuthUsageDataBit.values().length, bitsCountMap.size());
		assertTrue(bitsCountMap.values().stream().allMatch(c -> c == 1));
	}

	@Test
	public void testUsageDataHexBySettingBits() {
		BitwiseInfo bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.setBit(0);

		assertEquals("0x0000000000000001", bitwiseInfo.toString());

		bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.setBit(4);
		assertEquals("0x0000000000000010", bitwiseInfo.toString());

		bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.setBit(8);
		bitwiseInfo.setBit(9);
		bitwiseInfo.setBit(10);
		bitwiseInfo.setBit(11);
		assertEquals("0x0000000000000f00".toLowerCase(), bitwiseInfo.toString().toLowerCase());

		bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.setBit(63);
		assertEquals("0x8000000000000000".toLowerCase(), bitwiseInfo.toString().toLowerCase());

		bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.setBit(0);
		bitwiseInfo.setBit(4);
		bitwiseInfo.setBit(8);
		bitwiseInfo.setBit(9);
		bitwiseInfo.setBit(10);
		bitwiseInfo.setBit(11);
		bitwiseInfo.setBit(63);
		assertEquals("0x8000000000000f11".toLowerCase(), bitwiseInfo.toString().toLowerCase());
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testUsageDataHexBySettingBitsAIOOBE() {
		BitwiseInfo bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.setBit(-1);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testUsageDataHexBySettingBitsAIOOBE2() {
		BitwiseInfo bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.setBit(64);
	}

	@Test
	public void testUsageDataHexBySettingHex() {
		BitwiseInfo bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.setBit(16, 0);

		assertEquals("0x0000000000000001", bitwiseInfo.toString());

		bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.setBit(15, 0);
		assertEquals("0x0000000000000010", bitwiseInfo.toString());

		bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.setBit(14, 0);
		bitwiseInfo.setBit(14, 1);
		bitwiseInfo.setBit(14, 2);
		bitwiseInfo.setBit(14, 3);
		assertEquals("0x0000000000000f00".toLowerCase(), bitwiseInfo.toString().toLowerCase());

		bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.setBit(1, 3);
		assertEquals("0x8000000000000000".toLowerCase(), bitwiseInfo.toString().toLowerCase());

		bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.setBit(16, 0);
		bitwiseInfo.setBit(15, 0);
		bitwiseInfo.setBit(14, 0);
		bitwiseInfo.setBit(14, 1);
		bitwiseInfo.setBit(14, 2);
		bitwiseInfo.setBit(14, 3);
		bitwiseInfo.setBit(1, 3);
		assertEquals("0x8000000000000f11".toLowerCase(), bitwiseInfo.toString().toLowerCase());
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testUsageDataHexBySettingHexAIOOBE() {
		BitwiseInfo bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.setBit(0, 1);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testUsageDataHexBySettingHexAIOOBE2() {
		BitwiseInfo bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.setBit(17, 1);
	}

	@Test
	public void testUsageDataHexByClearingBits() {
		final BitwiseInfo bitwiseInfo = getFullySetBitwiseInfo();
		bitwiseInfo.clearBit(0);

		assertEquals("0xfffffffffffffffe", bitwiseInfo.toString());

		final BitwiseInfo bitwiseInfo2 = getFullySetBitwiseInfo();
		bitwiseInfo2.clearBit(4);
		assertEquals("0xffffffffffffffef", bitwiseInfo2.toString());

		BitwiseInfo bitwiseInfo3 = getFullySetBitwiseInfo();
		bitwiseInfo3.clearBit(8);
		bitwiseInfo3.clearBit(9);
		bitwiseInfo3.clearBit(10);
		bitwiseInfo3.clearBit(11);
		assertEquals("0xfffffffffffff0ff".toLowerCase(), bitwiseInfo3.toString().toLowerCase());

		BitwiseInfo bitwiseInfo4 = getFullySetBitwiseInfo();
		bitwiseInfo4.clearBit(63);
		assertEquals("0x7fffffffffffffff".toLowerCase(), bitwiseInfo4.toString().toLowerCase());

		bitwiseInfo4 = getFullySetBitwiseInfo();
		bitwiseInfo4.clearBit(16, 0);
		bitwiseInfo4.clearBit(15, 0);
		bitwiseInfo4.clearBit(14, 0);
		bitwiseInfo4.clearBit(14, 1);
		bitwiseInfo4.clearBit(14, 2);
		bitwiseInfo4.clearBit(14, 3);
		bitwiseInfo4.clearBit(1, 3);
		assertEquals("0x7ffffffffffff0ee".toLowerCase(), bitwiseInfo4.toString().toLowerCase());
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testUsageDataHexByClearingBitsAIOOBE() {
		BitwiseInfo bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.clearBit(-1);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testUsageDataHexByClearingBitsAIOOBE2() {
		BitwiseInfo bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.clearBit(64);
	}

	@Test
	public void testUsageDataHexByClearingHex() {
		final BitwiseInfo bitwiseInfo = getFullySetBitwiseInfo();
		bitwiseInfo.clearBit(16, 0);

		assertEquals("0xfffffffffffffffe", bitwiseInfo.toString());

		final BitwiseInfo bitwiseInfo2 = getFullySetBitwiseInfo();
		bitwiseInfo2.clearBit(15, 0);
		assertEquals("0xffffffffffffffef", bitwiseInfo2.toString());

		BitwiseInfo bitwiseInfo3 = getFullySetBitwiseInfo();
		bitwiseInfo3.clearBit(14, 0);
		bitwiseInfo3.clearBit(14, 1);
		bitwiseInfo3.clearBit(14, 2);
		bitwiseInfo3.clearBit(14, 3);
		assertEquals("0xfffffffffffff0ff".toLowerCase(), bitwiseInfo3.toString().toLowerCase());

		BitwiseInfo bitwiseInfo4 = getFullySetBitwiseInfo();
		bitwiseInfo4.clearBit(1, 3);
		assertEquals("0x7fffffffffffffff".toLowerCase(), bitwiseInfo4.toString().toLowerCase());

		bitwiseInfo4 = getFullySetBitwiseInfo();
		bitwiseInfo4.clearBit(16, 0);
		bitwiseInfo4.clearBit(15, 0);
		bitwiseInfo4.clearBit(14, 0);
		bitwiseInfo4.clearBit(14, 1);
		bitwiseInfo4.clearBit(14, 2);
		bitwiseInfo4.clearBit(14, 3);
		bitwiseInfo4.clearBit(1, 3);
		assertEquals("0x7ffffffffffff0ee".toLowerCase(), bitwiseInfo4.toString().toLowerCase());
	}

	private BitwiseInfo getFullySetBitwiseInfo() {
		final BitwiseInfo bitwiseInfo = new BitwiseInfo(16);
		IntStream.range(0, 64).forEach(i -> bitwiseInfo.setBit(i));
		return bitwiseInfo;
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testUsageDataHexByClearingHexAIOOBE() {
		BitwiseInfo bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.clearBit(0, 1);
	}

	@Test(expected = ArrayIndexOutOfBoundsException.class)
	public void testUsageDataHexByClearingHexAIOOBE2() {
		BitwiseInfo bitwiseInfo = new BitwiseInfo(16);
		bitwiseInfo.clearBit(17, 1);
	}

	@Test
	public void testAuthStatusInfoBuilder() {
//		assertTrue(AuthStatusInfoBuilder.newInstance().setStatus(true).build().isStatus());
//		assertFalse(AuthStatusInfoBuilder.newInstance().setStatus(false).build().isStatus());
//		
//		AuthStatusInfoBuilder authStatusInfoBuilder = AuthStatusInfoBuilder.newInstance();
//		AuthStatusInfo authStatusInfo = authStatusInfoBuilder
//				.addMessageInfo(DemoAuthType.PI_PRI.getType(), "P", 60)
//				.addMessageInfo(DemoAuthType.FAD_PRI.getType(), "E", 100)
//				.addAuthUsageDataBits(AuthUsageDataBit.USED_OTP, AuthUsageDataBit.MATCHED_OTP)
//				.addAuthUsageDataBits(AuthUsageDataBit.USED_PI_NAME_PRI, AuthUsageDataBit.MATCHED_PI_NAME_PRI)
//				.addErrors(new AuthError("101", "Error1"))
//				.addErrors(new AuthError("102", "Error2"), new AuthError("103", "Error3"))
//				.build();
//		assertEquals(authStatusInfo.getMatchInfos().get(0).getMs()
//				, "P");
//		assertEquals(authStatusInfo.getMatchInfos().get(1).getMt(), (Integer)100);
//		assertTrue(authStatusInfo.getUsageDataBits().size() == 4 && authStatusInfo.getUsageDataBits()
//				.containsAll(Arrays.asList(
//						AuthUsageDataBit.USED_OTP, 
//						AuthUsageDataBit.MATCHED_OTP,
//						AuthUsageDataBit.USED_PI_NAME_PRI,
//						AuthUsageDataBit.MATCHED_PI_NAME_PRI
//						)));
//		
//		assertEquals(authStatusInfo.getErr().size(), 3);
//		
//		assertTrue(authStatusInfo.getErr()
//				.stream()
//				.map(AuthError::getErrorCode)
//				.collect(Collectors.toList())
//				.containsAll(Arrays.asList("101", "102", "103")));

	}

	@Test(expected = IllegalStateException.class)
	public void testAuthStatusInfoBuilderMultipleTimes() {
		AuthStatusInfoBuilder statusInfoBuilder = AuthStatusInfoBuilder.newInstance();
		statusInfoBuilder.setStatus(true);
		statusInfoBuilder.build();

		statusInfoBuilder.addAuthUsageDataBits(AuthUsageDataBit.USED_PI_DOB);
	}

	@Test
	public void testAuthResponseInfoBuilder() {
//		assertTrue(AuthResponseBuilder
//					.newInstance()
//					.addAuthStatusInfo(AuthStatusInfoBuilder
//							.newInstance()
//							.setStatus(true)
//							.build())
//					.build()
//					.isStatus());
//		assertFalse(AuthResponseBuilder
//				.newInstance()
//				.addAuthStatusInfo(AuthStatusInfoBuilder
//						.newInstance()
//						.setStatus(false)
//						.build())
//				.build()
//				.isStatus());
//		
//		assertEquals(AuthResponseBuilder
//				.newInstance()
//				.setTxnID("1234567890")
//				.build()
//				.getTxnID(), "1234567890");
//		
//		AuthResponseDTO authResponseDTO = AuthResponseBuilder.newInstance()
//					.addErrors(new AuthError("101", "Error1"))
//					.addErrors(new AuthError("102", "Error2"), new AuthError("103", "Error3"))
//					.build();
//		
//		assertTrue(authResponseDTO.getErr().size() == 3 && authResponseDTO.getErr()
//				.stream()
//				.map(AuthError::getErrorCode)
//				.collect(Collectors.toList())
//				.containsAll(Arrays.asList("101", "102", "103")));
//		
//		
//		
////		AuthStatusInfo authStatusInfo1 = AuthStatusInfoBuilder.newInstance()
////				.addMessageInfo(DemoAuthType.PI_PRI.getType(), "P", 60)
////				.addAuthUsageDataBits(AuthUsageDataBit.USED_OTP, AuthUsageDataBit.MATCHED_OTP)
////				.addErrors(new AuthError("101", "Error1"))
////				.build();
//		
//		AuthStatusInfo authStatusInfo2 = AuthStatusInfoBuilder.newInstance()
//				.addMessageInfo(DemoAuthType.FAD_PRI.getType(), "E", 100)
//				.addAuthUsageDataBits(AuthUsageDataBit.USED_PI_NAME_PRI, AuthUsageDataBit.MATCHED_PI_NAME_PRI)
//				.addAuthUsageDataBits(AuthUsageDataBit.USED_PI_EMAIL, AuthUsageDataBit.MATCHED_PI_EMAIL)
//				.addErrors(new AuthError("102", "Error2"), new AuthError("103", "Error3"))
//				.build();
//		
//		
//		AuthResponseDTO authResponseDTO2 = AuthResponseBuilder.newInstance()
//				.addAuthStatusInfo(authStatusInfo1)
//				.addAuthStatusInfo(authStatusInfo2)
//				.build();
//		
//		assertEquals(authResponseDTO2.getInfo().getMatchInfos().get(0).getAuthType(), DemoAuthType.PI_PRI.getType());
//		assertEquals(authResponseDTO2.getInfo().getMatchInfos().get(1).getAuthType(), DemoAuthType.FAD_PRI.getType());
//		assertEquals(authResponseDTO2.getInfo().getUsageData(), "0x3400000034000000");
//		
//		assertEquals(3, authResponseDTO2.getErr().size());
//
//		assertTrue(authResponseDTO2.getErr().stream().map(AuthError::getErrorCode).collect(Collectors.toList())
//				.containsAll(Arrays.asList("101", "102", "103")));

	}

	@Ignore
	@Test(expected = IllegalStateException.class)
	public void testAuthResponseBuilderMultipleTimes() {
		AuthResponseBuilder statusInfoBuilder = new AuthResponseBuilder(env.getProperty("datetime.pattern"));
		statusInfoBuilder.setTxnID("1234567890");
		statusInfoBuilder.build();

		statusInfoBuilder.build();
	}

}
