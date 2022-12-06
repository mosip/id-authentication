package io.mosip.authentication.common.service.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.kernel.core.util.DateUtils;

public class AuthTransactionBuilderTest {
	
	@Autowired
	EnvUtil env;
	
	@Mock
	IdInfoFetcher idInfoFetcher;
	
	AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance();
	
	@Test
	public void withOtpRequestTest() {
		assertNotNull(authTransactionBuilder.withRequest(new OtpRequestDTO()));
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void buildTest() throws IdAuthenticationBusinessException {
		ReflectionTestUtils.setField(authTransactionBuilder, "requestDTO", null);
	authTransactionBuilder.build(env,null,null);
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void buildTest2() throws IdAuthenticationBusinessException {
		authTransactionBuilder.build(env,null,null);
	}
	
	@Test
	public void testToString() {
		assertNotNull(authTransactionBuilder.toString());
	}
	
	@Test
	public void testRequestTypeSortOrder1() throws IdAuthenticationBusinessException {
		AuthTransactionBuilder transactionBuilder = AuthTransactionBuilder.newInstance();
		transactionBuilder.addRequestType(RequestType.FACE_AUTH);
		transactionBuilder.addRequestType(RequestType.OTP_AUTH);
		transactionBuilder.addRequestType(RequestType.EKYC_AUTH_REQUEST);
		AuthRequestDTO requestDTO = new AuthRequestDTO();
		requestDTO.setRequestTime(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
		transactionBuilder.withRequest(requestDTO);
		EnvUtil.setDateTimePattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		
		AutnTxn autnTxn = transactionBuilder.build(Mockito.mock(EnvUtil.class), Mockito.mock(IdaUinHashSaltRepo.class), Mockito.mock(IdAuthSecurityManager.class));
		assertTrue(autnTxn.getAuthTypeCode().startsWith(RequestType.EKYC_AUTH_REQUEST.getType()));
		assertTrue(autnTxn.getStatusComment().startsWith(RequestType.EKYC_AUTH_REQUEST.getMessage()));

	}
	
	@Test
	public void testRequestTypeSortOrder2() throws IdAuthenticationBusinessException {
		AuthTransactionBuilder transactionBuilder = AuthTransactionBuilder.newInstance();
		transactionBuilder.addRequestType(RequestType.DEMO_AUTH);
		transactionBuilder.addRequestType(RequestType.OTP_AUTH);
		transactionBuilder.addRequestType(RequestType.EKYC_AUTH_REQUEST);
		transactionBuilder.addRequestType(RequestType.IRIS_AUTH);
		transactionBuilder.addRequestType(RequestType.FINGER_AUTH);
		AuthRequestDTO requestDTO = new AuthRequestDTO();
		requestDTO.setRequestTime(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
		transactionBuilder.withRequest(requestDTO);
		EnvUtil.setDateTimePattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		
		AutnTxn autnTxn = transactionBuilder.build(Mockito.mock(EnvUtil.class), Mockito.mock(IdaUinHashSaltRepo.class), Mockito.mock(IdAuthSecurityManager.class));
		assertTrue(autnTxn.getAuthTypeCode().startsWith(RequestType.EKYC_AUTH_REQUEST.getType()));
		assertTrue(autnTxn.getStatusComment().startsWith(RequestType.EKYC_AUTH_REQUEST.getMessage()));

	}
	
	@Test
	public void testRequestTypeSortOrder3() throws IdAuthenticationBusinessException {
		AuthTransactionBuilder transactionBuilder = AuthTransactionBuilder.newInstance();
		transactionBuilder.addRequestType(RequestType.EKYC_AUTH_REQUEST);
		transactionBuilder.addRequestType(RequestType.IRIS_AUTH);
		transactionBuilder.addRequestType(RequestType.FINGER_AUTH);
		AuthRequestDTO requestDTO = new AuthRequestDTO();
		requestDTO.setRequestTime(DateUtils.formatToISOString(DateUtils.getUTCCurrentDateTime()));
		transactionBuilder.withRequest(requestDTO);
		EnvUtil.setDateTimePattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		
		AutnTxn autnTxn = transactionBuilder.build(Mockito.mock(EnvUtil.class), Mockito.mock(IdaUinHashSaltRepo.class), Mockito.mock(IdAuthSecurityManager.class));
		assertTrue(autnTxn.getAuthTypeCode().startsWith(RequestType.EKYC_AUTH_REQUEST.getType()));
		assertTrue(autnTxn.getStatusComment().startsWith(RequestType.EKYC_AUTH_REQUEST.getMessage()));

	}

}
