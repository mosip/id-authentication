package io.mosip.authentication.common.service.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import io.mosip.authentication.common.service.impl.match.PinAuthType;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;

/**
 * {@code AuthResponseBuilderTest} - Test class for {@link AuthResponseBuilder}
 * 
 * @author Loganathan Sekar
 */
public class AuthResponseBuilderTest {
	@Autowired
	Environment env;
	private static String dateTimePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

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
		statusInfoBuilder.setStatus(false);
//		 statusInfoBuilder.addAuthUsageDataBits(AuthUsageDataBit.USED_PI_DOB);
	}

	@Test
	public void testAuthResponseInfoBuilder() {
		assertTrue(AuthResponseBuilder.newInstance(dateTimePattern)
				.addAuthStatusInfo(AuthStatusInfoBuilder.newInstance().setStatus(true).build()).build("123456789")
				.getResponse().isAuthStatus());
		assertFalse(AuthResponseBuilder.newInstance(dateTimePattern)
				.addAuthStatusInfo(AuthStatusInfoBuilder.newInstance().setStatus(false).build()).build("123456789")
				.getResponse().isAuthStatus());

		assertEquals(AuthResponseBuilder.newInstance(dateTimePattern).setTxnID("1234567890").build("123456789")
				.getTransactionID(), "1234567890");

		AuthResponseDTO authResponseDTO = AuthResponseBuilder.newInstance(dateTimePattern)
				.addErrors(new AuthError("101", "Error1"))
				.addErrors(new AuthError("102", "Error2"), new AuthError("103", "Error3")).build("123456789");

		assertTrue(authResponseDTO.getErrors().size() == 3
				&& authResponseDTO.getErrors().stream().map(AuthError::getErrorCode).collect(Collectors.toList())
						.containsAll(Arrays.asList("101", "102", "103")));

		AuthStatusInfo authStatusInfo1 = AuthStatusInfoBuilder.newInstance()
				// .addMatchInfo(DemoAuthType.PERSONAL_IDENTITY.getType(), "P", 60,
				// PRIMARY_LANG_CODE)
				// .addAuthUsageDataBits(AuthUsageDataBit.USED_OTP,
				// AuthUsageDataBit.MATCHED_OTP)
				.addErrors(new AuthError("101", "Error1")).build();

		AuthStatusInfo authStatusInfo2 = AuthStatusInfoBuilder.newInstance()
				// .addMatchInfo(DemoAuthType.FULL_ADDRESS.getType(), "E", 100,
				// PRIMARY_LANG_CODE)
				// .addAuthUsageDataBits(AuthUsageDataBit.USED_PI_NAME,
				// AuthUsageDataBit.MATCHED_PI_NAME)
				// .addAuthUsageDataBits(AuthUsageDataBit.USED_PI_EMAIL,
				// AuthUsageDataBit.MATCHED_PI_EMAIL)
				.addErrors(new AuthError("102", "Error2"), new AuthError("103", "Error3")).build();

		AuthResponseDTO authResponseDTO2 = AuthResponseBuilder.newInstance(dateTimePattern)
				.addAuthStatusInfo(authStatusInfo1).addAuthStatusInfo(authStatusInfo2).build("123456789");

		/*
		 * assertEquals(authResponseDTO2.getInfo().getMatchInfos().get(0).getAuthType(),
		 * DemoAuthType.PERSONAL_IDENTITY.getType());
		 * assertEquals(authResponseDTO2.getInfo().getMatchInfos().get(1).getAuthType(),
		 * DemoAuthType.FULL_ADDRESS.getType()); assertEquals("0xc2000000c2000000",
		 * authResponseDTO2.getInfo().getUsageData());
		 */

		assertEquals(3, authResponseDTO2.getErrors().size());

		assertTrue(authResponseDTO2.getErrors().stream().map(AuthError::getErrorCode).collect(Collectors.toList())
				.containsAll(Arrays.asList("101", "102", "103")));

	}

	@Test(expected = IllegalStateException.class)
	public void testAuthResponseBuilderMultipleTimes() {
		AuthResponseBuilder statusInfoBuilder = AuthResponseBuilder.newInstance(dateTimePattern);
		statusInfoBuilder.setTxnID("1234567890");
		statusInfoBuilder.build("123456789");

		statusInfoBuilder.build("123456789");
	}

	@Test
	public void TestSetId() {
		AuthResponseBuilder authResponseBuilder = AuthResponseBuilder.newInstance(dateTimePattern);
		authResponseBuilder.setId(PinAuthType.OTP.getDisplayName());
	}

	@Test
	public void TestSetStaticToken() {
		AuthResponseBuilder authResponseBuilder = AuthResponseBuilder.newInstance(dateTimePattern);
		authResponseBuilder.setStaticTokenId("TEST123");
	}

	@Test
	public void TestSetVersion() {
		AuthResponseBuilder authResponseBuilder = AuthResponseBuilder.newInstance(dateTimePattern);
		authResponseBuilder.setVersion("1.0");
	}

}
