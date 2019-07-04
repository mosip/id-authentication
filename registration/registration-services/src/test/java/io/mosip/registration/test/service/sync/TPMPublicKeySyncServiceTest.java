package io.mosip.registration.test.service.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.sync.impl.TPMPublicKeySyncServiceImpl;
import io.mosip.registration.tpm.spi.TPMUtil;
import io.mosip.registration.util.restclient.ServiceDelegateUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TPMUtil.class, ApplicationContext.class })
public class TPMPublicKeySyncServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@Mock
	private ServiceDelegateUtil serviceDelegateUtil;
	@InjectMocks
	private TPMPublicKeySyncServiceImpl tpmPublicKeySyncServiceImpl;

	@Before
	public void initialize() throws Exception {
		PowerMockito.mockStatic(ApplicationContext.class);

		Map<String, Object> applicationMap = new HashMap<>();
		applicationMap.put(RegistrationConstants.REGISTRATION_CLIENT, "registrationclient");

		PowerMockito.doReturn(applicationMap).when(ApplicationContext.class, "map");
	}

	@Test
	public void syncTPMPublicKeySuccess() throws Exception {
		PowerMockito.mockStatic(TPMUtil.class);

		Map<String, Object> publicKeyResponse = new LinkedHashMap<>();
		Map<String, String> response = new LinkedHashMap<>();
		String keyIndex = "keyIndex";
		response.put(RegistrationConstants.KEY_INDEX, keyIndex);
		publicKeyResponse.put(RegistrationConstants.RESPONSE, response);

		PowerMockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(), Mockito.anyString()))
				.thenReturn(publicKeyResponse);
		PowerMockito.doReturn("signedData".getBytes()).when(TPMUtil.class, "getSigningPublicPart");

		Assert.assertEquals(keyIndex, tpmPublicKeySyncServiceImpl.syncTPMPublicKey());

	}

	@Test(expected = RegBaseCheckedException.class)
	public void syncTPMPublicKeyFailureWithNoErrors() throws Exception {
		PowerMockito.mockStatic(TPMUtil.class);

		Map<String, Object> publicKeyResponse = new LinkedHashMap<>();
		publicKeyResponse.put(RegistrationConstants.ERRORS, null);

		PowerMockito.doReturn("signedData".getBytes()).when(TPMUtil.class, "getSigningPublicPart");
		PowerMockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(), Mockito.anyString()))
				.thenReturn(publicKeyResponse);

		tpmPublicKeySyncServiceImpl.syncTPMPublicKey();

	}

	@Test(expected = RegBaseCheckedException.class)
	public void syncTPMPublicKeyFailureWithErrors() throws Exception {
		PowerMockito.mockStatic(TPMUtil.class);

		Map<String, Object> publicKeyResponse = new LinkedHashMap<>();
		List<Map<String, String>> errors = new ArrayList<>();
		Map<String, String> error = new HashMap<>();
		error.put(RegistrationConstants.ERROR_CODE, "Code");
		error.put(RegistrationConstants.MESSAGE_CODE, "Message");
		errors.add(error);
		publicKeyResponse.put(RegistrationConstants.ERRORS, errors);

		PowerMockito.doReturn("signedData".getBytes()).when(TPMUtil.class, "getSigningPublicPart");
		PowerMockito.when(serviceDelegateUtil.post(Mockito.anyString(), Mockito.any(), Mockito.anyString()))
				.thenReturn(publicKeyResponse);

		tpmPublicKeySyncServiceImpl.syncTPMPublicKey();

	}

	@Test(expected = RegBaseUncheckedException.class)
	public void syncTPMPublicKeyRuntimeException() throws Exception {
		PowerMockito.mockStatic(TPMUtil.class);

		PowerMockito.doThrow(new RuntimeException("TPM not started")).when(TPMUtil.class, "getSigningPublicPart");

		tpmPublicKeySyncServiceImpl.syncTPMPublicKey();

	}

}
