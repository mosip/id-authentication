package io.mosip.authetication.service.staticpin.facade;


import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.entity.StaticPin;
import io.mosip.authentication.common.service.entity.StaticPinHistory;
import io.mosip.authentication.common.service.entity.VIDEntity;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.RestHelper;
import io.mosip.authentication.common.service.integration.IdRepoManager;
import io.mosip.authentication.common.service.repository.StaticPinHistoryRepository;
import io.mosip.authentication.common.service.repository.StaticPinRepository;
import io.mosip.authentication.common.service.repository.VIDRepository;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.vid.VIDResponseDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.id.service.VIDService;
import io.mosip.authentication.core.staticpin.dto.PinRequestDTO;
import io.mosip.authentication.core.staticpin.dto.StaticPinRequestDTO;
import io.mosip.authentication.staticpin.service.impl.StaticPinServiceImpl;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.idgenerator.vid.impl.VidGeneratorImpl;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * Test class for StaticPinFacadeImpl
 * 
 * @author Prem Kumar
 *
 */
@Ignore
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, TemplateManagerBuilderImpl.class })
public class StaticPinFacadeImplTest {

	/** The Static Pin Service */
	@InjectMocks
	private StaticPinServiceImpl staticPinServiceImpl;

	/** The Environment */
	@Autowired
	private Environment env;

	/** The IdAuthService */
	@Mock
	private IdService<AutnTxn> idAuthService;

	/** The Audit Helper */
	@Mock
	private AuditHelper auditHelper;

	/** The StaticPin Entity */
	@Mock
	StaticPin staticPin;

	/** The Static Pin Repository */
	@Mock
	StaticPinRepository staticPinRepository;

	/** Static Pin History Repository */
	@Mock
	private StaticPinHistoryRepository staticPinHistoryRepo;

	/** The RestRequest Factory */
	@InjectMocks
	private RestRequestFactory restRequestFactory;

	/** The Id Repo Service */
	@Mock
	private IdRepoManager idRepoManager;

	/** The Rest Helper */
	@InjectMocks
	private RestHelper restHelper;

	@Mock
	private VIDRepository vidRepository;

	@Mock
	private VidGeneratorImpl vidGenerator;

	@InjectMocks
	private VIDService vidService;

	/** The Constant for IDA */
	private static final String IDA = "IDA";

	private static final String DATETIME_PATTERN = "datetime.pattern";

	@Before
	public void before() {
		ReflectionTestUtils.setField(staticPinServiceImpl, "auditHelper", auditHelper);
		ReflectionTestUtils.setField(staticPinServiceImpl, "env", env);
		ReflectionTestUtils.setField(staticPinServiceImpl, "staticPinRepo", staticPinRepository);
		ReflectionTestUtils.setField(staticPinServiceImpl, "staticPinHistoryRepo", staticPinHistoryRepo);
		ReflectionTestUtils.setField(staticPinServiceImpl, "idAuthService", idAuthService);
		ReflectionTestUtils.setField(restRequestFactory, "env", env);
		ReflectionTestUtils.setField(vidService, "env", env);

	}

	@Test
	public void testStorePin_Success_uin() throws IdAuthenticationBusinessException {
		StaticPinRequestDTO staticPinRequestDTO = new StaticPinRequestDTO();
		String uin = "4950679436";
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		staticPinRequestDTO.setIndividualIdType(IdType.VID.getType());
		String reqTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern")))
				.toString();
		staticPinRequestDTO.setRequestTime(reqTime);
		staticPinRequestDTO.setVersion("1.0");
		PinRequestDTO pinRequestDTO = new PinRequestDTO();
		String pin = "123454";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		StaticPin stat = new StaticPin(pin, uin, true, IDA, now(), IDA, now(), false, now());
		StaticPinHistory staticPinHistory = new StaticPinHistory(pin, uin, true, IDA, now(), IDA, now(), false, now(),
				now());

		Optional<StaticPin> entity = Optional.of(stat);
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", uin);
		idRepo.put("registrationId", "1234567890");
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(idAuthService.processIdType(IdType.UIN.getType(), uin, false)).thenReturn(idRepo);
		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(repoDetails(uin));

		Mockito.when(idAuthService.getIdInfo(repoDetails(uin))).thenReturn(idInfo);

		Mockito.when(staticPinRepository.findById(uin)).thenReturn(entity);
		Mockito.when(staticPinHistoryRepo.save(staticPinHistory)).thenReturn(staticPinHistory);
		Mockito.when(staticPinRepository.update(entity.get())).thenReturn(stat);
		staticPinServiceImpl.storeSpin(staticPinRequestDTO);
	}

	@Test
	public void testStorePin_Success_vid() throws IdAuthenticationBusinessException {
		StaticPinRequestDTO staticPinRequestDTO = new StaticPinRequestDTO();
		String vid = "5371843613598206";
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
		StaticPin stat = new StaticPin(pin, vid, true, IDA, now(), IDA, now(), false, now());
		StaticPinHistory staticPinHistory = new StaticPinHistory(pin, vid, true, IDA, now(), IDA, now(), false, now(),
				now());

		Optional<StaticPin> entity = Optional.of(stat);
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", "284169042058");
		idRepo.put("registrationId", "1234567890");
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(idAuthService.processIdType(IdType.VID.getType(), vid, false)).thenReturn(idRepo);
		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(repoDetails(vid));

		Mockito.when(idAuthService.getIdInfo(repoDetails(vid))).thenReturn(idInfo);

		Mockito.when(staticPinRepository.findById(vid)).thenReturn(entity);
		Mockito.when(staticPinHistoryRepo.save(staticPinHistory)).thenReturn(staticPinHistory);
		Mockito.when(staticPinRepository.update(entity.get())).thenReturn(stat);
		staticPinServiceImpl.storeSpin(staticPinRequestDTO);
	}

	@Test
	public void testStorePin_Failure() throws IdAuthenticationBusinessException {
		StaticPinRequestDTO staticPinRequestDTO = new StaticPinRequestDTO();
		String vid = "5371843613598206";
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		staticPinRequestDTO.setIndividualIdType(IdType.VID.getType());
		String reqTime = ZonedDateTime.now().format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern")))
				.toString();
		staticPinRequestDTO.setRequestTime(reqTime);
		staticPinRequestDTO.setVersion("1.0");
		PinRequestDTO pinRequestDTO = new PinRequestDTO();
		String pin = "123454";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		StaticPin stat = new StaticPin(pin, vid, true, IDA, now(), IDA, now(), false, now());
		StaticPinHistory staticPinHistory = new StaticPinHistory(pin, vid, true, IDA, now(), IDA, now(), false, now(),
				now());

		Optional<StaticPin> entity = Optional.of(stat);
		Map<String, Object> idRepo = new HashMap<>();
		idRepo.put("uin", null);
		idRepo.put("registrationId", "1234567890");
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(idAuthService.processIdType(IdType.VID.getType(), vid, false)).thenReturn(idRepo);
		Mockito.when(idRepoManager.getIdenity(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.getIdByUin(Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(repoDetails(vid));

		Mockito.when(idAuthService.getIdInfo(repoDetails(vid))).thenReturn(idInfo);

		Mockito.when(staticPinRepository.findById(vid)).thenReturn(entity);
		Mockito.when(staticPinHistoryRepo.save(staticPinHistory)).thenReturn(staticPinHistory);
		Mockito.when(staticPinRepository.update(entity.get())).thenReturn(stat);
		staticPinServiceImpl.storeSpin(staticPinRequestDTO);
	}

	private Map<String, Object> repoDetails(String uin) {
		Map<String, Object> map = new HashMap<>();
		map.put("uin", "284169042058");
		return map;
	}

	private LocalDateTime now() throws IdAuthenticationBusinessException {
		try {
			return DateUtils.parseUTCToLocalDateTime(
					DateUtils.formatDate(new Date(), env.getProperty(DATETIME_PATTERN)),
					env.getProperty(DATETIME_PATTERN));
		} catch (ParseException e) {

			throw new IDDataValidationException(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(),
							"DATETIME_PATTERN"),
					e);
		}
	}

	@Test
	public void generateVIDTest() throws IdAuthenticationBusinessException {
		Map<String, Object> uinMap = new HashMap<>();
		uinMap.put("uin", "2342342344");
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(uinMap);
		Mockito.when(vidRepository.findByUIN(Mockito.anyString(), Mockito.any())).thenReturn(Collections.emptyList());
		VIDResponseDTO vidResponseDTO = vidService.generateVID("2342342344");
		assertEquals("mosip.identity.vid", vidResponseDTO.getId());
	}

//	(expected = IdAuthenticationBusinessException.class)
	@Test
	public void generateVIDAlreadyexists() throws IdAuthenticationBusinessException {
		Map<String, Object> uinMap = new HashMap<>();
		uinMap.put("uin", "2342342344");
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(uinMap);
		VIDEntity vidEntity = new VIDEntity();
		vidEntity.setExpiryDate(LocalDateTime.of(2050, 9, 12, 9, 54));
		vidEntity.setActive(true);
		List<VIDEntity> vidEntityList = new ArrayList<>();
		vidEntityList.add(vidEntity);
		Mockito.when(vidRepository.findByUIN(Mockito.anyString(), Mockito.any())).thenReturn(vidEntityList);
		vidService.generateVID("2342342344");
	}

	@Test
	public void generateVIDExpired() throws IdAuthenticationBusinessException {
		Map<String, Object> uinMap = new HashMap<>();
		uinMap.put("uin", "2342342344");
		Mockito.when(idAuthService.processIdType(Mockito.any(), Mockito.any(), Mockito.anyBoolean()))
				.thenReturn(uinMap);
		VIDEntity vidEntity = new VIDEntity();
		vidEntity.setExpiryDate(LocalDateTime.of(2019, 2, 12, 9, 54, 54, 567));
		vidEntity.setActive(true);
		List<VIDEntity> vidEntityList = new ArrayList<>();
		vidEntityList.add(vidEntity);
		Mockito.when(vidRepository.findByUIN(Mockito.anyString(), Mockito.any())).thenReturn(vidEntityList);
		VIDResponseDTO vidResponseDTO = vidService.generateVID("2342342344");
		assertEquals("mosip.identity.vid", vidResponseDTO.getId());
	}

}
