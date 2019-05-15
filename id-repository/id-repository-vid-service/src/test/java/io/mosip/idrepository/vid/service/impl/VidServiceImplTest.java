package io.mosip.idrepository.vid.service.impl;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.idrepository.core.builder.RestRequestBuilder;
import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.RestServicesConstants;
import io.mosip.idrepository.core.dto.IdResponseDTO;
import io.mosip.idrepository.core.dto.ResponseDTO;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.idrepository.vid.dto.RequestDTO;
import io.mosip.idrepository.vid.dto.VidPolicy;
import io.mosip.idrepository.vid.dto.VidRequestDTO;
import io.mosip.idrepository.vid.dto.VidResponseDTO;
import io.mosip.idrepository.vid.entity.Vid;
import io.mosip.idrepository.vid.provider.VidPolicyProvider;
import io.mosip.idrepository.vid.repository.VidRepo;
import io.mosip.kernel.core.idgenerator.spi.VidGenerator;
import io.mosip.kernel.core.util.DateUtils;

/**
 * 
 * @author Prem Kumar
 *
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles("test")
@ConfigurationProperties("mosip.idrepo.vid")
public class VidServiceImplTest {

	@InjectMocks
	private VidServiceImpl impl;

	@Mock
	private VidRepo vidRepo;
	
	@Mock
	private VidPolicyProvider vidPolicyProvider;
	
	@Mock
	private VidServiceImpl vidServiceImpl;
	
	@Mock
	private RestRequestBuilder restBuilder;

	@Mock
	private RestHelper restHelper;
	
	@Mock
	private WebClient webClient;
	
	@Mock
	private VidGenerator<String> vidGenerator;
	
	/** The security manager. */
	@Mock
	private IdRepoSecurityManager securityManager;
	
	/** The mapper. */
	@Mock
	private ObjectMapper mapper;
	@Autowired
	Environment environment;
	
	private Map<String, String> id;

	public Map<String, String> getId() {
		return id;
	}

	public void setId(Map<String, String> id) {
		this.id = id;
	}

	@Before
	public void before() {
		ReflectionTestUtils.setField(impl, "env", environment);
		ReflectionTestUtils.setField(impl, "vidRepo", vidRepo);
		ReflectionTestUtils.setField(impl, "policyProvider", vidPolicyProvider);
		ReflectionTestUtils.setField(impl, "restHelper", restHelper);
		ReflectionTestUtils.setField(impl, "securityManager", securityManager);
		ReflectionTestUtils.setField(restHelper, "webClient", webClient);
		ReflectionTestUtils.setField(restHelper, "mapper", mapper);
		ReflectionTestUtils.setField(impl, "restBuilder", restBuilder);
		ReflectionTestUtils.setField(impl, "vidGenerator", vidGenerator);
		ReflectionTestUtils.setField(restBuilder, "env", environment);
		ReflectionTestUtils.setField(impl, "id", id);
	}

	@Test
	public void testRetrieveUinByVid() throws IdRepoAppException {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
				.toLocalDateTime().plusDays(1);
		Vid vid = new Vid("18b67aa3-a25a-5cec-94c2-90644bf5b05b", "2015642902372691", "3920450236", "3920450236",
				"perpetual", currentTime, currentTime, "ACTIVE", "IdRepo", currentTime, "IdRepo", currentTime, false,
				currentTime);
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(vid);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
			impl.retrieveUinByVid("12345678");
	}
	
	@Test
	public void testRetrieveUinByVid_Expired() {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
				.toLocalDateTime();
		Vid vid = new Vid("18b67aa3-a25a-5cec-94c2-90644bf5b05b", "2015642902372691", "3920450236", "3920450236",
				"perpetual", currentTime, currentTime, "ACTIVATED", "IdRepo", currentTime, "IdRepo", currentTime, false,
				currentTime);
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(vid);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
			try {
				impl.retrieveUinByVid("12345678");
			} catch (IdRepoAppException e) {
				assertEquals("IDR-VID-002 --> Expired VID", e.getMessage());
			}
	}
	
	@Test
	public void testRetrieveUinByVid_Blocked() {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
				.toLocalDateTime().plusDays(1);
		Vid vid = new Vid("18b67aa3-a25a-5cec-94c2-90644bf5b05b", "2015642902372691", "3920450236", "3920450236",
				"perpetual", currentTime, currentTime, "Blocked", "IdRepo", currentTime, "IdRepo", currentTime, false,
				currentTime);
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(vid);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
			try {
				impl.retrieveUinByVid("12345678");
			} catch (IdRepoAppException e) {
				assertEquals("IDR-VID-002 --> Blocked VID", e.getMessage());
			}
	}
	
	@Test
	public void testRetrieveUinByVid_Invalid_NoRecordsFound() {
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(null);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
			try {
				impl.retrieveUinByVid("12345678");
			} catch (IdRepoAppException e) {
				assertEquals("IDR-VID-006 --> No Record(s) found", e.getMessage());
			}
	}
	
	@Test
	public void testUpdateVid_valid() throws IdRepoAppException {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
				.toLocalDateTime().plusDays(1);
		Vid vid = new Vid("18b67aa3-a25a-5cec-94c2-90644bf5b05b", "2015642902372691", "3920450236", "3920450236",
				"perpetual", currentTime, currentTime, "ACTIVE", "IdRepo", currentTime, "IdRepo", currentTime, false,
				currentTime);
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(vid);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
		VidPolicy policy=new VidPolicy();
		policy.setAllowedInstances(1);
		policy.setAllowedTransactions(null);
		policy.setAutoRestoreAllowed(true);
		policy.setRestoreOnAction("REVOKE");
		policy.setValidForInMinutes(null);
		Mockito.when(vidPolicyProvider.getPolicy(Mockito.anyString())).thenReturn(policy);
		VidRequestDTO req=new VidRequestDTO();
		req.setId("mosip.vid.update");
		RequestDTO request=new RequestDTO();
		request.setVidStatus("ACTIVE");
		req.setRequest(request);
		req.setVersion("v1");
		req.setRequesttime(DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue()))).toLocalDateTime());
		req.setRequest(request);
		
		impl.updateVid("12345678", req);
	}
	
	@Test
	public void testUpdateVid_valid_REVOKE() throws IdRepoAppException {
		LocalDateTime currentTime = DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue())))
				.toLocalDateTime().plusDays(1);
		Vid vid = new Vid("18b67aa3-a25a-5cec-94c2-90644bf5b05b", "2015642902372691", "3920450236", "3920450236",
				"perpetual", currentTime, currentTime, "ACTIVE", "IdRepo", currentTime, "IdRepo", currentTime, false,
				currentTime);
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(vid);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
		VidPolicy policy=new VidPolicy();
		policy.setAllowedInstances(1);
		policy.setAllowedTransactions(null);
		policy.setAutoRestoreAllowed(true);
		policy.setRestoreOnAction("REVOKE");
		policy.setValidForInMinutes(null);
		Mockito.when(vidPolicyProvider.getPolicy(Mockito.anyString())).thenReturn(policy);
		VidRequestDTO req=new VidRequestDTO();
		RestRequestDTO restRequestDTO=new RestRequestDTO();
		IdResponseDTO idResponse=new IdResponseDTO();
		ResponseDTO resDTO=new ResponseDTO();
		resDTO.setStatus("ACTIVATED");
		idResponse.setResponse(resDTO);
		Mockito.when(vidServiceImpl.createVid(req)).thenReturn(new VidResponseDTO());
		Mockito.when(restBuilder.buildRequest(RestServicesConstants.IDREPO_IDENTITY_SERVICE, null,
				IdResponseDTO.class)).thenReturn(restRequestDTO);
		Mockito.when(restHelper.requestSync(restRequestDTO)).thenReturn(idResponse);
		Mockito.when(vidRepo.save(Mockito.any())).thenReturn(vid);
		Mockito.when(securityManager.hash(Mockito.any())).thenReturn("6B764AE0FF065490AEFAF796A039D6B4F251101A5F13DA93146B9DEB11087AFC");
		
		req.setId("mosip.vid.update");
		RequestDTO request=new RequestDTO();
		request.setVidStatus("REVOKE");
		req.setRequest(request);
		req.setVersion("v1");
		req.setRequesttime(DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue()))).toLocalDateTime());
		req.setRequest(request);
		
		impl.updateVid("12345678", req);
	}
	@Test
	public void testUpdateVid_Invalid(){
		Mockito.when(vidRepo.findByVid(Mockito.anyString())).thenReturn(null);
		Mockito.when(vidRepo.retrieveUinByVid(Mockito.anyString())).thenReturn("1234567");
		
		VidRequestDTO req=new VidRequestDTO();
		req.setId("mosip.vid.update");
		RequestDTO request=new RequestDTO();
		request.setVidStatus("ACTIVE");
		req.setRequest(request);
		req.setVersion("v1");
		req.setRequesttime(DateUtils.getUTCCurrentDateTime()
				.atZone(ZoneId.of(environment.getProperty(IdRepoConstants.DATETIME_TIMEZONE.getValue()))).toLocalDateTime());
		req.setRequest(request);
		
		try {
			impl.updateVid("12345678", req);
		} catch (IdRepoAppException e) {
		assertEquals("IDR-VID-006 --> No Record(s) found",e.getMessage());
		}
	}
}
