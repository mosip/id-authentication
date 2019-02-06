package io.mosip.authetication.service.impl.spin.facade;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
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

import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.spinstore.PinRequestDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinIdentityDTO;
import io.mosip.authentication.core.dto.spinstore.StaticPinRequestDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.service.entity.StaticPin;
import io.mosip.authentication.service.entity.StaticPinHistory;
import io.mosip.authentication.service.factory.RestRequestFactory;
import io.mosip.authentication.service.helper.AuditHelper;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.authentication.service.helper.RestHelper;
import io.mosip.authentication.service.impl.spin.facade.StaticPinFacadeImpl;
import io.mosip.authentication.service.impl.spin.service.StaticPinServiceImpl;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.authentication.service.repository.StaticPinHistoryRepository;
import io.mosip.authentication.service.repository.StaticPinRepository;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * Test class for StaticPinFacadeImpl
 * 
 * @author Prem Kumar
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IdTemplateManager.class,
		TemplateManagerBuilderImpl.class })
public class StaticPinFacadeImplTest {
	
	/** The Static Pin FacadeImpl */
	@InjectMocks
	private StaticPinFacadeImpl pinFacadeImpl;
	
	/** The Static Pin Service */
	@InjectMocks
	private StaticPinServiceImpl staticPinServiceImpl;
	
	/** The Environment */
	@Autowired
	private Environment env;
	
	/** The IdAuthService */
	@Mock
	private IdAuthService idAuthService;
	
	/** The Audit Helper */
	@Mock
	private AuditHelper auditHelper;
	
	/** The Date Helper */
	@InjectMocks
	DateHelper dateHelper;
	
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
	private IdRepoService idRepoService;
	
	/** The Rest Helper */
	@InjectMocks
	private RestHelper restHelper;
	
	/** The Constant for IDA*/
	private static final String IDA = "IDA";
	
	/** The IdRepoService **/
	@Mock
	private IdRepoService idInfoService;

	
	@Before
	public void before() {
		ReflectionTestUtils.setField(pinFacadeImpl, "auditHelper", auditHelper);
		ReflectionTestUtils.setField(pinFacadeImpl, "env", env);
		ReflectionTestUtils.setField(pinFacadeImpl, "staticPinService", staticPinServiceImpl);
		ReflectionTestUtils.setField(staticPinServiceImpl, "dateHelper", dateHelper);
		ReflectionTestUtils.setField(dateHelper, "env", env);
		ReflectionTestUtils.setField(staticPinServiceImpl, "staticPinRepo", staticPinRepository);
		ReflectionTestUtils.setField(staticPinServiceImpl, "staticPinHistoryRepo", staticPinHistoryRepo);
		ReflectionTestUtils.setField(pinFacadeImpl, "idAuthService", idAuthService);
		ReflectionTestUtils.setField(restRequestFactory, "env", env);

	}
	
	@Test
	public void testStorePin_Success_uin() throws IdAuthenticationBusinessException {
		StaticPinRequestDTO staticPinRequestDTO=new StaticPinRequestDTO();
		String uin = "794138547620";
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		staticPinRequestDTO.setReqTime(reqTime);
		staticPinRequestDTO.setVer("1.0");
		staticPinRequestDTO.setTspID("TSP0001");
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
		Mockito.when(idAuthService.processIdType(IdType.UIN.getType(), uin, false))
		.thenReturn(idRepo);
		Mockito.when(idRepoService.getIdRepo(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.getIdRepoByUinNumber(Mockito.anyString(), Mockito.anyBoolean()))
		.thenReturn(repoDetails(uin));

		Mockito.when(idInfoService.getIdInfo(repoDetails(uin))).thenReturn(idInfo);

		Mockito.when(staticPinRepository.findById(uin)).thenReturn(entity);
		Mockito.when(staticPinHistoryRepo.save(staticPinHistory)).thenReturn(staticPinHistory);
		Mockito.when(staticPinRepository.update(entity.get())).thenReturn(stat);
		pinFacadeImpl.storeSpin(staticPinRequestDTO);
	}
	
	@Test
	public void testStorePin_Success_vid() throws IdAuthenticationBusinessException {
		StaticPinRequestDTO staticPinRequestDTO=new StaticPinRequestDTO();
		String vid = "5371843613598206";
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		staticPinRequestDTO.setReqTime(reqTime);
		staticPinRequestDTO.setVer("1.0");
		staticPinRequestDTO.setTspID("TSP0001");
		StaticPinIdentityDTO dto=new StaticPinIdentityDTO();
		dto.setVid(vid);
		PinRequestDTO pinRequestDTO=new PinRequestDTO();
		pinRequestDTO.setIdentity(dto);
		String pin = "123454";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		StaticPin stat = new StaticPin();
		stat.setCreatedDTimes(new Date());
		stat.setPin("123456");
		stat.setUin(vid);
		StaticPinHistory staticPinHistory = new StaticPinHistory();
		staticPinHistory.setUin(vid);
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
		idRepo.put("uin", "284169042058");
		idRepo.put("registrationId", "1234567890");
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(idAuthService.processIdType(IdType.VID.getType(), vid, false))
		.thenReturn(idRepo);
		Mockito.when(idRepoService.getIdRepo(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.getIdRepoByUinNumber(Mockito.anyString(), Mockito.anyBoolean()))
		.thenReturn(repoDetails(vid));

		Mockito.when(idInfoService.getIdInfo(repoDetails(vid))).thenReturn(idInfo);

		Mockito.when(staticPinRepository.findById(vid)).thenReturn(entity);
		Mockito.when(staticPinHistoryRepo.save(staticPinHistory)).thenReturn(staticPinHistory);
		Mockito.when(staticPinRepository.update(entity.get())).thenReturn(stat);
		pinFacadeImpl.storeSpin(staticPinRequestDTO);
	}
	
	@Test
	public void testStorePin_Failure() throws IdAuthenticationBusinessException {
		StaticPinRequestDTO staticPinRequestDTO=new StaticPinRequestDTO();
		String vid = "5371843613598206";
		staticPinRequestDTO.setId("mosip.identity.static-pin");
		String reqTime = ZonedDateTime.now()
				.format(DateTimeFormatter.ofPattern(env.getProperty("datetime.pattern"))).toString();
		staticPinRequestDTO.setReqTime(reqTime);
		staticPinRequestDTO.setVer("1.0");
		staticPinRequestDTO.setTspID("TSP0001");
		StaticPinIdentityDTO dto=new StaticPinIdentityDTO();
		dto.setVid(vid);
		PinRequestDTO pinRequestDTO=new PinRequestDTO();
		pinRequestDTO.setIdentity(dto);
		String pin = "123454";
		pinRequestDTO.setStaticPin(pin);
		staticPinRequestDTO.setRequest(pinRequestDTO);
		StaticPin stat = new StaticPin();
		stat.setCreatedDTimes(new Date());
		stat.setPin("123456");
		stat.setUin(vid);
		StaticPinHistory staticPinHistory = new StaticPinHistory();
		staticPinHistory.setUin(vid);
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
		idRepo.put("uin", null);
		idRepo.put("registrationId", "1234567890");
		List<IdentityInfoDTO> list = new ArrayList<IdentityInfoDTO>();
		list.add(new IdentityInfoDTO("en", "mosip"));
		Map<String, List<IdentityInfoDTO>> idInfo = new HashMap<>();
		idInfo.put("name", list);
		idInfo.put("email", list);
		idInfo.put("phone", list);
		Mockito.when(idAuthService.processIdType(IdType.VID.getType(), vid, false))
		.thenReturn(idRepo);
		Mockito.when(idRepoService.getIdRepo(Mockito.anyString(), Mockito.anyBoolean())).thenReturn(idRepo);
		Mockito.when(idAuthService.getIdRepoByUinNumber(Mockito.anyString(), Mockito.anyBoolean()))
		.thenReturn(repoDetails(vid));

		Mockito.when(idInfoService.getIdInfo(repoDetails(vid))).thenReturn(idInfo);

		Mockito.when(staticPinRepository.findById(vid)).thenReturn(entity);
		Mockito.when(staticPinHistoryRepo.save(staticPinHistory)).thenReturn(staticPinHistory);
		Mockito.when(staticPinRepository.update(entity.get())).thenReturn(stat);
		pinFacadeImpl.storeSpin(staticPinRequestDTO);
	}


		private Map<String, Object> repoDetails(String uin) {
			Map<String, Object> map = new HashMap<>();
			map.put("uin", "284169042058");
			return map;
		}
	}
	
	

