package io.kernel.core.idrepo.service;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.kernel.core.idrepo.dao.impl.IdRepoDaoImpl;
import io.kernel.core.idrepo.dto.IdRequestDTO;
import io.kernel.core.idrepo.entity.Uin;
import io.kernel.core.idrepo.entity.UinDetail;
import io.kernel.core.idrepo.exception.IdRepoAppException;
import io.kernel.core.idrepo.service.impl.IdRepoServiceImpl;

/**
 * The Class IdRepoServiceTest.
 *
 * @author Manoj SP
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles("test")
public class IdRepoServiceTest {

	/** The service. */
	@InjectMocks
	IdRepoServiceImpl service;

	/** The mapper. */
	@Autowired
	private ObjectMapper mapper;

	/** The env. */
	@Autowired
	private Environment env;

	/** The rest template. */
	@Mock
	private RestTemplate restTemplate;

	/** The id. */
	private Map<String, String> id = new HashMap<>();

	/** The id repo. */
	@Mock
	private IdRepoDaoImpl idRepo;

	/** The uin. */
	Uin uin = new Uin();

	/** The request. */
	IdRequestDTO request = new IdRequestDTO();

	/**
	 * Setup.
	 *
	 * @throws JsonProcessingException the json processing exception
	 */
	@Before
	public void setup() throws JsonProcessingException {
		ReflectionTestUtils.setField(service, "mapper", mapper);
		ReflectionTestUtils.setField(service, "env", env);
		ReflectionTestUtils.setField(service, "id", id);
		id.put("create", "mosip.id.create");
		id.put("update", "mosip.id.update");
		id.put("read", "mosip.id.read");
		request.setRegistrationId("registrationId");
		request.setRequest(null);
		uin.setUin("1234");
		uin.setUinRefId("uinRefId");
		UinDetail uinDetail = new UinDetail();
		uinDetail.setUinData(mapper.writeValueAsBytes(request));
		uin.setUinDetail(uinDetail);
		uin.setStatusCode(env.getProperty("mosip.idrepo.status.registered"));
	}

	/**
	 * Test add identity.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test
	public void testAddIdentity() throws IdRepoAppException {
		when(idRepo.addIdentity(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(uin);
		Mockito.when(restTemplate.exchange(env.getProperty("mosip.uingen.url"), HttpMethod.GET, null, String.class))
				.thenReturn(new ResponseEntity<String>("1234", HttpStatus.OK));
	}

	/**
	 * Test add identity exception.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = IdRepoAppException.class)
	public void testAddIdentityException() throws IdRepoAppException {
		when(idRepo.addIdentity(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new IdRepoAppException());
		Mockito.when(restTemplate.exchange(env.getProperty("mosip.uingen.url"), HttpMethod.GET, null, String.class))
				.thenReturn(new ResponseEntity<String>("1234", HttpStatus.OK));
		service.addIdentity(request);
	}

	/**
	 * Test retrieve identity.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test
	public void testRetrieveIdentity() throws IdRepoAppException {
		when(idRepo.retrieveIdentity(Mockito.anyString())).thenReturn(uin);
		Mockito.when(restTemplate.exchange(env.getProperty("mosip.uingen.url"), HttpMethod.GET, null, String.class))
				.thenReturn(new ResponseEntity<String>("1234", HttpStatus.OK));
		service.retrieveIdentity("1234");
	}
}
