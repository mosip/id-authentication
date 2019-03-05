package io.mosip.authentication.service.impl.indauth.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthTypeDTO;
import io.mosip.authentication.core.dto.indauth.IdentityDTO;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.RequestDTO;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.factory.IDAMappingFactory;
import io.mosip.authentication.service.helper.IdInfoHelper;

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IDAMappingConfig.class,
		IDAMappingFactory.class })
public class IdaMappingTest {

	@InjectMocks
	private AuthRequestValidator authRequestValidator;

	@InjectMocks
	private AuthRequestValidator baseauthRequestValidator;

	@InjectMocks
	private IdInfoHelper idinfoHelper;

	@Autowired
	private IDAMappingConfig idMappingConfig;

	@Autowired
	private Environment env;

	@Before
	public void before() {
		ReflectionTestUtils.setField(authRequestValidator, "env", env);
		ReflectionTestUtils.setField(authRequestValidator, "idInfoHelper", idinfoHelper);
		ReflectionTestUtils.setField(idinfoHelper, "environment", env);
		ReflectionTestUtils.setField(idinfoHelper, "idMappingConfig", idMappingConfig);

	}

	@Test
	public void checkOtherValuesTest() {
		AuthRequestDTO authRequestDTO = new AuthRequestDTO();
		AuthTypeDTO authType = new AuthTypeDTO();
		authType.setBio(false);
		authType.setOtp(false);
		authType.setDemo(false);
		authType.setPin(false);
		authRequestDTO.setRequestedAuth(authType);
		List<IdentityInfoDTO> dobList = new ArrayList();
		IdentityInfoDTO dobEntity = new IdentityInfoDTO();
		dobEntity.setLanguage(null);
		dobEntity.setValue(Instant.now().atOffset(ZoneOffset.of("+0530"))
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		dobList.add(dobEntity);
		RequestDTO requestDTO = new RequestDTO();
		IdentityDTO identityDTO = new IdentityDTO();
		identityDTO.setDob(dobList);
		requestDTO.setIdentity(identityDTO);
		authRequestDTO.setRequest(requestDTO);
		Errors errors = new BeanPropertyBindingResult(authRequestDTO, "authRequestDTO");
		Set<String> availableAuthTypeInfos = new HashSet<>();
		ReflectionTestUtils.invokeMethod(authRequestValidator, "checkOtherValues", authRequestDTO, errors,
				availableAuthTypeInfos);
		assertTrue(errors.hasErrors());
	}

}
