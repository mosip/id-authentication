package io.mosip.authentication.service.staticpin.service;

import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.entity.StaticPin;
import io.mosip.authentication.common.service.entity.StaticPinHistory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.integration.IdRepoManager;
import io.mosip.authentication.common.service.repository.StaticPinHistoryRepository;
import io.mosip.authentication.common.service.repository.StaticPinRepository;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.staticpin.dto.PinRequestDTO;
import io.mosip.authentication.core.staticpin.dto.StaticPinRequestDTO;
import io.mosip.authentication.core.staticpin.dto.StaticPinResponseDTO;
import io.mosip.authentication.staticpin.service.impl.StaticPinServiceImpl;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * This Test Class is for StaticPinServiceImpl
 * 
 * @author Prem Kumar
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, TemplateManagerBuilderImpl.class })
public class StaticPinServiceImplTest {

	/** The Static Pin Service */
	@InjectMocks
	private StaticPinServiceImpl staticPinServiceImpl;

	/** The Environment */
	@Autowired
	private Environment env;

	/** The IdAuthService */
	@Mock
	private IdService<AutnTxn> idAuthService;

	@Mock
	private AuditHelper auditHelper;

	@Mock
	StaticPin staticPin;

	@Mock
	StaticPinRepository staticPinRepository;

	@Mock
	private StaticPinHistoryRepository staticPinHistoryRepo;

	@InjectMocks
	private RestRequestFactory restRequestFactory;

	@InjectMocks
	private RestHelper restHelper;
	/** The Constant for IDA */
	private static final String IDA = "IDA";

	/** The IdRepoService **/
	@Mock
	private IdRepoManager idRepoManager;

	StaticPinRequestDTO staticPinRequestDTO = new StaticPinRequestDTO();
	private Errors errors = new org.springframework.validation.BindException(staticPinRequestDTO,
			"staticPinRequestDTO");

	@Before
	public void before() {
		ReflectionTestUtils.setField(staticPinServiceImpl, "staticPinRepo", staticPinRepository);
		ReflectionTestUtils.setField(staticPinServiceImpl, "env", env);
		ReflectionTestUtils.setField(staticPinServiceImpl, "staticPinHistoryRepo", staticPinHistoryRepo);
		ReflectionTestUtils.setField(restRequestFactory, "env", env);
	}

	@Test
	public void testStorePin_Success_uin() throws IdAuthenticationBusinessException {

		String uin = "794138547620";
		staticPinRequestDTO.setIndividualIdType(IdType.UIN.getType());
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern")))
				.toString();
		staticPinRequestDTO.setRequestTime(reqTime);
		staticPinRequestDTO.setVersion("1.0");
		PinRequestDTO pinRequestDTO = new PinRequestDTO();
		String pin = "123454";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		StaticPin stat = new StaticPin("123456", uin, true, IDA, now(), IDA, now(), false, now());
		StaticPinHistory staticPinHistory = new StaticPinHistory(pin, uin, true, IDA, now(), IDA, now(), false, now(),
				now());
		Optional<StaticPin> entity = Optional.of(stat);
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		Mockito.when(staticPinRepository.findById(uin)).thenReturn(entity);
		Mockito.when(staticPinHistoryRepo.save(staticPinHistory)).thenReturn(staticPinHistory);
		Mockito.when(staticPinRepository.update(entity.get())).thenReturn(stat);
		StaticPinResponseDTO storeSpin = staticPinServiceImpl.storeSpin(staticPinRequestDTO);
		assertTrue(storeSpin.isStatus());
	}

	@Test
	public void TestVidStore() throws IdAuthenticationBusinessException {
		String vid = "247334310780728918141754192454591343";
		staticPinRequestDTO.setIndividualIdType(IdType.VID.getType());
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern")))
				.toString();
		staticPinRequestDTO.setRequestTime(reqTime);
		staticPinRequestDTO.setVersion("1.0");
		PinRequestDTO pinRequestDTO = new PinRequestDTO();
		String pin = "123454";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		StaticPin stat = new StaticPin("123456", vid, true, IDA, now(), IDA, now(), false, now());
		StaticPinHistory staticPinHistory = new StaticPinHistory(pin, vid, true, IDA, now(), IDA, now(), false, now(),
				now());
		Optional<StaticPin> entity1 = Optional.empty();

		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", vid);
		idRepo.put("registrationId", "1234567890");
		errors.rejectValue(null, "test error", "test error");
		Mockito.when(staticPinRepository.findById(vid)).thenReturn(entity1);
		Mockito.when(staticPinRepository.save(stat)).thenReturn(stat);
		Mockito.when(staticPinHistoryRepo.save(staticPinHistory)).thenReturn(staticPinHistory);
		StaticPinResponseDTO storeSpin = staticPinServiceImpl.storeSpin(staticPinRequestDTO);
		assertTrue(storeSpin.isStatus());
	}

	@Test
	public void TestStorePin_save() throws IdAuthenticationBusinessException {
		String uin = "794138547620";
		staticPinRequestDTO.setIndividualIdType(IdType.UIN.getType());
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern")))
				.toString();
		staticPinRequestDTO.setRequestTime(reqTime);
		staticPinRequestDTO.setVersion("1.0");
		PinRequestDTO pinRequestDTO = new PinRequestDTO();
		String pin = "123454";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		StaticPin stat = new StaticPin("123456", uin, true, IDA, now(), IDA, now(), false, now());
		StaticPinHistory staticPinHistory = new StaticPinHistory(pin, uin, true, IDA, now(), IDA, now(), false, now(),
				now());
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(idRepo);
		Mockito.when(staticPinRepository.findById(uin)).thenReturn(Optional.empty());
		Mockito.when(staticPinRepository.save(Mockito.any())).thenReturn(stat);
		Mockito.when(staticPinHistoryRepo.save(Mockito.any())).thenReturn(staticPinHistory);
		StaticPinResponseDTO storeSpin = staticPinServiceImpl.storeSpin(staticPinRequestDTO);
		assertTrue(storeSpin.isStatus());
	}

	@Test
	public void TestStoreSPin_Update() throws IdAuthenticationBusinessException {
		String uin = "794138547620";
		staticPinRequestDTO.setIndividualIdType(IdType.UIN.getType());
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern")))
				.toString();
		staticPinRequestDTO.setRequestTime(reqTime);
		staticPinRequestDTO.setVersion("1.0");
		PinRequestDTO pinRequestDTO = new PinRequestDTO();
		String pin = "123454";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		StaticPin stat = new StaticPin("123456", uin, true, IDA, now(), IDA, now(), false, now());
		StaticPinHistory staticPinHistory = new StaticPinHistory(pin, uin, true, IDA, now(), IDA, now(), false, now(),
				now());
		Optional<StaticPin> entity = Optional.of(stat);
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(idRepo);
		Mockito.when(staticPinRepository.findById(uin)).thenReturn(entity);
		Mockito.when(staticPinRepository.update(Mockito.any())).thenReturn(stat);
		Mockito.when(staticPinHistoryRepo.save(Mockito.any())).thenReturn(staticPinHistory);
		StaticPinResponseDTO storeSpin = staticPinServiceImpl.storeSpin(staticPinRequestDTO);
		assertTrue(storeSpin.isStatus());
	}

	@Test
	public void testStorePin_UniqueUin() throws IdAuthenticationBusinessException {

		String uin = "794138547620";
		staticPinRequestDTO.setIndividualIdType(IdType.UIN.getType());
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern")))
				.toString();
		staticPinRequestDTO.setRequestTime(reqTime);
		staticPinRequestDTO.setVersion("1.0");
		PinRequestDTO pinRequestDTO = new PinRequestDTO();
		String pin = "123454";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		StaticPin stat = new StaticPin("123456", uin, true, IDA, now(), IDA, now(), false, now());
		StaticPinHistory staticPinHistory = new StaticPinHistory(pin, uin, true, IDA, now(), IDA, now(), false, now(),
				now());
		Optional<StaticPin> entity1 = Optional.empty();
		Optional<StaticPin> entity = Optional.of(stat);
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		errors.rejectValue(null, "test error", "test error");
		Mockito.when(staticPinRepository.findById(uin)).thenReturn(entity1);
		Mockito.when(staticPinRepository.save(stat)).thenReturn(stat);
		Mockito.when(staticPinHistoryRepo.save(staticPinHistory)).thenReturn(staticPinHistory);
		Mockito.when(staticPinRepository.update(entity.get())).thenReturn(stat);
		StaticPinResponseDTO storeSpin = staticPinServiceImpl.storeSpin(staticPinRequestDTO);
		assertTrue(storeSpin.isStatus());
	}

	@Test
	public void testStorePin_Failure() throws IdAuthenticationBusinessException {
		String uin = "794138547620";
		staticPinRequestDTO.setIndividualIdType(IdType.UIN.getType());
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern")))
				.toString();
		staticPinRequestDTO.setRequestTime(reqTime);
		staticPinRequestDTO.setVersion("1.0");
		PinRequestDTO pinRequestDTO = new PinRequestDTO();
		String pin = "123454";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		StaticPin stat = new StaticPin("123456", uin, true, IDA, now(), IDA, now(), false, now());
		StaticPinHistory staticPinHistory = new StaticPinHistory(pin, uin, true, IDA, now(), IDA, now(), false, now(),
				now());
		Optional<StaticPin> entity1 = Optional.empty();
		Optional<StaticPin> entity = Optional.of(stat);
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		errors.rejectValue(null, "test error", "test error");
		Mockito.when(staticPinRepository.findById(uin)).thenReturn(entity1);
		Mockito.when(staticPinRepository.save(stat)).thenReturn(stat);
		Mockito.when(staticPinHistoryRepo.save(staticPinHistory)).thenReturn(staticPinHistory);
		Mockito.when(staticPinRepository.update(entity.get())).thenReturn(stat);
		staticPinServiceImpl.storeSpin(staticPinRequestDTO);
	}

	@Test
	public void testGetUin() {
		String uin = "794138547620";
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		ReflectionTestUtils.invokeMethod(staticPinServiceImpl, "getUINValue", idRepo);
	}

	@Test
	public void testGetUin_UINEmpty() {
		String uin = "";
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		ReflectionTestUtils.invokeMethod(staticPinServiceImpl, "getUINValue", idRepo);
	}

	@Test
	public void testGetUin_UINNull() {
		String uin = null;
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		ReflectionTestUtils.invokeMethod(staticPinServiceImpl, "getUINValue", idRepo);
	}

	@Test
	public void testGetUin_DifferentKey() {
		String uin = null;
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("vid", uin);
		idRepo.put("registrationId", "1234567890");
		ReflectionTestUtils.invokeMethod(staticPinServiceImpl, "getUINValue", idRepo);
	}

	private LocalDateTime now() throws IdAuthenticationBusinessException {
		return DateUtils.getUTCCurrentDateTime();
	}

}
