package io.mosip.kernel.idrepo.service.impl;

import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.io.Files;

import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.idrepo.dto.IdRequestDTO;
import io.mosip.kernel.idrepo.entity.Uin;
import io.mosip.kernel.idrepo.repository.UinHistoryRepo;
import io.mosip.kernel.idrepo.repository.UinRepo;

/**
 * The Class IdRepoServiceTest.
 *
 * @author Manoj SP
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles("test")
@ConfigurationProperties("mosip.kernel.idrepo")
@Ignore
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

	@Mock
	private DefaultShardResolver shardResolver;

	/** The uin repo. */
	@Mock
	private UinRepo uinRepo;


	/** The uin history repo. */
	@Mock
	private UinHistoryRepo uinHistoryRepo;

	/** The id. */
	private Map<String, String> id;

	private List<String> status;

	/** The uin. */
	@InjectMocks
	Uin uin;

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
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@Before
	public void setup() throws FileNotFoundException, IOException {
		ReflectionTestUtils.setField(service, "mapper", mapper);
		ReflectionTestUtils.setField(service, "env", env);
		ReflectionTestUtils.setField(service, "id", id);
		ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
		request.setRegistrationId("registrationId");
		request.setRequest(null);
		uin.setUin("1234");
		uin.setUinRefId("uinRefId");
		uin.setUinData(mapper.writeValueAsBytes(request));
		uin.setStatusCode(env.getProperty("mosip.kernel.idrepo.status.registered"));

		byte[] sessionKey = Files.toByteArray(ResourceUtils.getFile("classpath:sessionKey"));
	}

	/**
	 * Test add identity.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@Test
	@Ignore
	public void testAddIdentity() throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		ObjectNode obj = mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				ObjectNode.class);
		request.setRequest(obj);
		ObjectNode response = mapper.readValue("{\"uin\":\"1234\"}", ObjectNode.class);
		when(uinRepo.save(Mockito.any())).thenReturn(uinObj);
		Mockito.when(restTemplate.exchange(env.getProperty("mosip.kernel.uingen.url"), HttpMethod.GET, null,
				ObjectNode.class)).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
		service.addIdentity(request);
	}

	@Test(expected = IdRepoAppException.class)
	@Ignore
	public void testAddIdentityRecordExists()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		ObjectNode obj = mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				ObjectNode.class);
		request.setRequest(obj);
		ObjectNode response = mapper.readValue("{\"uin\":\"1234\"}", ObjectNode.class);
		when(uinRepo.existsById(Mockito.anyString())).thenReturn(true);
		when(uinRepo.save(Mockito.any())).thenReturn(uinObj);
		Mockito.when(restTemplate.exchange(env.getProperty("mosip.kernel.uingen.url"), HttpMethod.GET, null,
				ObjectNode.class)).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
		service.addIdentity(request);
	}

	@Test(expected = IdRepoAppException.class)
	@Ignore
	public void testAddIdentityDataAccessException()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		ObjectNode obj = mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				ObjectNode.class);
		request.setRequest(obj);
		ObjectNode response = mapper.readValue("{\"uin\":\"1234\"}", ObjectNode.class);
		when(uinRepo.existsById(Mockito.anyString())).thenReturn(true);
		when(uinRepo.save(Mockito.any())).thenThrow(new RecoverableDataAccessException(null));
		Mockito.when(restTemplate.exchange(env.getProperty("mosip.kernel.uingen.url"), HttpMethod.GET, null,
				ObjectNode.class)).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
		service.addIdentity(request);
	}

	@Test(expected = IdRepoAppException.class)
	@Ignore
	public void testUpdateuinStatusDataAccessException()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		when(uinRepo.save(Mockito.any())).thenThrow(new RecoverableDataAccessException(null));
		service.updateUinStatus(new Uin(), "status");
	}

	@Test(expected = IdRepoAppException.class)
	@Ignore
	public void testUpdateUinidentityInfoDataAccessException()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
	}

	/**
	 * Test add identity exception.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@Test(expected = IdRepoAppException.class)
	@Ignore
	public void testAddIdentityException()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		when(uinRepo.save(Mockito.any())).thenThrow(new DataAccessResourceFailureException(null));
		ObjectNode response = mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				ObjectNode.class);
		Mockito.when(restTemplate.exchange(env.getProperty("mosip.kernel.uingen.url"), HttpMethod.GET, null,
				ObjectNode.class)).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
		service.addIdentity(request);
	}

	/**
	 * Test retrieve identity.
	 *
	 * @throws IdRepoAppException
	 *             the id repo app exception
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@Test
	@Ignore
	public void testRetrieveIdentity()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		uinObj.setUinData(
				"QoRqW6uabMvVGMZCY+ywZ2zmf8Fvm2ONzuBCe2SQXA8=|HulYCbo+K4NgMpiKt2qcYMfg3mRBsqkebZm4bUrGPpvJ28fm5Xw3HEblAKAYkumG4PuFZFa88XL4YD8giVsTx/1au6uIvD+TI5VA4XG1g05xrVIxnq1T/IjFkhBhHI5HcvP4tMiv1BrZ2/pjvc7lzh90p6gqnR2L3tnKckj9BY5k9S9yyr9Gjn4SgXXHTpRzgpm9XkTd32P5HsGOceMLtHIg0ESQwxMYgzBCk8MZ45cYswlRtCVLASV+ZaFk6cEePZgHUHmAJYsv0X3uQQzzSNI7GPHoMitWHZbzeSSaCIRE+kvMjKK/fHz8DHJNRdBcAUWEIOvvsv496TrAE/UcGw==|vKOOGGaHQYSt6fDnq5aHfR1rIWNQfBBLHzq2cl5XvMpszG/S2VVFlneCfcbDOI0IkYAPUJ37ex3tOdLwDFIwrG6sstMBrxmEYmomyuhCWTOGU6/GDaTdj3OHPI8VwblG"
						.getBytes());
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.getStatusByUin(Mockito.any())).thenReturn("REGISTERED");
		ObjectNode response = mapper.readValue("{\"uin\":\"1234\"}", ObjectNode.class);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		Mockito.when(restTemplate.exchange(env.getProperty("mosip.kernel.uingen.url"), HttpMethod.GET, null,
				ObjectNode.class)).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
	}

//	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityNullUinOject()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
	}

	@Test
	@Ignore
	public void testUpdateIdentity() throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		request.setStatus("REGISTERED");
		request.setRequest(mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class));
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		uinObj.setStatusCode("REGISTERED");
		uinObj.setUinData(
				"rgAADOjjov89sjVwvI8Gc4ngK9lQgPxMpNDe+LXb5qI=|P6NGM4tYz1Zdy+ZC/ikKYNp1csxrarX/dCEta1HCHWE=|P6NGM4tYz1Zdy+ZC/ikKYNp1csxrarX/dCEta1HCHWE="
						.getBytes());
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.getStatusByUin(Mockito.any())).thenReturn("REGISTERED");
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
//		service.updateIdentity(request);
	}

	@Test
	@Ignore
	public void testUpdateIdentityStatus()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		request.setStatus("BLOCKED");
		request.setRequest(mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class));
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		uinObj.setStatusCode("REGISTERED");
		uinObj.setUinData(
				"rgAADOjjov89sjVwvI8Gc4ngK9lQgPxMpNDe+LXb5qI=|P6NGM4tYz1Zdy+ZC/ikKYNp1csxrarX/dCEta1HCHWE=|P6NGM4tYz1Zdy+ZC/ikKYNp1csxrarX/dCEta1HCHWE="
						.getBytes());
		when(uinRepo.save(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.getStatusByUin(Mockito.any())).thenReturn("REGISTERED");
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
	}

	@Test
	@Ignore
	public void testUpdateIdentityWithDiff()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		request.setStatus("REGISTERED");
		request.setRequest(mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Mano\",\"label\":\"string\"},{\"language\":\"FR\",\"value\":\"Mano\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class));
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		uinObj.setStatusCode("REGISTERED");
		uinObj.setUinData(
				"rgAADOjjov89sjVwvI8Gc4ngK9lQgPxMpNDe+LXb5qI=|P6NGM4tYz1Zdy+ZC/ikKYNp1csxrarX/dCEta1HCHWE=|P6NGM4tYz1Zdy+ZC/ikKYNp1csxrarX/dCEta1HCHWE="
						.getBytes());
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.getStatusByUin(Mockito.any())).thenReturn("REGISTERED");
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
	}

	@Test
	@Ignore
	public void testUpdateIdentityInvalidRequest()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		request.setStatus("REGISTERED");
		request.setRequest(mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Mano\",\"label\":\"string\"},{\"language\":\"FR\",\"value\":\"Mano\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class));
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		uinObj.setStatusCode("REGISTERED");
		uinObj.setUinData(
				"rgAADOjjov89sjVwvI8Gc4ngK9lQgPxMpNDe+LXb5qI=|P6NGM4tYz1Zdy+ZC/ikKYNp1csxrarX/dCEta1HCHWE=|P6NGM4tYz1Zdy+ZC/ikKYNp1csxrarX/dCEta1HCHWE="
						.getBytes());
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.getStatusByUin(Mockito.any())).thenReturn("REGISTERED");
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
	}

	@Test(expected = IdRepoAppException.class)
	@Ignore
	public void testconvertToMap() throws Throwable {
		try {
			ReflectionTestUtils.invokeMethod(service, "convertToMap",
					mapper.readValue("{\"uin\":\"1234\"}".getBytes(), Object.class));
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	/**
	 * @throws Throwable 
	 * 
	 */
	@Test(expected = IdRepoAppException.class)
	@Ignore
	public void testvalidateUIN() throws Throwable {
		try {
			when(uinRepo.existsByUin(Mockito.anyString())).thenReturn(true);
			when(uinRepo.getStatusByUin(Mockito.anyString())).thenReturn("wrong");
			ReflectionTestUtils.invokeMethod(service, "validateUIN", "1234");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}
}
