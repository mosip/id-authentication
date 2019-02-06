package io.mosip.authentication.service.impl.spin.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.Errors;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.dto.spinstore.PinRequestDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinIdentityDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.service.entity.StaticPin;
import io.mosip.authentication.service.entity.StaticPinHistory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.AuditHelper;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.authentication.service.repository.StaticPinHistoryRepository;
import io.mosip.authentication.service.repository.StaticPinRepository;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * This Test Class is for StaticPinServiceImpl
 * 
 * @author Prem Kumar
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IdTemplateManager.class,
		TemplateManagerBuilderImpl.class })
public class StaticPinServiceImplTest {

	/** The Static Pin Service */
	@InjectMocks
	private StaticPinServiceImpl staticPinServiceImpl;

	/** The Environment */
	@Autowired
	private Environment env;

	/** The IdAuthService */
	@Mock
	private IdAuthService idAuthService;

	@Mock
	private AuditHelper auditHelper;

	@InjectMocks
	DateHelper dateHelper;

	@Mock
	StaticPin staticPin;

	@Mock
	StaticPinRepository staticPinRepository;

	@Mock
	private StaticPinHistoryRepository staticPinHistoryRepo;

	@InjectMocks
	private RestRequestFactory restRequestFactory;

	@Mock
	private IdRepoService idRepoService;

	@InjectMocks
	private RestHelper restHelper;
	/** The Constant for IDA */
	private static final String IDA = "IDA";

	/** The IdRepoService **/
	@Mock
	private IdRepoService idInfoService;
	StaticPinRequestDTO staticPinRequestDTO = new StaticPinRequestDTO();
	private Errors errors = new org.springframework.validation.BindException(staticPinRequestDTO, "staticPinRequestDTO");

	@Before
	public void before() {

		ReflectionTestUtils.setField(staticPinServiceImpl, "dateHelper", dateHelper);
		ReflectionTestUtils.setField(dateHelper, "env", env);
		ReflectionTestUtils.setField(staticPinServiceImpl, "staticPinRepo", staticPinRepository);
		ReflectionTestUtils.setField(staticPinServiceImpl, "staticPinHistoryRepo", staticPinHistoryRepo);
		ReflectionTestUtils.setField(restRequestFactory, "env", env);

	}

	@Test
	public void testStorePin_Success_uin() throws IdAuthenticationBusinessException {
		
		String uin = "794138547620";
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern")))
				.toString();
		staticPinRequestDTO.setReqTime(reqTime);
		staticPinRequestDTO.setVer("1.0");
		StaticPinIdentityDTO dto = new StaticPinIdentityDTO();
		dto.setUin(uin);
		PinRequestDTO pinRequestDTO = new PinRequestDTO();
		pinRequestDTO.setIdentity(dto);
		String pin = "123454";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		StaticPin stat = new StaticPin();
		stat.setCreatedDTimes(new Date());
		stat.setPin("123456");
		stat.setUin(uin);
		StaticPinHistory staticPinHistory = new StaticPinHistory();
		staticPinHistory.setUin(uin);
		staticPinHistory.setPin(pin);
		staticPinHistory.setCreatedBy(IDA);
		staticPinHistory.setCreatedDTimes(new Date());
		staticPinHistory.setEffectiveDate(new Date());
		staticPinHistory.setActive(true);
		staticPinHistory.setDeleted(false);
		staticPinHistory.setUpdatedBy(IDA);
		staticPinHistory.setUpdatedOn(new Date());
		Optional<StaticPin> entity = Optional.of(stat);
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		Mockito.when(staticPinRepository.findById(uin)).thenReturn(entity);
		Optional<StaticPinHistory> entitySpin = Optional.of(staticPinHistory);
		Mockito.when(staticPinHistoryRepo.save(staticPinHistory)).thenReturn(staticPinHistory);
		Mockito.when(staticPinRepository.update(entity.get())).thenReturn(stat);
		ReflectionTestUtils.invokeMethod(staticPinServiceImpl, "storeSpin", staticPinRequestDTO, "794138547620");
	}
	@Test
	public void testStorePin_UniqueUin() throws IDDataValidationException {
		
		String uin = "794138547620";
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		staticPinRequestDTO.setReqTime(reqTime);
		staticPinRequestDTO.setVer("1.0");
		StaticPinIdentityDTO dto=new StaticPinIdentityDTO();
		dto.setUin(uin);
		PinRequestDTO pinRequestDTO=new PinRequestDTO();
		pinRequestDTO.setIdentity(dto);
		String pin = "123454";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		StaticPin stat = new StaticPin();
		stat.setCreatedDTimes(new Date());
		stat.setPin("123456");
		stat.setUin(uin);
		StaticPinHistory staticPinHistory = new StaticPinHistory();
		staticPinHistory.setUin(uin);
		staticPinHistory.setPin(pin);
		staticPinHistory.setCreatedBy(IDA);
		staticPinHistory.setCreatedDTimes(new Date());
		staticPinHistory.setEffectiveDate(new Date());
		staticPinHistory.setActive(true);
		staticPinHistory.setDeleted(false);
		staticPinHistory.setUpdatedBy(IDA);
		staticPinHistory.setUpdatedOn(new Date());
		Optional<StaticPin> entity1 = Optional.empty();
		Optional<StaticPin> entity = Optional.of(stat);
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		Optional<StaticPinHistory> entitySpin=Optional.of(staticPinHistory);
		errors.rejectValue(null, "test error", "test error");
		Mockito.when(staticPinRepository.findById(uin)).thenReturn(entity1);
		Mockito.when(staticPinRepository.save(stat)).thenReturn(stat);
		Mockito.when(staticPinHistoryRepo.save(staticPinHistory)).thenReturn(staticPinHistory);
		Mockito.when(staticPinRepository.update(entity.get())).thenReturn(stat);	
		ReflectionTestUtils.invokeMethod(staticPinServiceImpl, "storeSpin", staticPinRequestDTO,"794138547620");
	}
	
	
}
