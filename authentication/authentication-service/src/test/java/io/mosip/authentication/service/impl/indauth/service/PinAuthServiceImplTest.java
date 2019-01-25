package io.mosip.authentication.service.impl.indauth.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
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

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.PinInfo;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.entity.StaticPinEntity;
import io.mosip.authentication.service.factory.BiometricProviderFactory;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.repository.StaticPinRepository;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@WebMvcTest
@Import(value = {IDAMappingConfig.class,IdInfoHelper.class, BiometricProviderFactory.class})
public class PinAuthServiceImplTest {

	/** The id info helper. */
	@Autowired
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
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(pinAuthServiceImpl, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(pinAuthServiceImpl, "staticPinRepo", staticPinRepo);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);
		ReflectionTestUtils.setField(idInfoHelper, "environment", environment);
		ReflectionTestUtils.setField(idInfoHelper, "biometricProviderFactory", biometricProviderFactory);
	}
	
	@Test
	public void validPinTest() throws IdAuthenticationBusinessException {
		StaticPinEntity stat = new StaticPinEntity();
		stat.setPin("12345");
		Optional<StaticPinEntity> entityValue = Optional.of(stat);
		Mockito.when(staticPinRepo.findById(Mockito.anyString())).thenReturn(entityValue);
		AuthStatusInfo validatePin = pinAuthServiceImpl.validatePin(constructRequest(), "284169042058");
		assertTrue(validatePin.isStatus());
	}
	
	@Test
	public void invalidPinTest() throws IdAuthenticationBusinessException {
		StaticPinEntity stat = new StaticPinEntity();
		stat.setPin("123456");
		Optional<StaticPinEntity> entityValue = Optional.of(stat);
		Mockito.when(staticPinRepo.findById(Mockito.anyString())).thenReturn(entityValue);
		AuthStatusInfo validatePin = pinAuthServiceImpl.validatePin(constructRequest(), "284169042058");
		assertFalse(validatePin.isStatus());
	}
	
	private AuthRequestDTO constructRequest() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		authRequestDTO.setId("mosip.identity.auth");
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setPin(true);
		authRequestDTO.setAuthType(authType);
		authRequestDTO.setIdvId("284169042058");
		authRequestDTO.setIdvIdType("D");
		PinInfo pinInfo = new PinInfo();
		pinInfo.setType("pin");
		pinInfo.setValue("12345");
		List<PinInfo> pinInfoList= new ArrayList<PinInfo>();
		pinInfoList.add(pinInfo);
		authRequestDTO.setPinInfo(pinInfoList);
		return authRequestDTO;		
	}
}
