package io.mosip.kernel.idrepo.service.impl;

import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.hibernate.exception.JDBCConnectionException;
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
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import io.mosip.kernel.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.cbeffutil.entity.BIR;
import io.mosip.kernel.cbeffutil.entity.BIRInfo;
import io.mosip.kernel.cbeffutil.entity.BIRVersion;
import io.mosip.kernel.cbeffutil.entity.SBInfo;
import io.mosip.kernel.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SBInfoType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.cbeffutil.service.impl.CbeffImpl;
import io.mosip.kernel.core.idrepo.constant.IdRepoErrorConstants;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppException;
import io.mosip.kernel.core.idrepo.exception.IdRepoAppUncheckedException;
import io.mosip.kernel.idrepo.dfsadapter.impl.AmazonS3DFSProvider;
import io.mosip.kernel.idrepo.dto.IdRequestDTO;
import io.mosip.kernel.idrepo.dto.RequestDTO;
import io.mosip.kernel.idrepo.entity.Uin;
import io.mosip.kernel.idrepo.entity.UinBiometric;
import io.mosip.kernel.idrepo.entity.UinDocument;
import io.mosip.kernel.idrepo.helper.AuditHelper;
import io.mosip.kernel.idrepo.provider.impl.FingerprintProvider;
import io.mosip.kernel.idrepo.repository.UinBiometricHistoryRepo;
import io.mosip.kernel.idrepo.repository.UinDocumentHistoryRepo;
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
public class IdRepoServiceTest {

	@Mock
	FingerprintProvider fpProvider;

	@Mock
	CbeffImpl cbeffUtil;

	@Mock
	AuditHelper auditHelper;

	@Mock
	AmazonS3DFSProvider connection;

	@Mock
	AmazonS3 conn;

	@Mock
	S3Object s3Obj;

	/** The service. */
	@InjectMocks
	IdRepoServiceImpl service;

	@Mock
	private UinBiometricHistoryRepo uinBioHRepo;

	@Mock
	private UinDocumentHistoryRepo uinDocHRepo;

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

	BIR rFinger = new BIR.BIRBuilder().withBdb("3".getBytes())
			.withVersion(new BIRVersion.BIRVersionBuilder()
                    .withMajor(1)
                    .withMinor(1)
                    .build())
			.withCbeffversion(new BIRVersion.BIRVersionBuilder()
                    .withMajor(1)
                    .withMinor(1)
                    .build())
			.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
			.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormatOwner(new Long(257)).withFormatType(new Long(7))
					.withQuality(95).withType(Arrays.asList(SingleType.FINGER))
					.withSubtype(Arrays.asList(SingleAnySubtypeType.RIGHT.value(),
							SingleAnySubtypeType.INDEX_FINGER.value()))
					.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW).withCreationDate(new Date())
					.build())
			.withSbInfo(new SBInfo.SBInfoBuilder()
                    .setFormatOwner(257l)
                    .setFormatType(7l)
                    .build())
			.build();

	/** The uin repo. */
	@Mock
	private UinRepo uinRepo;

	/** The uin history repo. */
	@Mock
	private UinHistoryRepo uinHistoryRepo;

	/** The id. */
	private Map<String, String> id;

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
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setup() throws FileNotFoundException, IOException {
		when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(ObjectNode.class), Mockito.any(Class.class)))
				.thenReturn(mapper.readValue("{\"data\":\"1234\"}".getBytes(), ObjectNode.class));
		ReflectionTestUtils.setField(service, "mapper", mapper);
		ReflectionTestUtils.setField(service, "env", env);
		ReflectionTestUtils.setField(service, "id", id);
		ReflectionTestUtils.setField(service, "allowedBioTypes", Collections.singletonList("individualBiometrics"));
		request.setRegistrationId("registrationId");
		request.setRequest(null);
		uin.setUin("1234");
		uin.setUinRefId("uinRefId");
		uin.setUinData(mapper.writeValueAsBytes(request));
		uin.setStatusCode(env.getProperty("mosip.kernel.idrepo.status.registered"));
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
		RequestDTO req = new RequestDTO();
		req.setIdentity(obj);
		request.setRequest(req);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(false);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		service.addIdentity(request, "1234");
	}

	@Test
	public void testAddIdentityWithDemoDocuments()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {

	}
	
	@Test(expected = IdRepoAppException.class)
	public void testAddDocumentsDataAccessException()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
		when(connection.getConnection()).thenReturn(conn);
		when(conn.doesBucketExistV2(Mockito.any())).thenReturn(true);
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		RequestDTO req = mapper.readValue(
				"{\"identity\":{\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"proofOfDateOfBirth\",\"value\":\"dGVzdA\"}]}"
						.getBytes(),
				RequestDTO.class);
		request.setRequest(req);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(false);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinDocHRepo.save(Mockito.any())).thenThrow(new DataAccessResourceFailureException(null));
		service.addIdentity(request, "1234");
	}
	
	@Test(expected = IdRepoAppException.class)
	public void testAddDocumentsJDBCConnectionException()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
		when(connection.getConnection()).thenReturn(conn);
		when(conn.doesBucketExistV2(Mockito.any())).thenReturn(true);
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		RequestDTO req = mapper.readValue(
				"{\"identity\":{\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"proofOfDateOfBirth\",\"value\":\"dGVzdA\"}]}"
						.getBytes(),
				RequestDTO.class);
		request.setRequest(req);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(false);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinDocHRepo.save(Mockito.any())).thenThrow(new JDBCConnectionException(null, null));
		service.addIdentity(request, "1234");
	}

	@Test(expected = IdRepoAppException.class)
	public void testAddIdentityDocumentStoreFailed()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
		when(connection.storeFile(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new IdRepoAppException(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR));
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		RequestDTO req = mapper.readValue(
				"{\"identity\":{\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"proofOfDateOfBirth\",\"value\":\"dGVzdA\"}]}"
						.getBytes(),
				RequestDTO.class);
		request.setRequest(req);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(false);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		service.addIdentity(request, "1234");
	}

	@Test
	public void testAddIdentityWithBioDocuments() throws Exception {
		when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
		when(connection.storeFile(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
		when(cbeffUtil.validateXML(Mockito.any(), Mockito.any())).thenReturn(true);
		when(cbeffUtil.updateXML(Mockito.any(), Mockito.any())).thenReturn("data".getBytes());
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		RequestDTO req = mapper.readValue(
				"{\"identity\":{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"individualBiometrics\",\"value\":\"dGVzdA\"}]}"
						.getBytes(),
				RequestDTO.class);
		request.setRequest(req);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(false);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		service.addIdentity(request, "1234");
	}

	@Test(expected = IdRepoAppException.class)
	public void testAddIdentityRecordExists()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		ObjectNode obj = mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				ObjectNode.class);
		RequestDTO req = new RequestDTO();
		req.setIdentity(obj);
		request.setRequest(req);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(true);
		service.addIdentity(request, "1234");
	}

	@Test(expected = IdRepoAppException.class)
	public void testAddIdentityDataAccessException()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		ObjectNode obj = mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				ObjectNode.class);
		RequestDTO req = new RequestDTO();
		req.setIdentity(obj);
		request.setRequest(req);
		when(uinRepo.save(Mockito.any())).thenThrow(new RecoverableDataAccessException(null));
		service.addIdentity(request, "1234");
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
		RequestDTO request2 = new RequestDTO();
		request2.setIdentity(mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class));
		request.setRequest(request2);
		service.addIdentity(request, "1234");
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
		uinObj.setUinData(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes());
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		service.retrieveIdentity("1234", null);
	}

	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityNoRecordExists()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		uinObj.setUinData(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes());
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(false);
		service.retrieveIdentity("1234", null);
	}

	@Test
	public void testRetrieveIdentityWithBioDocuments()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		when(connection.getFile(Mockito.any(), Mockito.any())).thenReturn("dGVzdA".getBytes());
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		UinBiometric biometrics = new UinBiometric();
		biometrics.setBiometricFileType("individualBiometrics");
		biometrics.setBiometricFileHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
		biometrics.setBioFileId("1234");
		biometrics.setBiometricFileName("name");
		uinObj.setBiometrics(Collections.singletonList(biometrics));
		uinObj.setUinData(
				"{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"}}"
						.getBytes());
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		service.retrieveIdentity("1234", "bio");
	}

	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityWithBioDocumentsFileRetrievalError()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		when(connection.getFile(Mockito.any(), Mockito.any()))
				.thenThrow(new IdRepoAppException(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR));
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		UinBiometric biometrics = new UinBiometric();
		biometrics.setBiometricFileType("individualBiometrics");
		biometrics.setBiometricFileHash("A6xnQhbz4Vx2HuGl4lXwZ5U2I8iziLRFnhP5eNfIRvQ");
		biometrics.setBioFileId("1234");
		biometrics.setBiometricFileName("name");
		uinObj.setBiometrics(Collections.singletonList(biometrics));
		uinObj.setUinData(
				"{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"}}"
						.getBytes());
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		service.retrieveIdentity("1234", "bio");
	}

	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityWithBioDocumentsHashFail()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		when(connection.getFile(Mockito.any(), Mockito.any())).thenReturn("data".getBytes());
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		UinBiometric biometrics = new UinBiometric();
		biometrics.setBiometricFileType("individualBiometrics");
		biometrics.setBiometricFileHash("A6xnQhbz4Vx2HuGl4lXwZ5U2I8iziLRFnhPeNfIRvQ");
		biometrics.setBioFileId("1234");
		biometrics.setBiometricFileName("name");
		uinObj.setBiometrics(Collections.singletonList(biometrics));
		uinObj.setUinData(
				"{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"}}"
						.getBytes());
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		service.retrieveIdentity("1234", "bio");
	}

	@Test
	public void testRetrieveIdentityWithDemoDocuments()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		when(connection.getFile(Mockito.any(), Mockito.any())).thenReturn("data".getBytes());
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		UinDocument document = new UinDocument();
		document.setDoccatCode("ProofOfIdentity");
		document.setDocHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
		document.setDocId("1234");
		document.setDocName("name");
		uinObj.setDocuments(Collections.singletonList(document));
		uinObj.setUinData(
				"{\"ProofOfIdentity\":{\"format\":\"pdf\",\"version\":1.0,\"value\":\"fileReferenceID\"}}".getBytes());
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		service.retrieveIdentity("1234", "demo");
	}

	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityWithDemoDocumentsHashFail()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		when(connection.getFile(Mockito.any(), Mockito.any())).thenReturn("data".getBytes());
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		UinDocument document = new UinDocument();
		document.setDoccatCode("ProofOfIdentity");
		document.setDocHash("A6xnQhbz4Vx2HuGl4lXwZ5U28iziLRFnhP5eNfIRvQ");
		document.setDocId("1234");
		document.setDocName("name");
		uinObj.setDocuments(Collections.singletonList(document));
		uinObj.setUinData(
				"{\"ProofOfIdentity\":{\"format\":\"pdf\",\"version\":1.0,\"value\":\"fileReferenceID\"}}".getBytes());
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		service.retrieveIdentity("1234", "demo");
	}

	@Test
	public void testRetrieveIdentityWithAllType()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		when(connection.getFile(Mockito.any(), Mockito.any())).thenReturn("dGVzdA".getBytes());
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		UinBiometric biometrics = new UinBiometric();
		biometrics.setBiometricFileType("individualBiometrics");
		biometrics.setBiometricFileHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
		biometrics.setBioFileId("1234");
		biometrics.setBiometricFileName("name");
		uinObj.setBiometrics(Collections.singletonList(biometrics));
		UinDocument document = new UinDocument();
		document.setDoccatCode("ProofOfIdentity");
		document.setDocHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
		document.setDocId("1234");
		document.setDocName("name");
		uinObj.setDocuments(Collections.singletonList(document));
		uinObj.setUinData(
				"{\"ProofOfIdentity\":{\"format\":\"pdf\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"}}"
						.getBytes());
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		service.retrieveIdentity("1234", "all");
	}

	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityWithUnknownType()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		when(connection.getConnection()).thenReturn(conn);
		when(conn.doesBucketExistV2(Mockito.any())).thenReturn(true);
		when(conn.doesObjectExist(Mockito.any(), Mockito.any())).thenReturn(true);
		when(conn.getObject(Mockito.any())).thenReturn(s3Obj);
		when(s3Obj.getObjectContent())
				.thenReturn(new S3ObjectInputStream(IOUtils.toInputStream("1234", Charset.defaultCharset()), null));
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		UinBiometric biometrics = new UinBiometric();
		biometrics.setBiometricFileType("individualBiometrics");
		biometrics.setBiometricFileHash("A6xnQhbz4Vx2HuGl4lXwZ5U2I8iziLRFnhP5eNfIRvQ");
		biometrics.setBioFileId("1234");
		biometrics.setBiometricFileName("name");
		uinObj.setBiometrics(Collections.singletonList(biometrics));
		UinDocument document = new UinDocument();
		document.setDoctypCode("ProofOfIdentity");
		document.setDocHash("47DEQpj8HBSa-_TImW-5JCeuQeRkm5NMpJWZG3hSuFU");
		document.setDocId("1234");
		document.setDocName("name");
		uinObj.setDocuments(Collections.singletonList(document));
		uinObj.setUinData(
				"{\"ProofOfIdentity\":{\"format\":\"pdf\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"}}"
						.getBytes());
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		service.retrieveIdentity("1234", "a");
	}

	@Test
	public void testUpdateIdentity() throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		request.setStatus("REGISTERED");
		Object obj = mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);
		RequestDTO req = new RequestDTO();
		req.setIdentity(obj);
		request.setRequest(req);
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		uinObj.setStatusCode("REGISTERED");
		Object obj2 = mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Mano\",\"label\":\"string\"}],\"lastName\":[{\"language\":\"AR\",\"value\":\"Mano\",\"label\":\"string\"},{\"language\":\"FR\",\"value\":\"Mano\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);
		uinObj.setUinData(mapper.writeValueAsBytes(obj2));
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		service.updateIdentity(request, "234").getResponse().equals(obj2);
	}

	@Test(expected = IdRepoAppException.class)
	public void testUpdateIdentityInvalidJsonException()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		request.setStatus("REGISTERED");
		Object obj = mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Mano\",\"label\":\"string\"},{\"language\":\"FR\",\"value\":\"Mano\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);

		RequestDTO req = new RequestDTO();
		req.setIdentity(obj);
		request.setRequest(req);
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
		service.updateIdentity(request, "1234");
	}
	
	@Test
	public void testUpdateIdentityWithDiff()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		request.setStatus("REGISTERED");
		Object obj = mapper.readValue(
				"{\"request\" : {\"age\" : 45}, \"UIN\" : 819431539502, \"identity\":{ \"fullName\" : [ {\"language\" : \"ara\"} ],\"IDSchemaVersion\" : 1.0, \"firstName\":[{\"language\":\"AR\",\"value\":\"Mano\",\"label\":\"string\"}], \"lastName\":[{\"language\":\"EN\",\"value\":\"Mano\",\"label\":\"string\"},{\"language\":\"FR\",\"value\":\"Mano\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);

		RequestDTO req = new RequestDTO();
		req.setIdentity(obj);
		request.setRequest(req);
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		uinObj.setStatusCode("REGISTERED");
		uinObj.setUinData(
				"{\"identity\":{ \"fullName\" : [ {\"language\" : \"aba\"} ],\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"},{\"language\":\"FR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes());
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.getStatusByUin(Mockito.any())).thenReturn("REGISTERED");
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		service.updateIdentity(request, "1234");
	}

	@SuppressWarnings("deprecation")
	@Test(expected = IdRepoAppException.class)
	public void testConvertToBytes() throws Throwable {
		ObjectMapper mockMapper = Mockito.mock(ObjectMapper.class);
		ReflectionTestUtils.setField(service, "mapper", mockMapper);
		try {
			when(mockMapper.writeValueAsBytes(Mockito.any())).thenThrow(new JsonMappingException(""));
			ReflectionTestUtils.invokeMethod(service, "convertToBytes", "1234");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@SuppressWarnings("deprecation")
	@Test(expected = IdRepoAppException.class)
	public void testConvertToObject() throws Throwable {
		ObjectMapper mockMapper = Mockito.mock(ObjectMapper.class);
		ReflectionTestUtils.setField(service, "mapper", mockMapper);
		try {
			when(mockMapper.readValue("1234".getBytes(), String.class)).thenThrow(new JsonMappingException(""));
			ReflectionTestUtils.invokeMethod(service, "convertToObject", "1234".getBytes(), String.class);
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test(expected = IdRepoAppException.class)
	public void testUpdateIdentityInvalidRegId() throws IdRepoAppException {
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(true);
		service.updateIdentity(new IdRequestDTO(), "12343");
	}

	@Test(expected = IdRepoAppException.class)
	public void testUpdateIdentityUinNotExists() throws IdRepoAppException {
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(false);
		service.updateIdentity(new IdRequestDTO(), "12343");
	}

	@Test(expected = IdRepoAppException.class)
	public void testUpdateIdentityDataAccessError() throws IdRepoAppException {
		when(uinRepo.existsByUin(Mockito.any())).thenThrow(new DataAccessResourceFailureException(""));
		service.updateIdentity(new IdRequestDTO(), "12343");
	}

	@Test
	public void testUpdateIdentityUpdateStatus() throws IdRepoAppException {
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setRegId("1234");
		uinObj.setUinRefId("1234");
		uinObj.setStatusCode("");
		uinObj.setUinData(new byte[] { 0 });
		uinObj.setUinDataHash("");
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		IdRequestDTO request = new IdRequestDTO();
		request.setStatus("status");
		service.updateIdentity(request, "12343");
	}

	@Test
	public void testConvertToBIR() {
		rFinger.getCbeffversion();
		BIRType birType = rFinger.toBIRType(rFinger);
		birType.setCBEFFVersion(new BIRVersion.BIRVersionBuilder()
                .withMajor(1)
                .withMinor(1)
                .build().toVersion());
		SBInfoType sbInfoType = new SBInfoType();
		sbInfoType.setFormatOwner(257l);
		sbInfoType.setFormatType(7l);
		birType.setSBInfo(sbInfoType);
		ReflectionTestUtils.invokeMethod(service, "convertToBIR",
				Collections.singletonList(birType));
	}

	@Test
	public void testConvertToBIRNoData() {
		ReflectionTestUtils.invokeMethod(service, "convertToBIR", Collections.singletonList(new BIRType()));
	}

	@SuppressWarnings("unchecked")
	@Test(expected = IdRepoAppException.class)
	public void testEncryptDecryptDocumentsException() throws Throwable {
		try {
			when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(ObjectNode.class),
					Mockito.any(Class.class))).thenThrow(new RestClientException(""));
			Uin uin = new Uin();
			uin.setUinData(new byte[] { 0 });
			ReflectionTestUtils.invokeMethod(service, "encryptDecryptDocuments", "document", "encrypt");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@SuppressWarnings("unchecked")
	@Test(expected = IdRepoAppException.class)
	public void testEncryptDecryptDocumentsNoData() throws Throwable {
		try {
			when(restTemplate.postForObject(Mockito.anyString(), Mockito.any(ObjectNode.class),
					Mockito.any(Class.class))).thenReturn(mapper.readValue("{}".getBytes(), ObjectNode.class));
			Uin uin = new Uin();
			uin.setUinData(new byte[] { 0 });
			ReflectionTestUtils.invokeMethod(service, "encryptDecryptDocuments", "document", "encrypt");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}
	
	@Test(expected = IdRepoAppException.class)
	public void testConvertToFMR() throws Throwable {
		try {
			when(cbeffUtil.updateXML(Mockito.any(), Mockito.any())).thenThrow(new NullPointerException());
			ReflectionTestUtils.invokeMethod(service, "convertToFMR", "123", "123");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test
	public void testIdentityUpdateBioDocuments() throws Exception {
		when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
		when(connection.getConnection()).thenReturn(conn);
		when(conn.doesBucketExistV2(Mockito.any())).thenReturn(true);
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		RequestDTO req = mapper.readValue(
				"{\"identity\":{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"individualBiometrics\",\"value\":\"dGVzdA\"}]}"
						.getBytes(),
				RequestDTO.class);
		request.setRequest(req);
		UinBiometric biometrics = new UinBiometric();
		biometrics.setBiometricFileType("individualBiometrics");
		biometrics.setBiometricFileHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
		biometrics.setBioFileId("1234");
		biometrics.setBiometricFileName("name");
		uinObj.setBiometrics(Collections.singletonList(biometrics));
		uinObj.setUinData(
				"{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"}}"
						.getBytes());
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(cbeffUtil.getBIRDataFromXML(Mockito.any())).thenReturn(Collections.singletonList(rFinger.toBIRType(rFinger)));
		when(cbeffUtil.updateXML(Mockito.any(), Mockito.any())).thenReturn("value".getBytes());
		service.updateIdentity(request, "1234");
	}
	
	@Test
	public void testIdentityUpdateNewBioDocument() throws Exception {
		when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
		when(connection.getConnection()).thenReturn(conn);
		when(conn.doesBucketExistV2(Mockito.any())).thenReturn(true);
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		RequestDTO req = mapper.readValue(
				"{\"identity\":{\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"parentOrGuardianBiometrics\",\"value\":\"dGVzdA\"}]}"
						.getBytes(),
				RequestDTO.class);
		request.setRequest(req);
		UinBiometric biometrics = new UinBiometric();
		biometrics.setBiometricFileType("individualBiometrics");
		biometrics.setBiometricFileHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
		biometrics.setBioFileId("1234");
		biometrics.setBiometricFileName("name");
		uinObj.setBiometrics(Lists.newArrayList(biometrics));
		uinObj.setUinData(
				"{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"}}"
						.getBytes());
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(cbeffUtil.getBIRDataFromXML(Mockito.any())).thenReturn(Collections.singletonList(rFinger.toBIRType(rFinger)));
		when(cbeffUtil.updateXML(Mockito.any(), Mockito.any())).thenReturn("value".getBytes());
		service.updateIdentity(request, "1234");
	}
	
	@Test(expected = IdRepoAppUncheckedException.class)
	public void testIdentityUpdateBioDocumentIdRepoAppException() throws Exception {
		when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
		when(connection.getConnection()).thenReturn(conn);
		when(conn.doesBucketExistV2(Mockito.any())).thenReturn(true);
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		RequestDTO req = mapper.readValue(
				"{\"identity\":{\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"parentOrGuardianBiometrics\",\"value\":\"dGVzdA\"}]}"
						.getBytes(),
				RequestDTO.class);
		request.setRequest(req);
		UinBiometric biometrics = new UinBiometric();
		biometrics.setBiometricFileType("parentOrGuardianBiometrics");
		biometrics.setBiometricFileHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
		biometrics.setBioFileId("1234");
		biometrics.setBiometricFileName("name");
		uinObj.setBiometrics(Collections.singletonList(biometrics));
		uinObj.setUinData(
				"{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"}}"
						.getBytes());
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(cbeffUtil.getBIRDataFromXML(Mockito.any())).thenReturn(Collections.singletonList(rFinger.toBIRType(rFinger)));
		when(cbeffUtil.updateXML(Mockito.any(), Mockito.any())).thenReturn("value".getBytes());
		when(connection.getFile(Mockito.any(), Mockito.any())).thenThrow(new IdRepoAppException("", ""));
		service.updateIdentity(request, "1234");
	}
	
	@Test(expected = IdRepoAppUncheckedException.class)
	public void testIdentityUpdateBioDocumentException() throws Exception {
		when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
		when(connection.getConnection()).thenReturn(conn);
		when(conn.doesBucketExistV2(Mockito.any())).thenReturn(true);
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		RequestDTO req = mapper.readValue(
				"{\"identity\":{\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"parentOrGuardianBiometrics\",\"value\":\"dGVzdA\"}]}"
						.getBytes(),
				RequestDTO.class);
		request.setRequest(req);
		UinBiometric biometrics = new UinBiometric();
		biometrics.setBiometricFileType("parentOrGuardianBiometrics");
		biometrics.setBiometricFileHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
		biometrics.setBioFileId("1234");
		biometrics.setBiometricFileName("name");
		uinObj.setBiometrics(Collections.singletonList(biometrics));
		uinObj.setUinData(
				"{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"}}"
						.getBytes());
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(cbeffUtil.getBIRDataFromXML(Mockito.any())).thenReturn(Collections.singletonList(rFinger.toBIRType(rFinger)));
		when(cbeffUtil.updateXML(Mockito.any(), Mockito.any())).thenReturn("value".getBytes());
		when(connection.getFile(Mockito.any(), Mockito.any())).thenThrow(new NullPointerException());
		service.updateIdentity(request, "1234");
	}
	
	@Test
	public void testIdentityUpdateNewDemoDocuments() throws Exception {
		when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
		when(connection.getConnection()).thenReturn(conn);
		when(conn.doesBucketExistV2(Mockito.any())).thenReturn(true);
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		RequestDTO req = mapper.readValue(
				"{\"identity\":{\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"1.0\",\"value\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"proofOfRelationship\",\"value\":\"dGVzdA\"}]}"
						.getBytes(),
				RequestDTO.class);
		request.setRequest(req);
		UinDocument document = new UinDocument();
		document.setDoccatCode("ProofOfIdentity");
		document.setDocHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
		document.setDocId("1234");
		document.setDocName("name");
		uinObj.setDocuments(Lists.newArrayList(document));
		uinObj.setUinData(
				"{\"ProofOfIdentity\":{\"format\":\"pdf\",\"version\":1.0,\"value\":\"fileReferenceID\"}}".getBytes());
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(cbeffUtil.getBIRDataFromXML(Mockito.any())).thenReturn(Collections.singletonList(rFinger.toBIRType(rFinger)));
		when(cbeffUtil.updateXML(Mockito.any(), Mockito.any())).thenReturn("value".getBytes());
		service.updateIdentity(request, "1234");
	}
	
	@Test
	public void testIdentityUpdateDemoDocuments() throws Exception {
		when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
		when(connection.getConnection()).thenReturn(conn);
		when(conn.doesBucketExistV2(Mockito.any())).thenReturn(true);
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		RequestDTO req = mapper.readValue(
				"{\"identity\":{\"ProofOfIdentity\":{\"format\":\"pdf\",\"type\":\"1.0\",\"value\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"ProofOfIdentity\",\"value\":\"dGVzdA\"}]}"
						.getBytes(),
				RequestDTO.class);
		request.setRequest(req);
		UinDocument document = new UinDocument();
		document.setDoccatCode("ProofOfIdentity");
		document.setDocHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
		document.setDocId("1234");
		document.setDocName("name");
		uinObj.setDocuments(Lists.newArrayList(document));
		uinObj.setUinData(
				"{\"ProofOfIdentity\":{\"format\":\"pdf\",\"version\":1.0,\"value\":\"fileReferenceID\"}}".getBytes());
		when(uinRepo.existsByUin(Mockito.any())).thenReturn(true);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUin(Mockito.any())).thenReturn(uinObj);
		when(cbeffUtil.getBIRDataFromXML(Mockito.any())).thenReturn(Collections.singletonList(rFinger.toBIRType(rFinger)));
		when(cbeffUtil.updateXML(Mockito.any(), Mockito.any())).thenReturn("value".getBytes());
		service.updateIdentity(request, "1234");
	}
}
