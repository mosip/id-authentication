package io.mosip.spin.authentication.service.impl.indauth.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.config.IDAMappingConfig;
import io.mosip.authentication.common.entity.StaticPin;
import io.mosip.authentication.common.factory.BiometricProviderFactory;
import io.mosip.authentication.common.factory.RestRequestFactory;
import io.mosip.authentication.common.helper.IdInfoHelper;
import io.mosip.authentication.common.helper.RestHelper;
import io.mosip.authentication.common.impl.indauth.service.IdInfoFetcherImpl;
import io.mosip.authentication.common.integration.OTPManager;
import io.mosip.authentication.common.repository.StaticPinRepository;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.service.impl.indauth.builder.MatchInputBuilder;
import io.mosip.authentication.service.impl.indauth.service.PinAuthServiceImpl;
import io.mosip.kernel.core.util.HMACUtils;

@Ignore
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
@Import(value = { IDAMappingConfig.class })
public class PinAuthServiceImplTest {

	/** The id info helper. */
	@InjectMocks
	public IdInfoHelper idInfoHelper;

	/** The id mapping config. */
	@Autowired
	private IDAMappingConfig idMappingConfig;

	/** The environment. */
	@Autowired
	private Environment environment;

	/** The BiometricProviderFactory value */
	@Mock
	private BiometricProviderFactory biometricProviderFactory;

	/** The static pin repo. */
	@Mock
	private StaticPinRepository staticPinRepo;

	@InjectMocks
	private PinAuthServiceImpl pinAuthServiceImpl;

	@InjectMocks
	private OTPManager otpManager;

	@InjectMocks
	private RestHelper restHelper;

	@InjectMocks
	private RestRequestFactory restRequestFactory;

	@InjectMocks
	private IdInfoFetcherImpl idInfoFetcherImpl;

	@InjectMocks
	private MatchInputBuilder matchInputBuilder;

	@Before
	public void before() {
		ReflectionTestUtils.setField(pinAuthServiceImpl, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(pinAuthServiceImpl, "matchInputBuilder", matchInputBuilder);
		ReflectionTestUtils.setField(matchInputBuilder, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(idInfoHelper, "environment", environment);
		ReflectionTestUtils.setField(pinAuthServiceImpl, "staticPinRepo", staticPinRepo);
		ReflectionTestUtils.setField(idInfoFetcherImpl, "idMappingConfig", idMappingConfig);
		ReflectionTestUtils.setField(idInfoFetcherImpl, "environment", environment);
		ReflectionTestUtils.setField(idInfoFetcherImpl, "biometricProviderFactory", biometricProviderFactory);
		ReflectionTestUtils.setField(otpManager, "restHelper", restHelper);
		ReflectionTestUtils.setField(otpManager, "restRequestFactory", restRequestFactory);
	}

	@Test
	public void validPinTest() throws IdAuthenticationBusinessException {
		StaticPin stat = new StaticPin();
		stat.setPin(HMACUtils.digestAsPlainText(HMACUtils.generateHash(("12345").getBytes())));
		Optional<StaticPin> entityValue = Optional.of(stat);
		Mockito.when(staticPinRepo.findById(Mockito.anyString())).thenReturn(entityValue);
		AuthStatusInfo validatePin = pinAuthServiceImpl.authenticate(constructRequest(), "284169042058",
				Collections.emptyMap(), "123456");
		assertTrue(validatePin.isStatus());
	}

	@Test
	public void invalidPinTest() throws IdAuthenticationBusinessException {
		StaticPin stat = new StaticPin();
		stat.setPin("123456");
		Optional<StaticPin> entityValue = Optional.of(stat);
		Mockito.when(staticPinRepo.findById(Mockito.anyString())).thenReturn(entityValue);
		AuthStatusInfo validatePin = pinAuthServiceImpl.authenticate(constructRequest(), "284169042058",
				Collections.emptyMap(), "123456");
		assertFalse(validatePin.isStatus());
	}

	private AuthRequestDTO constructRequest() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setPin(true);
		authRequestDTO.setRequestedAuth(authType);
		authRequestDTO.setIndividualId("284169042058");
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.setStaticPin("12345");
		authRequestDTO.setRequest(requestDTO);
		return authRequestDTO;
	}
}
