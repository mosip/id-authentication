package io.mosip.resident.service;

import static org.junit.Assert.assertTrue;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;

import io.mosip.kernel.core.crypto.spi.CryptoCoreSpec;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.resident.dto.AuthTypeStatusResponseDto;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.service.impl.IdAuthServiceImpl;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.TokenGenerator;

@RunWith(MockitoJUnitRunner.class)
@RefreshScope
@ContextConfiguration
public class IdAuthServiceTest {
	@Mock
	private KeyGenerator keyGenerator;

	@Mock
	private TokenGenerator tokenGenerator;

	@Mock
	private Environment environment;

	@Mock
	private ResidentServiceRestClient restClient;

	@Mock
	private CryptoCoreSpec<byte[], byte[], SecretKey, PublicKey, PrivateKey, String> encryptor;

	@InjectMocks
	private IdAuthService idAuthService = new IdAuthServiceImpl();

	@Before
	public void setup() {

	}

	@Test
	public void testAuthTypeStatusUpdateSuccess() throws ApisResourceAccessException, ResidentServiceCheckedException {
		AuthTypeStatusResponseDto authTypeStatusResponseDto = new AuthTypeStatusResponseDto();
		Mockito.when(restClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(authTypeStatusResponseDto);
		List<String> authTypes = new ArrayList<>();
		authTypes.add("bio-FIR");
		boolean isUpdated = idAuthService.authTypeStatusUpdate("1234567891", "UIN", authTypes, true);
		assertTrue(isUpdated);
	}

	@Test(expected = ApisResourceAccessException.class)
	public void testAuthTypeStatusUpdateFailure() throws ApisResourceAccessException, ResidentServiceCheckedException {

		Mockito.when(restClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new ApisResourceAccessException());
		List<String> authTypes = new ArrayList<>();
		authTypes.add("bio-FIR");
		boolean isUpdated = idAuthService.authTypeStatusUpdate("1234567891", "UIN", authTypes, true);
		assertTrue(isUpdated);
	}
}
