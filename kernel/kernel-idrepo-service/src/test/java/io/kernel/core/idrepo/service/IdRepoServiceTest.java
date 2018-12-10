package io.kernel.core.idrepo.service;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
@ConfigurationProperties("mosip.idrepo")
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
	private Map<String, String> id;

	/** The id repo. */
	@Mock
	private IdRepoDaoImpl idRepo;

	/** The uin. */
	Uin uin = new Uin();

	/** The request. */
	IdRequestDTO request = new IdRequestDTO();

	public Map<String, String> getId() {
		return id;
	}

	public void setId(Map<String, String> id) {
		this.id = id;
	}

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
		ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
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
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@Test
	public void testAddIdentity() throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		ObjectNode response = mapper.readValue("{\"uin\":\"1234\"}", ObjectNode.class);
		when(idRepo.addIdentity(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(uin);
		Mockito.when(restTemplate.exchange(env.getProperty("mosip.uingen.url"), HttpMethod.GET, null, ObjectNode.class))
				.thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
		service.addIdentity(request);
	}

	/**
	 * Test add identity exception.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@Test(expected = IdRepoAppException.class)
	public void testAddIdentityException() throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		ObjectNode response = mapper.readValue("{\"uin\":\"1234\"}", ObjectNode.class);
		when(idRepo.addIdentity(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new IdRepoAppException());
		Mockito.when(restTemplate.exchange(env.getProperty("mosip.uingen.url"), HttpMethod.GET, null, ObjectNode.class))
				.thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
		service.addIdentity(request);
	}

	/**
	 * Test retrieve identity.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@Test
	public void testRetrieveIdentity() throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		ObjectNode response = mapper.readValue("{\"uin\":\"1234\"}", ObjectNode.class);
		when(idRepo.retrieveIdentity(Mockito.anyString())).thenReturn(uin);
		Mockito.when(restTemplate.exchange(env.getProperty("mosip.uingen.url"), HttpMethod.GET, null, ObjectNode.class))
				.thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
		service.retrieveIdentity("1234");
	}
	
	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityNullUinOject() throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		ObjectNode response = mapper.readValue("{\"uin\":\"1234\"}", ObjectNode.class);
		when(idRepo.retrieveIdentity(Mockito.anyString())).thenReturn(null);
		Mockito.when(restTemplate.exchange(env.getProperty("mosip.uingen.url"), HttpMethod.GET, null, ObjectNode.class))
				.thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
		service.retrieveIdentity("1234");
	}
	
	public void testUpdateIdentity() {
		
	}
}
