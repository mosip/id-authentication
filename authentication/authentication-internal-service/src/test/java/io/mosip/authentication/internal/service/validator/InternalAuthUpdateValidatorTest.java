package io.mosip.authentication.internal.service.validator;

import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.integration.MasterDataManager;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.common.service.validator.IdAuthValidator;
import io.mosip.authentication.core.authtype.dto.AuthtypeStatus;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.spi.authtype.status.service.AuthTypeStatusDto;
import io.mosip.authentication.core.spi.indauth.match.MatchType.Category;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectPatternValidator;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.logger.logback.appender.RollingFileAppender;

/**
 * @author Prem Kumar
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class InternalAuthUpdateValidatorTest {

	@Mock
	private SpringValidatorAdapter validator;

	@Mock
	private IdObjectPatternValidator idObjectPatternValidator;

	@Autowired
	private Environment environment;

	@Mock
	Errors errors;

	@InjectMocks
	RollingFileAppender appender;

	@InjectMocks
	private InternalAuthRequestValidator internalAuthRequestValidator;

	@InjectMocks
	private UpdateAuthtypeStatusValidator updateAuthtypeStatusValidator;

	@InjectMocks
	private AuthRequestValidator baseAuthRequestValidator;

	@Mock
	IdInfoHelper idinfoHelper;

	@Mock
	UinValidatorImpl uinValidator;

	@Mock
	VidValidatorImpl vidValidator;

	@Mock
	IdAuthValidator idValidator;

	@Autowired
	Environment env;

	@Mock
	private MasterDataManager masterDataManager;

	IdAuthValidator idauthValidator = new IdAuthValidator() {

		@Override
		public void validate(Object target, Errors errors) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean supports(Class<?> clazz) {
			// TODO Auto-generated method stub
			return false;
		}
	};

	@Before
	public void before() {
		ReflectionTestUtils.setField(internalAuthRequestValidator, "env", env);
		ReflectionTestUtils.setField(internalAuthRequestValidator, "idInfoHelper", idinfoHelper);
		ReflectionTestUtils.setField(baseAuthRequestValidator, "env", env);
		ReflectionTestUtils.setField(idinfoHelper, "environment", env);
	}

	@Test
	public void testSupportTrue() {
		assertTrue(internalAuthRequestValidator.supports(AuthRequestDTO.class));
	}

	@Test
	public void TestAuthTypeSupportTrue() {
		assertTrue(updateAuthtypeStatusValidator.supports(AuthTypeStatusDto.class));
	}

	@Test
	public void TestvalidateConsentRequest() {
		AuthTypeStatusDto authTypeStatusDto = new AuthTypeStatusDto();
		Errors errors = new BeanPropertyBindingResult(authTypeStatusDto, "authTypeStatusDto");
		updateAuthtypeStatusValidator.validate(authTypeStatusDto, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestValidateId() {
		AuthTypeStatusDto authTypeStatusDto = new AuthTypeStatusDto();
		authTypeStatusDto.setConsentObtained(true);
		authTypeStatusDto.setIndividualId(null);
		authTypeStatusDto.setIndividualIdType("invalid");
		Errors errors = new BeanPropertyBindingResult(authTypeStatusDto, "authTypeStatusDto");
		updateAuthtypeStatusValidator.validate(authTypeStatusDto, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestValidateTime() {
		AuthTypeStatusDto authTypeStatusDto = new AuthTypeStatusDto();
		authTypeStatusDto.setConsentObtained(true);
		authTypeStatusDto.setIndividualId("274390482564");
		authTypeStatusDto.setIndividualIdType(IdType.UIN.getType());
		authTypeStatusDto.setRequestTime("invalid");
		Errors errors = new BeanPropertyBindingResult(authTypeStatusDto, "authTypeStatusDto");
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("request.idtypes.allowed", "VID,UIN");
		mockenv.setProperty("mosip.idtype.allowed", "UIN,VID");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		ReflectionTestUtils.setField(updateAuthtypeStatusValidator, "env", mockenv);
		updateAuthtypeStatusValidator.validate(authTypeStatusDto, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestValidateType() {
		AuthTypeStatusDto authTypeStatusDto = new AuthTypeStatusDto();
		authTypeStatusDto.setConsentObtained(true);
		authTypeStatusDto.setIndividualId("274390482564");
		authTypeStatusDto.setIndividualIdType(IdType.UIN.getType());
		ZoneOffset offset = ZoneOffset.MAX;
		authTypeStatusDto.setRequestTime(Instant.now().atOffset(offset)
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		Errors errors = new BeanPropertyBindingResult(authTypeStatusDto, "authTypeStatusDto");
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("request.idtypes.allowed", "VID,UIN");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		ReflectionTestUtils.setField(updateAuthtypeStatusValidator, "env", mockenv);
		updateAuthtypeStatusValidator.validate(authTypeStatusDto, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestValidateAuthTypeRequestwithNull() {
		AuthTypeStatusDto authTypeStatusDto = new AuthTypeStatusDto();
		authTypeStatusDto.setConsentObtained(true);
		authTypeStatusDto.setIndividualId("274390482564");
		authTypeStatusDto.setIndividualIdType(IdType.UIN.getType());
		ZoneOffset offset = ZoneOffset.MAX;
		authTypeStatusDto.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		MockEnvironment mockenv = new MockEnvironment();
		List<AuthtypeStatus> request = new ArrayList<>();
		authTypeStatusDto.setRequest(request);
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("request.idtypes.allowed", "VID,UIN");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		mockenv.setProperty("authrequest.received-time-allowed.minutes", "30");
		ReflectionTestUtils.setField(updateAuthtypeStatusValidator, "env", mockenv);
		Errors errors = new BeanPropertyBindingResult(authTypeStatusDto, "authTypeStatusDto");
		updateAuthtypeStatusValidator.validate(authTypeStatusDto, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestValidateAuthTypeRequestwithauthtypeisEmpty() {
		AuthTypeStatusDto authTypeStatusDto = new AuthTypeStatusDto();
		authTypeStatusDto.setConsentObtained(true);
		authTypeStatusDto.setIndividualId("274390482564");
		authTypeStatusDto.setIndividualIdType(IdType.UIN.getType());
		ZoneOffset offset = ZoneOffset.MAX;
		authTypeStatusDto.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		MockEnvironment mockenv = new MockEnvironment();
		List<AuthtypeStatus> request = new ArrayList<>();
		AuthtypeStatus authtypeStatus = new AuthtypeStatus();
		authtypeStatus.setAuthType(null);
		request.add(authtypeStatus);
		authTypeStatusDto.setRequest(request);
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("request.idtypes.allowed", "VID,UIN");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		mockenv.setProperty("authrequest.received-time-allowed.minutes", "30");
		ReflectionTestUtils.setField(updateAuthtypeStatusValidator, "env", mockenv);
		Errors errors = new BeanPropertyBindingResult(authTypeStatusDto, "authTypeStatusDto");
		updateAuthtypeStatusValidator.validate(authTypeStatusDto, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestValidateAuthTypeRequestwithauthtypeInvalid() {
		AuthTypeStatusDto authTypeStatusDto = new AuthTypeStatusDto();
		authTypeStatusDto.setConsentObtained(true);
		authTypeStatusDto.setIndividualId("274390482564");
		authTypeStatusDto.setIndividualIdType(IdType.UIN.getType());
		ZoneOffset offset = ZoneOffset.MAX;
		authTypeStatusDto.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		MockEnvironment mockenv = new MockEnvironment();
		List<AuthtypeStatus> request = new ArrayList<>();
		AuthtypeStatus authtypeStatus = new AuthtypeStatus();
		authtypeStatus.setAuthType("invalid");
		request.add(authtypeStatus);
		authTypeStatusDto.setRequest(request);
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("request.idtypes.allowed", "VID,UIN");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		mockenv.setProperty("authrequest.received-time-allowed.minutes", "30");
		ReflectionTestUtils.setField(updateAuthtypeStatusValidator, "env", mockenv);
		Errors errors = new BeanPropertyBindingResult(authTypeStatusDto, "authTypeStatusDto");
		updateAuthtypeStatusValidator.validate(authTypeStatusDto, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestValidateAuthTypeRequestwithauthsubtypeInvalid() {
		AuthTypeStatusDto authTypeStatusDto = new AuthTypeStatusDto();
		authTypeStatusDto.setConsentObtained(true);
		authTypeStatusDto.setIndividualId("274390482564");
		authTypeStatusDto.setIndividualIdType(IdType.UIN.getType());
		ZoneOffset offset = ZoneOffset.MAX;
		authTypeStatusDto.setRequestTime(Instant.now().atOffset(ZoneOffset.of("+0530")) // offset
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString());
		MockEnvironment mockenv = new MockEnvironment();
		List<AuthtypeStatus> request = new ArrayList<>();
		AuthtypeStatus authtypeStatus = new AuthtypeStatus();
		authtypeStatus.setAuthType(Category.BIO.getType());
		authtypeStatus.setAuthSubType("invalid");
		request.add(authtypeStatus);
		authTypeStatusDto.setRequest(request);
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("request.idtypes.allowed", "VID,UIN");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		mockenv.setProperty("authrequest.received-time-allowed.minutes", "30");
		ReflectionTestUtils.setField(updateAuthtypeStatusValidator, "env", mockenv);
		Errors errors = new BeanPropertyBindingResult(authTypeStatusDto, "authTypeStatusDto");
		updateAuthtypeStatusValidator.validate(authTypeStatusDto, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestTimestampwithOldTime() {
		AuthTypeStatusDto authTypeStatusDto = new AuthTypeStatusDto();
		authTypeStatusDto.setConsentObtained(true);
		authTypeStatusDto.setIndividualId("274390482564");
		authTypeStatusDto.setIndividualIdType(IdType.UIN.getType());
		ZoneOffset offset = ZoneOffset.MAX;
		authTypeStatusDto.setRequestTime("2019-08-06T09:06:13.092Z");
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("request.idtypes.allowed", "VID,UIN");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		mockenv.setProperty("authrequest.received-time-allowed.minutes", "30");
		ReflectionTestUtils.setField(updateAuthtypeStatusValidator, "env", mockenv);
		Errors errors = new BeanPropertyBindingResult(authTypeStatusDto, "authTypeStatusDto");
		updateAuthtypeStatusValidator.validate(authTypeStatusDto, errors);
		assertTrue(errors.hasErrors());
	}

	@Test
	public void TestTimestampwithInvalidValue() {
		String reqTime = "2019-08-06T09:06:13.";
		AuthTypeStatusDto authTypeStatusDto = new AuthTypeStatusDto();
		MockEnvironment mockenv = new MockEnvironment();
		mockenv.merge(((AbstractEnvironment) mockenv));
		mockenv.setProperty("request.idtypes.allowed", "VID,UIN");
		mockenv.setProperty("datetime.pattern", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		mockenv.setProperty("authrequest.received-time-allowed.minutes", "30");
		ReflectionTestUtils.setField(updateAuthtypeStatusValidator, "env", mockenv);
		Errors errors = new BeanPropertyBindingResult(authTypeStatusDto, "authTypeStatusDto");
		ReflectionTestUtils.invokeMethod(updateAuthtypeStatusValidator, "validateRequestTimedOut", reqTime, errors);
		assertTrue(errors.hasErrors());
	}

}
