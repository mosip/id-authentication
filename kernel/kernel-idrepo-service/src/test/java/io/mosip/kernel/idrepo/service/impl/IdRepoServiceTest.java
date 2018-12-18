package io.mosip.kernel.idrepo.service.impl;

import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

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
import org.springframework.dao.DataAccessResourceFailureException;
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
import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;
import io.mosip.kernel.crypto.jce.impl.EncryptorImpl;
import io.mosip.kernel.idrepo.dto.IdRequestDTO;
import io.mosip.kernel.idrepo.entity.Uin;
import io.mosip.kernel.idrepo.entity.UinDetail;
import io.mosip.kernel.idrepo.repository.UinDetailHistoryRepo;
import io.mosip.kernel.idrepo.repository.UinDetailRepo;
import io.mosip.kernel.idrepo.repository.UinHistoryRepo;
import io.mosip.kernel.idrepo.repository.UinRepo;
import io.mosip.kernel.idrepo.service.impl.DefaultShardResolver;
import io.mosip.kernel.idrepo.service.impl.IdRepoServiceImpl;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;

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

	/** The uin detail repo. */
	@Mock
	private UinDetailRepo uinDetailRepo;

	/** The uin history repo. */
	@Mock
	private UinHistoryRepo uinHistoryRepo;

	/** The uin detail history repo. */
	@Mock
	private UinDetailHistoryRepo uinDetailHistoryRepo;

	/** The key generator. */
	@Mock
	private KeyGenerator keyGenerator;

	/** The encryptor. */
	@Mock
	private EncryptorImpl encryptor;

	/** The decryptor. */
	@Mock
	private DecryptorImpl decryptor;

	/** The id. */
	private Map<String, String> id;

	private List<String> status;

	/** The uin. */
	@InjectMocks
	Uin uin;

	@InjectMocks
	UinDetail uinDetail;

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
		uinDetail.setUinData(mapper.writeValueAsBytes(request));
		uin.setUinDetail(uinDetail);
		uin.setStatusCode(env.getProperty("mosip.kernel.idrepo.status.registered"));

		byte[] sessionKey = Files.toByteArray(ResourceUtils.getFile("classpath:sessionKey"));
		when(encryptor.symmetricEncrypt(Mockito.any(), Mockito.any())).thenReturn(sessionKey);
		when(encryptor.asymmetricPublicEncrypt(Mockito.any(), Mockito.any())).thenReturn(sessionKey);
		when(keyGenerator.getSymmetricKey()).thenReturn(new SecretKeySpec(sessionKey, 0, sessionKey.length, "AES"));

		when(decryptor.asymmetricPrivateDecrypt(Mockito.any(), Mockito.any())).thenReturn(sessionKey);
		when(decryptor.symmetricDecrypt(Mockito.any(), Mockito.any())).thenReturn("{\"uin\":\"1234\"}".getBytes());
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
	public void testRetrieveIdentity()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		UinDetail uinDetailObj = new UinDetail();
		uinDetailObj.setUinData(
				"QoRqW6uabMvVGMZCY+ywZ2zmf8Fvm2ONzuBCe2SQXA8=|HulYCbo+K4NgMpiKt2qcYMfg3mRBsqkebZm4bUrGPpvJ28fm5Xw3HEblAKAYkumG4PuFZFa88XL4YD8giVsTx/1au6uIvD+TI5VA4XG1g05xrVIxnq1T/IjFkhBhHI5HcvP4tMiv1BrZ2/pjvc7lzh90p6gqnR2L3tnKckj9BY5k9S9yyr9Gjn4SgXXHTpRzgpm9XkTd32P5HsGOceMLtHIg0ESQwxMYgzBCk8MZ45cYswlRtCVLASV+ZaFk6cEePZgHUHmAJYsv0X3uQQzzSNI7GPHoMitWHZbzeSSaCIRE+kvMjKK/fHz8DHJNRdBcAUWEIOvvsv496TrAE/UcGw==|vKOOGGaHQYSt6fDnq5aHfR1rIWNQfBBLHzq2cl5XvMpszG/S2VVFlneCfcbDOI0IkYAPUJ37ex3tOdLwDFIwrG6sstMBrxmEYmomyuhCWTOGU6/GDaTdj3OHPI8VwblG"
						.getBytes());
		uinObj.setUinDetail(uinDetailObj);
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.getStatusByUin(Mockito.any())).thenReturn("REGISTERED");
		ObjectNode response = mapper.readValue("{\"uin\":\"1234\"}", ObjectNode.class);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		Mockito.when(restTemplate.exchange(env.getProperty("mosip.kernel.uingen.url"), HttpMethod.GET, null,
				ObjectNode.class)).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
		service.retrieveIdentity("1234");
	}

	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityNullUinOject()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		service.retrieveIdentity(null);
	}

	@Test
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
		UinDetail uinDetailObj = new UinDetail();
		uinDetailObj.setUinData(
				"rgAADOjjov89sjVwvI8Gc4ngK9lQgPxMpNDe+LXb5qI=|P6NGM4tYz1Zdy+ZC/ikKYNp1csxrarX/dCEta1HCHWE=|P6NGM4tYz1Zdy+ZC/ikKYNp1csxrarX/dCEta1HCHWE="
						.getBytes());
		uinObj.setUinDetail(uinDetailObj);
		when(decryptor.symmetricDecrypt(Mockito.any(), Mockito.any())).thenReturn(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes());
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.getStatusByUin(Mockito.any())).thenReturn("REGISTERED");
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		service.updateIdentity(request);
	}

	@Test
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
		UinDetail uinDetailObj = new UinDetail();
		uinDetailObj.setUinData(
				"rgAADOjjov89sjVwvI8Gc4ngK9lQgPxMpNDe+LXb5qI=|P6NGM4tYz1Zdy+ZC/ikKYNp1csxrarX/dCEta1HCHWE=|P6NGM4tYz1Zdy+ZC/ikKYNp1csxrarX/dCEta1HCHWE="
						.getBytes());
		uinObj.setUinDetail(uinDetailObj);
		when(decryptor.symmetricDecrypt(Mockito.any(), Mockito.any())).thenReturn(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes());
		when(uinRepo.save(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.getStatusByUin(Mockito.any())).thenReturn("REGISTERED");
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		service.updateIdentity(request);
	}

	@Test
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
		UinDetail uinDetailObj = new UinDetail();
		uinDetailObj.setUinData(
				"rgAADOjjov89sjVwvI8Gc4ngK9lQgPxMpNDe+LXb5qI=|P6NGM4tYz1Zdy+ZC/ikKYNp1csxrarX/dCEta1HCHWE=|P6NGM4tYz1Zdy+ZC/ikKYNp1csxrarX/dCEta1HCHWE="
						.getBytes());
		uinObj.setUinDetail(uinDetailObj);
		when(decryptor.symmetricDecrypt(Mockito.any(), Mockito.any())).thenReturn(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes());
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.getStatusByUin(Mockito.any())).thenReturn("REGISTERED");
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		service.updateIdentity(request);
	}
}
