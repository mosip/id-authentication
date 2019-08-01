package io.mosip.idrepository.identity.test.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
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
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import io.mosip.idrepository.core.builder.RestRequestBuilder;
import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.dto.IdRequestDTO;
import io.mosip.idrepository.core.dto.IdResponseDTO;
import io.mosip.idrepository.core.dto.RequestDTO;
import io.mosip.idrepository.core.dto.RestRequestDTO;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.exception.IdRepoDataValidationException;
import io.mosip.idrepository.core.exception.RestServiceException;
import io.mosip.idrepository.core.helper.AuditHelper;
import io.mosip.idrepository.core.helper.RestHelper;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.idrepository.identity.entity.Uin;
import io.mosip.idrepository.identity.entity.UinBiometric;
import io.mosip.idrepository.identity.entity.UinDocument;
import io.mosip.idrepository.identity.provider.impl.FingerprintProvider;
import io.mosip.idrepository.identity.repository.UinBiometricHistoryRepo;
import io.mosip.idrepository.identity.repository.UinDocumentHistoryRepo;
import io.mosip.idrepository.identity.repository.UinEncryptSaltRepo;
import io.mosip.idrepository.identity.repository.UinHashSaltRepo;
import io.mosip.idrepository.identity.repository.UinHistoryRepo;
import io.mosip.idrepository.identity.repository.UinRepo;
import io.mosip.idrepository.identity.service.impl.DefaultShardResolver;
import io.mosip.idrepository.identity.service.impl.IdRepoProxyServiceImpl;
import io.mosip.idrepository.identity.service.impl.IdRepoServiceImpl;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.entity.BIRInfo;
import io.mosip.kernel.core.cbeffutil.entity.BIRVersion;
import io.mosip.kernel.core.cbeffutil.entity.SBInfo;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.ProcessedLevelType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.PurposeType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.QualityType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.RegistryIDType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.fsadapter.hdfs.constant.HDFSAdapterErrorCode;

/**
 * The Class IdRepoServiceTest.
 *
 * @author Manoj SP
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
@RunWith(SpringRunner.class)
@WebMvcTest
@ActiveProfiles("test")
@ConfigurationProperties("mosip.idrepo.identity")
public class IdRepoServiceTest {

	private static final String TYPE = "type";

	private static final String ACTIVATED = "ACTIVATED";

	@Mock
	FingerprintProvider fpProvider;

	@Mock
	CbeffImpl cbeffUtil;

	@Mock
	AuditHelper auditHelper;

	@Mock
	FileSystemAdapter connection;

	/** The service. */
	@InjectMocks
	IdRepoProxyServiceImpl proxyService;

	@InjectMocks
	IdRepoServiceImpl service;

	@InjectMocks
	IdRepoSecurityManager securityManager;

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
	private RestHelper restHelper;

	@Mock
	private DefaultShardResolver shardResolver;
	


	BIR rFinger = null;

	/** The uin repo. */
	@Mock
	private UinRepo uinRepo;

	/** The uin history repo. */
	@Mock
	private UinHistoryRepo uinHistoryRepo;

	@Mock
	RestRequestBuilder restBuilder;

	@Mock
	private UinHashSaltRepo uinHashSaltRepo;

	@Mock
	private UinEncryptSaltRepo uinEncryptSaltRepo;

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
	 * @throws IdRepoDataValidationException
	 * @throws RestServiceException
	 */
	@Before
	public void setup() throws FileNotFoundException, IOException, IdRepoDataValidationException, RestServiceException {
		RegistryIDType registryIDType = new RegistryIDType();
		registryIDType.setOrganization("257");
		registryIDType.setType("7");
		QualityType quality = new QualityType();
		quality.setScore(95l);
		rFinger = new BIR.BIRBuilder().withBdb("3".getBytes())
				.withVersion(new BIRVersion.BIRVersionBuilder().withMajor(1).withMinor(1).build())
				.withCbeffversion(new BIRVersion.BIRVersionBuilder().withMajor(1).withMinor(1).build())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(new BDBInfo.BDBInfoBuilder().withFormat(registryIDType)
						.withQuality(quality).withType(Arrays.asList(SingleType.FINGER))
						.withSubtype(Arrays.asList(SingleAnySubtypeType.RIGHT.value(),
								SingleAnySubtypeType.INDEX_FINGER.value()))
						.withPurpose(PurposeType.ENROLL).withLevel(ProcessedLevelType.RAW)
						.withCreationDate(LocalDateTime.now()).build())
				.withSbInfo(new SBInfo.SBInfoBuilder().setFormatOwner(registryIDType).build()).build();;
		ReflectionTestUtils.setField(securityManager, "env", env);
		ReflectionTestUtils.setField(securityManager, "mapper", mapper);
		ReflectionTestUtils.setField(service, "securityManager", securityManager);
		ReflectionTestUtils.setField(proxyService, "securityManager", securityManager);
		when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(new RestRequestDTO());
		when(restHelper.requestSync(Mockito.any()))
				.thenReturn(mapper.readValue("{\"response\":{\"data\":\"1234\"}}".getBytes(), ObjectNode.class));
		ReflectionTestUtils.setField(proxyService, "mapper", mapper);
		ReflectionTestUtils.setField(proxyService, "env", env);
		ReflectionTestUtils.setField(proxyService, "id", id);
		ReflectionTestUtils.setField(service, "mapper", mapper);
		ReflectionTestUtils.setField(service, "env", env);
		ReflectionTestUtils.setField(proxyService, "service", service);
		ReflectionTestUtils.setField(proxyService, "allowedBioAttributes",
				Collections.singletonList("individualBiometrics"));
		ReflectionTestUtils.setField(service, "bioAttributes",
				Lists.newArrayList("individualBiometrics", "parentOrGuardianBiometrics"));
		RequestDTO req = new RequestDTO();
		req.setRegistrationId("registrationId");
		request.setRequest(req);
		uin.setUin("1234");
		uin.setUinRefId("uinRefId");
		uin.setUinData(mapper.writeValueAsBytes(request));
		uin.setStatusCode(env.getProperty("mosip.idrepo.status.registered"));
	}

	/**
	 * Test add identity.
	 *
	 * @throws IdRepoAppException   the id repo app exception
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@Test
	public void testAddIdentity() throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		uinObj.setStatusCode(ACTIVATED);
		ObjectNode obj = mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				ObjectNode.class);
		RequestDTO req = new RequestDTO();
		req.setIdentity(obj);
		req.setRegistrationId("27841457360002620190730095024");
		request.setRequest(req);
		when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(false);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
		when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg	");
		when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
		IdResponseDTO addIdentity = proxyService.addIdentity(request, "1234");
		assertEquals(ACTIVATED, addIdentity.getResponse().getStatus());
	}

	@Test
	public void testAddIdentityWithDemoDocuments()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {

	}

	@Test
	public void testAddDocumentsDataAccessException()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
			Uin uinObj = new Uin();
			uinObj.setUin("1234");
			uinObj.setUinRefId("1234");
			RequestDTO req = mapper.readValue(
					"{\"identity\":{\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"proofOfDateOfBirth\",\"value\":\"dGVzdA\"}]}"
							.getBytes(),
					RequestDTO.class);
			req.setRegistrationId("27841457360002620190730095024");
			request.setRequest(req);
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(false);
			when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			when(uinDocHRepo.save(Mockito.any())).thenThrow(new DataAccessResourceFailureException(null));
			proxyService.addIdentity(request, "1234");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testAddDocumentsJDBCConnectionException()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
			Uin uinObj = new Uin();
			uinObj.setUin("1234");
			uinObj.setUinRefId("1234");
			RequestDTO req = mapper.readValue(
					"{\"identity\":{\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"proofOfDateOfBirth\",\"value\":\"dGVzdA\"}]}"
							.getBytes(),
					RequestDTO.class);
			req.setRegistrationId("27841457360002620190730095024");
			request.setRequest(req);
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(false);
			when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			when(uinDocHRepo.save(Mockito.any())).thenThrow(new JDBCConnectionException(null, null));
			proxyService.addIdentity(request, "1234");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testAddIdentityDocumentStoreFailed()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
			when(connection.storeFile(Mockito.any(), Mockito.any(), Mockito.any()))
					.thenThrow(new FSAdapterException(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorCode(),
							IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorMessage()));
			Uin uinObj = new Uin();
			uinObj.setUin("1234");
			uinObj.setUinRefId("1234");
			RequestDTO req = mapper
					.readValue(("{\"identity\":{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\""
							+ IdRepoConstants.FILE_NAME_ATTRIBUTE.getValue()
							+ "\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"individualBiometrics\",\"value\":\"dGVzdA\"}]}")
									.getBytes(),
							RequestDTO.class);
			req.setRegistrationId("27841457360002620190730095024");
			request.setRequest(req);
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(false);
			when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			proxyService.addIdentity(request, "1234");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}
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
		uinObj.setStatusCode(ACTIVATED);
		RequestDTO req = mapper
				.readValue(("{\"identity\":{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\""
						+ IdRepoConstants.FILE_NAME_ATTRIBUTE.getValue()
						+ "\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"individualBiometrics\",\"value\":\"dGVzdA\"}]}")
								.getBytes(),
						RequestDTO.class);
		 req.setRegistrationId("27841457360002620190730095024");
		request.setRequest(req);
		when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(false);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
		when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
		when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
		IdResponseDTO addIdentity = proxyService.addIdentity(request, "1234");
		assertEquals(ACTIVATED, addIdentity.getResponse().getStatus());
	}

	@Test
	public void testAddIdentityRecordExists()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			Uin uinObj = new Uin();
			uinObj.setUin("1234");
			uinObj.setUinRefId("1234");
			ObjectNode obj = mapper.readValue(
					"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
							.getBytes(),
					ObjectNode.class);
			RequestDTO req = new RequestDTO();
			req.setRegistrationId("27841457360002620190730095024");
			req.setIdentity(obj);
			request.setRequest(req);
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinRepo.existsByRegId(Mockito.any())).thenReturn(true);
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			proxyService.addIdentity(request, "1234");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.RECORD_EXISTS.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.RECORD_EXISTS.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testAddIdentityDataAccessException()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			Uin uinObj = new Uin();
			uinObj.setUin("1234");
			uinObj.setUinRefId("1234");
			ObjectNode obj = mapper.readValue(
					"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
							.getBytes(),
					ObjectNode.class);
			RequestDTO req = new RequestDTO();
			 req.setRegistrationId("27841457360002620190730095024");
			req.setIdentity(obj);
			request.setRequest(req);
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			when(uinRepo.save(Mockito.any())).thenThrow(new RecoverableDataAccessException(null));
			proxyService.addIdentity(request, "1234");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}
	}

	/**
	 * Test add identity exception.
	 *
	 * @throws IdRepoAppException   the id repo app exception
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@Test
	public void testAddIdentityException()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			when(uinRepo.save(Mockito.any())).thenThrow(new DataAccessResourceFailureException(null));
			RequestDTO request2 = new RequestDTO();
			request2.setIdentity(mapper.readValue(
					"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
							.getBytes(),
					Object.class));
			request2.setRegistrationId("27841457360002620190730095024");
			request.setRequest(request2);
			proxyService.addIdentity(request, "1234");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}
	}

	/**
	 * Test retrieve identity.
	 *
	 * @throws IdRepoAppException   the id repo app exception
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
		String identity = "{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}";
		uinObj.setUinData(identity.getBytes());
		when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
		when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
		IdResponseDTO retrieveIdentityByUin = proxyService.retrieveIdentityByUin("1234", null);
		assertEquals(identity, mapper.writeValueAsString(retrieveIdentityByUin.getResponse().getIdentity()));

	}

	@Test
	public void testRetrieveIdentityNoRecordExists()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			Uin uinObj = new Uin();
			uinObj.setUin("1234");
			uinObj.setUinRefId("1234");
			uinObj.setUinData(
					"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
							.getBytes());
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(false);
			proxyService.retrieveIdentityByUin("1234", null);
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.NO_RECORD_FOUND.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testRetrieveIdentityDataAccessException()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			Uin uinObj = new Uin();
			uinObj.setUin("1234");
			uinObj.setUinRefId("1234");
			uinObj.setUinData(
					"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
							.getBytes());
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			when(uinRepo.existsByUinHash(Mockito.any())).thenThrow(new JDBCConnectionException("", null));
			proxyService.retrieveIdentityByUin("1234", null);
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testRetrieveIdentityWithBioDocuments()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		when(connection.getFile(Mockito.any(), Mockito.any()))
				.thenReturn(IOUtils.toInputStream("dGVzdA", Charset.defaultCharset()));
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		UinBiometric biometrics = new UinBiometric();
		biometrics.setBiometricFileType("individualBiometrics");
		biometrics.setBiometricFileHash("5B72C3B57A72C6497461289FCA7B1F865ED6FB0596B446FEA1F92AF931A5D4B7");
		biometrics.setBioFileId("1234");
		biometrics.setBiometricFileName("name");
		uinObj.setBiometrics(Collections.singletonList(biometrics));
		String identityWithDoc = "{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}}";
		uinObj.setUinData(identityWithDoc.getBytes());
		when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(true);
		when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
		when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
		IdResponseDTO retrieveIdentityByUin = proxyService.retrieveIdentityByUin("1234", "bio");
		assertEquals(identityWithDoc, mapper.writeValueAsString(retrieveIdentityByUin.getResponse().getIdentity()));
	}

	@Test
	public void testRetrieveIdentityWithBioDocumentsFileRetrievalError()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			when(connection.getFile(Mockito.any(), Mockito.any()))
					.thenThrow(new FSAdapterException(HDFSAdapterErrorCode.FILE_NOT_FOUND_EXCEPTION.getErrorCode(),
							HDFSAdapterErrorCode.FILE_NOT_FOUND_EXCEPTION.getErrorMessage()));
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
					"{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}}"
							.getBytes());
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinRepo.existsByRegId(Mockito.any())).thenReturn(true);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			proxyService.retrieveIdentityByUin("1234", "bio");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.FILE_NOT_FOUND.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.FILE_NOT_FOUND.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testRetrieveIdentityWithBioDocumentsFileRetrievalErrorUnknownError()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			when(connection.getFile(Mockito.any(), Mockito.any()))
					.thenThrow(new FSAdapterException(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorCode(),
							IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorMessage()));
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
					"{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}}"
							.getBytes());
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			proxyService.retrieveIdentityByUin("1234", "bio");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}

	}

	@Test
	public void testRetrieveIdentityWithBioDocumentsFileRetrievalIOError()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			FileInputStream mockStream = Mockito.mock(FileInputStream.class);
			when(mockStream.read(Mockito.any())).thenThrow(new FileNotFoundException());
			when(connection.getFile(Mockito.any(), Mockito.any())).thenReturn(mockStream);
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
					"{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}}"
							.getBytes());
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinRepo.existsByRegId(Mockito.any())).thenReturn(true);
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			proxyService.retrieveIdentityByUin("1234", "bio");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testRetrieveIdentityWithBioDocumentsHashFail()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			when(connection.getFile(Mockito.any(), Mockito.any()))
					.thenReturn(IOUtils.toInputStream("data", Charset.defaultCharset()));
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
					"{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}}"
							.getBytes());
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinRepo.existsByRegId(Mockito.any())).thenReturn(true);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			proxyService.retrieveIdentityByUin("1234", "bio");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.DOCUMENT_HASH_MISMATCH.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.DOCUMENT_HASH_MISMATCH.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testRetrieveIdentityWithDemoDocuments()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		when(connection.getFile(Mockito.any(), Mockito.any()))
				.thenReturn(IOUtils.toInputStream("data", Charset.defaultCharset()));
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		UinDocument document = new UinDocument();
		document.setDoccatCode("ProofOfIdentity");
		document.setDocHash("5B72C3B57A72C6497461289FCA7B1F865ED6FB0596B446FEA1F92AF931A5D4B7");
		document.setDocId("1234");
		document.setDocName("name");
		uinObj.setDocuments(Collections.singletonList(document));
		String identityWithDoc = "{\"ProofOfIdentity\":{\"format\":\"pdf\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}}";
		uinObj.setUinData(identityWithDoc.getBytes());
		when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
		when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
		when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
		IdResponseDTO retrieveIdentityByUin = proxyService.retrieveIdentityByUin("1234", "demo");
		assertEquals(identityWithDoc, mapper.writeValueAsString(retrieveIdentityByUin.getResponse().getIdentity()));
	}

	@Test
	public void testRetrieveIdentityWithDemoDocumentsIOError()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			FileInputStream mockStream = Mockito.mock(FileInputStream.class);
			when(mockStream.read(Mockito.any())).thenThrow(new FileNotFoundException());
			when(connection.getFile(Mockito.any(), Mockito.any())).thenReturn(mockStream);
			Uin uinObj = new Uin();
			uinObj.setUin("1234");
			uinObj.setUinRefId("1234");
			UinDocument document = new UinDocument();
			document.setDoccatCode("ProofOfIdentity");
			document.setDocHash("5B72C3B57A72C6497461289FCA7B1F865ED6FB0596B446FEA1F92AF931A5D4B7");
			document.setDocId("1234");
			document.setDocName("name");
			uinObj.setDocuments(Collections.singletonList(document));
			uinObj.setUinData(
					"{\"ProofOfIdentity\":{\"format\":\"pdf\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}}"
							.getBytes());
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			proxyService.retrieveIdentityByUin("1234", "demo");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testRetrieveIdentityWithDemoDocumentsFSError()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			when(connection.getFile(Mockito.any(), Mockito.any()))
					.thenThrow(new FSAdapterException(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorCode(),
							IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorMessage()));
			Uin uinObj = new Uin();
			uinObj.setUin("1234");
			uinObj.setUinRefId("1234");
			UinDocument document = new UinDocument();
			document.setDoccatCode("ProofOfIdentity");
			document.setDocHash("5B72C3B57A72C6497461289FCA7B1F865ED6FB0596B446FEA1F92AF931A5D4B7");
			document.setDocId("1234");
			document.setDocName("name");
			uinObj.setDocuments(Collections.singletonList(document));
			uinObj.setUinData(
					"{\"ProofOfIdentity\":{\"format\":\"pdf\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}}"
							.getBytes());
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			proxyService.retrieveIdentityByUin("1234", "demo");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testRetrieveIdentityWithDemoDocumentsFileNotFound()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			when(connection.getFile(Mockito.any(), Mockito.any()))
					.thenThrow(new FSAdapterException(HDFSAdapterErrorCode.FILE_NOT_FOUND_EXCEPTION.getErrorCode(),
							HDFSAdapterErrorCode.FILE_NOT_FOUND_EXCEPTION.getErrorMessage()));
			Uin uinObj = new Uin();
			uinObj.setUin("1234");
			uinObj.setUinRefId("1234");
			UinDocument document = new UinDocument();
			document.setDoccatCode("ProofOfIdentity");
			document.setDocHash("5B72C3B57A72C6497461289FCA7B1F865ED6FB0596B446FEA1F92AF931A5D4B7");
			document.setDocId("1234");
			document.setDocName("name");
			uinObj.setDocuments(Collections.singletonList(document));
			uinObj.setUinData(
					"{\"ProofOfIdentity\":{\"format\":\"pdf\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}}"
							.getBytes());
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg	");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			proxyService.retrieveIdentityByUin("1234", "demo");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.FILE_NOT_FOUND.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.FILE_NOT_FOUND.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testRetrieveIdentityWithDemoDocumentsHashFail()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			when(connection.getFile(Mockito.any(), Mockito.any()))
					.thenReturn(IOUtils.toInputStream("data", Charset.defaultCharset()));
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
					"{\"ProofOfIdentity\":{\"format\":\"pdf\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}}"
							.getBytes());
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg	");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			proxyService.retrieveIdentityByUin("1234", "demo");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.DOCUMENT_HASH_MISMATCH.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.DOCUMENT_HASH_MISMATCH.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testRetrieveIdentityWithAllType()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		when(connection.getFile(Mockito.any(), Mockito.any()))
				.thenReturn(IOUtils.toInputStream("data", Charset.defaultCharset()));
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		UinBiometric biometrics = new UinBiometric();
		biometrics.setBiometricFileType("individualBiometrics");
		biometrics.setBiometricFileHash("5B72C3B57A72C6497461289FCA7B1F865ED6FB0596B446FEA1F92AF931A5D4B7");
		biometrics.setBioFileId("1234");
		biometrics.setBiometricFileName("name");
		uinObj.setBiometrics(Collections.singletonList(biometrics));
		UinDocument document = new UinDocument();
		document.setDoccatCode("ProofOfIdentity");
		document.setDocHash("5B72C3B57A72C6497461289FCA7B1F865ED6FB0596B446FEA1F92AF931A5D4B7");
		document.setDocId("1234");
		document.setDocName("name");
		uinObj.setDocuments(Collections.singletonList(document));
		String identity = "{\"ProofOfIdentity\":{\"format\":\"pdf\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}}";
		uinObj.setUinData(identity.getBytes());
		when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
		when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
		when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
		when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
		IdResponseDTO retrieveIdentityByUin = proxyService.retrieveIdentityByUin("1234", "all");
		assertEquals(identity, mapper.writeValueAsString(retrieveIdentityByUin.getResponse().getIdentity()));
	}

	@Test
	public void testRetrieveIdentityWithUnknownType()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
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
					"{\"ProofOfIdentity\":{\"format\":\"pdf\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}}"
							.getBytes());
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			proxyService.retrieveIdentityByUin("1234", "a");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorCode(), e.getErrorCode());
			assertEquals(String.format(IdRepoErrorConstants.INVALID_INPUT_PARAMETER.getErrorMessage(), TYPE),
					e.getErrorText());
		}
	}

	@Test
	public void testUpdateIdentity() throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		Object obj = mapper.readValue(
				"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Manoj\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);
		RequestDTO req = new RequestDTO();
		req.setStatus("REGISTERED");
		req.setRegistrationId("27841457360002620190730095024");
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
		when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
		when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
		when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
		when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
		proxyService.updateIdentity(request, "234").getResponse().equals(obj2);
	}

	@Test
	public void testUpdateIdentityInvalidJsonException()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		try {
			Object obj = mapper.readValue(
					"{\"identity\":{\"firstName\":[{\"language\":\"AR\",\"value\":\"Mano\",\"label\":\"string\"},{\"language\":\"FR\",\"value\":\"Mano\",\"label\":\"string\"}]}}"
							.getBytes(),
					Object.class);

			RequestDTO req = new RequestDTO();
			req.setStatus("REGISTERED");
			req.setRegistrationId("27841457360002620190730095024");
			req.setIdentity(obj);
			request.setRequest(req);
			Uin uinObj = new Uin();
			uinObj.setUin("1234");
			uinObj.setUinRefId("1234");
			uinObj.setStatusCode("REGISTERED");
			uinObj.setUinData(
					"rgAADOjjov89sjVwvI8Gc4ngK9lQgPxMpNDe+LXb5qI=|P6NGM4tYz1Zdy+ZC/ikKYNp1csxrarX/dCEta1HCHWE=|P6NGM4tYz1Zdy+ZC/ikKYNp1csxrarX/dCEta1HCHWE="
							.getBytes());
			when(uinRepo.getStatusByUin(Mockito.any())).thenReturn("REGISTERED");
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			proxyService.updateIdentity(request, "1234");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.ID_OBJECT_PROCESSING_FAILED.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.ID_OBJECT_PROCESSING_FAILED.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testUpdateIdentityWithDiff()
			throws IdRepoAppException, JsonParseException, JsonMappingException, IOException {
		Object obj = mapper.readValue(
				"{\"request\" : {\"age\" : 45}, \"UIN\" : 819431539502, \"identity\":{ \"fullName\" : [ {\"language\" : \"ara\"} ],\"IDSchemaVersion\" : 1.0, \"firstName\":[{\"language\":\"AR\",\"value\":\"Mano\",\"label\":\"string\"}], \"lastName\":[{\"language\":\"EN\",\"value\":\"Mano\",\"label\":\"string\"},{\"language\":\"FR\",\"value\":\"Mano\",\"label\":\"string\"}]}}"
						.getBytes(),
				Object.class);

		RequestDTO req = new RequestDTO();
		req.setStatus("REGISTERED");
		req.setRegistrationId("27841457360002620190730095024");
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
		when(uinRepo.getStatusByUin(Mockito.any())).thenReturn("REGISTERED");
		when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
		when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
		when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
		when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
		proxyService.updateIdentity(request, "234").getResponse().equals(obj2);
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
	public void testConvertToObjectProxy() throws Throwable {
		ObjectMapper mockMapper = Mockito.mock(ObjectMapper.class);
		ReflectionTestUtils.setField(proxyService, "mapper", mockMapper);
		try {
			when(mockMapper.readValue("1234".getBytes(), String.class)).thenThrow(new JsonMappingException(""));
			ReflectionTestUtils.invokeMethod(proxyService, "convertToObject", "1234".getBytes(), String.class);
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

	@Test
	public void testUpdateIdentityInvalidRegId() throws IdRepoAppException {
		try {
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinRepo.existsByRegId(Mockito.any())).thenReturn(true);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			IdRequestDTO idRequestDTO = new IdRequestDTO();
			RequestDTO req=new RequestDTO();
			req.setRegistrationId("27841457360002620190730095024");
			idRequestDTO.setRequest(req);
			proxyService.updateIdentity(idRequestDTO, "12343");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.RECORD_EXISTS.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.RECORD_EXISTS.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testUpdateIdentityUinNotExists() throws IdRepoAppException {
		try {
			IdRequestDTO idRequestDTO = new IdRequestDTO();
			RequestDTO requestDTO = new RequestDTO();
			requestDTO.setRegistrationId("1234");
			idRequestDTO.setRequest(requestDTO);
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(false);
			when(uinRepo.existsByRegId(Mockito.any())).thenReturn(true);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			proxyService.updateIdentity(idRequestDTO, "12343");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.NO_RECORD_FOUND.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.NO_RECORD_FOUND.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testUpdateIdentityDataAccessError() throws IdRepoAppException {
		try {
			IdRequestDTO idRequestDTO = new IdRequestDTO();
			RequestDTO requestDTO = new RequestDTO();
			requestDTO.setRegistrationId("1234");
			idRequestDTO.setRequest(requestDTO);
			when(uinRepo.existsByRegId(Mockito.any())).thenReturn(true);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			when(uinRepo.existsByUinHash(Mockito.any())).thenThrow(new DataAccessResourceFailureException(""));
			proxyService.updateIdentity(idRequestDTO, "12343");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.DATABASE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}
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
		when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
		when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
		IdRequestDTO request = new IdRequestDTO();
		RequestDTO req = new RequestDTO();
		String status = "status";
		req.setStatus(status);
		req.setRegistrationId("27841457360002620190730095024");
		request.setRequest(req);
		IdResponseDTO updateIdentity = proxyService.updateIdentity(request, "12343");
		assertEquals(status, updateIdentity.getResponse().getStatus());
	}

	@Test(expected = IdRepoAppException.class)
	public void testEncryptDecryptDocumentsExceptionProxy() throws Throwable {
		try {
			RestRequestDTO restRequestDTO = new RestRequestDTO();
			when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(restRequestDTO);
			when(restHelper.requestSync(Mockito.any()))
					.thenThrow(new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR));
			Uin uin = new Uin();
			uin.setUinData(new byte[] { 0 });
			ReflectionTestUtils.invokeMethod(securityManager, "encryptDecryptData", restRequestDTO);
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test(expected = IdRepoAppException.class)
	public void testEncryptDecryptDocumentsException() throws Throwable {
		try {
			RestRequestDTO restRequestDTO = new RestRequestDTO();
			when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(restRequestDTO);
			when(restHelper.requestSync(Mockito.any()))
					.thenThrow(new RestServiceException(IdRepoErrorConstants.CLIENT_ERROR));
			Uin uin = new Uin();
			uin.setUinData(new byte[] { 0 });
			ReflectionTestUtils.invokeMethod(securityManager, "encryptDecryptData", restRequestDTO);
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test(expected = IdRepoAppException.class)
	public void testEncryptDecryptDocumentsNoData() throws Throwable {
		try {
			RestRequestDTO restRequestDTO = new RestRequestDTO();
			when(restBuilder.buildRequest(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(restRequestDTO);
			when(restHelper.requestSync(Mockito.any())).thenReturn(mapper.readValue("{}".getBytes(), ObjectNode.class));
			Uin uin = new Uin();
			uin.setUinData(new byte[] { 0 });
			ReflectionTestUtils.invokeMethod(securityManager, "encryptDecryptData", restRequestDTO);
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
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		uinObj.setStatusCode(ACTIVATED);
		RequestDTO req = mapper
				.readValue(("{\"identity\":{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\""
						+ IdRepoConstants.FILE_NAME_ATTRIBUTE.getValue()
						+ "\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"individualBiometrics\",\"value\":\"dGVzdA\"}]}")
								.getBytes(),
						RequestDTO.class);
		req.setRegistrationId("27841457360002620190730095024");
		request.setRequest(req);
		UinBiometric biometrics = new UinBiometric();
		biometrics.setBiometricFileType("individualBiometrics");
		biometrics.setBiometricFileHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
		biometrics.setBioFileId("1234.cbeff");
		biometrics.setBiometricFileName("name");
		uinObj.setBiometrics(Collections.singletonList(biometrics));
		uinObj.setUinData(
				("{\"status\": \"ACTIVATED\",\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\""
						+ IdRepoConstants.FILE_NAME_ATTRIBUTE.getValue() + "\":\"fileReferenceID\"}}").getBytes());
		when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
		when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
		when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
		when(cbeffUtil.getBIRDataFromXML(Mockito.any()))
				.thenReturn(Collections.singletonList(rFinger.toBIRType(rFinger)));
		when(cbeffUtil.updateXML(Mockito.any(), Mockito.any())).thenReturn("value".getBytes());
		when(connection.getFile(Mockito.any(), Mockito.any()))
				.thenReturn(IOUtils.toInputStream("dGVzdA", Charset.defaultCharset()));
		IdResponseDTO updateIdentity = proxyService.updateIdentity(request, "1234");
		assertEquals(ACTIVATED, updateIdentity.getResponse().getStatus());
	}

	@Test
	public void testIdentityUpdateNewBioDocument() throws Exception {
		when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		uinObj.setStatusCode(ACTIVATED);
		RequestDTO req = mapper
				.readValue(("{\"identity\":{\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\""
						+ IdRepoConstants.FILE_NAME_ATTRIBUTE.getValue()
						+ "\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"parentOrGuardianBiometrics\",\"value\":\"dGVzdA\"}]}")
								.getBytes(),
						RequestDTO.class);
						req.setRegistrationId("27841457360002620190730095024");
		request.setRequest(req);
		
		UinBiometric biometrics = new UinBiometric();
		biometrics.setBiometricFileType("individualBiometrics");
		biometrics.setBiometricFileHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
		biometrics.setBioFileId("1234.cbeff");
		biometrics.setBiometricFileName("name");
		uinObj.setBiometrics(Lists.newArrayList(biometrics));
		uinObj.setUinData(("{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\""
				+ IdRepoConstants.FILE_NAME_ATTRIBUTE.getValue() + "\":\"fileReferenceID\"}}").getBytes());
		when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
		when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
		when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
		when(cbeffUtil.getBIRDataFromXML(Mockito.any()))
				.thenReturn(Collections.singletonList(rFinger.toBIRType(rFinger)));
		when(cbeffUtil.updateXML(Mockito.any(), Mockito.any())).thenReturn("value".getBytes());
		IdResponseDTO updateIdentity = proxyService.updateIdentity(request, "1234");
		assertEquals(ACTIVATED, updateIdentity.getResponse().getStatus());
	}

	@Test
	public void testIdentityUpdateNewBioDocumentNonCbeff() throws Exception {
		when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		uinObj.setStatusCode(ACTIVATED);
		RequestDTO req = mapper
				.readValue(("{\"identity\":{\"parentOrGuardianBiometrics\":{\"format\":\"pdf\",\"version\":1.0,\""
						+ IdRepoConstants.FILE_NAME_ATTRIBUTE.getValue()
						+ "\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"parentOrGuardianBiometrics\",\"value\":\"dGVzdA\"}]}")
								.getBytes(),
						RequestDTO.class);
		req.setRegistrationId("27841457360002620190730095024");
		request.setRequest(req);
		UinBiometric biometrics = new UinBiometric();
		biometrics.setBiometricFileType("individualBiometrics");
		biometrics.setBiometricFileHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
		biometrics.setBioFileId("1234.cbeff");
		biometrics.setBiometricFileName("name");
		uinObj.setBiometrics(Lists.newArrayList(biometrics));
		uinObj.setUinData(("{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\""
				+ IdRepoConstants.FILE_NAME_ATTRIBUTE.getValue() + "\":\"fileReferenceID\"}}").getBytes());
		when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
		when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
		when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
		when(cbeffUtil.getBIRDataFromXML(Mockito.any()))
				.thenReturn(Collections.singletonList(rFinger.toBIRType(rFinger)));
		when(cbeffUtil.updateXML(Mockito.any(), Mockito.any())).thenReturn("value".getBytes());
		IdResponseDTO updateIdentity = proxyService.updateIdentity(request, "1234");
		assertEquals(ACTIVATED, updateIdentity.getResponse().getStatus());
	}

	@Test
	public void testIdentityUpdateBioDocumentIdRepoAppException() throws Exception {
		try {
			when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
			Uin uinObj = new Uin();
			uinObj.setUin("1234");
			uinObj.setUinRefId("1234");
			RequestDTO req = mapper.readValue(
					"{\"identity\":{\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"parentOrGuardianBiometrics\",\"value\":\"dGVzdA\"}]}"
							.getBytes(),
					RequestDTO.class);
			req.setRegistrationId("27841457360002620190730095024");
			request.setRequest(req);
			UinBiometric biometrics = new UinBiometric();
			biometrics.setBiometricFileType("parentOrGuardianBiometrics");
			biometrics.setBiometricFileHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
			biometrics.setBioFileId("1234");
			biometrics.setBiometricFileName("name");
			uinObj.setBiometrics(Collections.singletonList(biometrics));
			uinObj.setUinData(
					"{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}}"
							.getBytes());
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			when(cbeffUtil.getBIRDataFromXML(Mockito.any()))
					.thenReturn(Collections.singletonList(rFinger.toBIRType(rFinger)));
			when(cbeffUtil.updateXML(Mockito.any(), Mockito.any())).thenReturn("value".getBytes());
			when(connection.getFile(Mockito.any(), Mockito.any()))
					.thenThrow(new FSAdapterException(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorCode(),
							IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorMessage()));
			proxyService.updateIdentity(request, "1234");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testIdentityUpdateBioDocumentFSAdpapterException() throws Exception {
		try {
			when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
			Uin uinObj = new Uin();
			uinObj.setUin("1234");
			uinObj.setUinRefId("1234");
			RequestDTO req = mapper.readValue(
					"{\"identity\":{\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"parentOrGuardianBiometrics\",\"value\":\"dGVzdA\"}]}"
							.getBytes(),
					RequestDTO.class);
			req.setRegistrationId("27841457360002620190730095024");
			request.setRequest(req);
			UinBiometric biometrics = new UinBiometric();
			biometrics.setBiometricFileType("parentOrGuardianBiometrics");
			biometrics.setBiometricFileHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
			biometrics.setBioFileId("1234");
			biometrics.setBiometricFileName("name");
			uinObj.setBiometrics(Collections.singletonList(biometrics));
			uinObj.setUinData(
					"{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}}"
							.getBytes());
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
			when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			when(cbeffUtil.getBIRDataFromXML(Mockito.any()))
					.thenReturn(Collections.singletonList(rFinger.toBIRType(rFinger)));
			when(cbeffUtil.updateXML(Mockito.any(), Mockito.any())).thenReturn("value".getBytes());
			when(connection.getFile(Mockito.any(), Mockito.any()))
					.thenThrow(new FSAdapterException(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorCode(),
							IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorMessage()));
			proxyService.updateIdentity(request, "1234");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.FILE_STORAGE_ACCESS_ERROR.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testIdentityUpdateBioDocumentException() throws Exception {
		try {
			when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
			Uin uinObj = new Uin();
			uinObj.setUin("1234");
			uinObj.setUinRefId("1234");
			RequestDTO req = mapper.readValue(
					"{\"identity\":{\"parentOrGuardianBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"parentOrGuardianBiometrics\",\"value\":\"dGVzdA\"}]}"
							.getBytes(),
					RequestDTO.class);
			req.setRegistrationId("27841457360002620190730095024");
			request.setRequest(req);
			UinBiometric biometrics = new UinBiometric();
			biometrics.setBiometricFileType("parentOrGuardianBiometrics");
			biometrics.setBiometricFileHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
			biometrics.setBioFileId("1234");
			biometrics.setBiometricFileName("name");
			uinObj.setBiometrics(Collections.singletonList(biometrics));
			uinObj.setUinData(
					"{\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}}"
							.getBytes());
			when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
			when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
			when(uinRepo.existsByRegId(Mockito.any())).thenReturn(true);
			when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
			when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
			when(cbeffUtil.getBIRDataFromXML(Mockito.any()))
					.thenReturn(Collections.singletonList(rFinger.toBIRType(rFinger)));
			when(cbeffUtil.updateXML(Mockito.any(), Mockito.any())).thenReturn("value".getBytes());
			proxyService.updateIdentity(request, "1234");
		} catch (IdRepoAppException e) {
			assertEquals(IdRepoErrorConstants.RECORD_EXISTS.getErrorCode(), e.getErrorCode());
			assertEquals(IdRepoErrorConstants.RECORD_EXISTS.getErrorMessage(), e.getErrorText());
		}
	}

	@Test
	public void testIdentityUpdateNewDemoDocuments() throws Exception {
		when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		uinObj.setStatusCode(ACTIVATED);
		RequestDTO req = mapper
				.readValue(("{\"identity\":{\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"1.0\",\""
						+ IdRepoConstants.FILE_NAME_ATTRIBUTE.getValue()
						+ "\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"proofOfRelationship\",\"value\":\"dGVzdA\"}]}")
								.getBytes(),
						RequestDTO.class);
		req.setRegistrationId("27841457360002620190730095024");
		request.setRequest(req);
		UinDocument document = new UinDocument();
		document.setDoccatCode("ProofOfIdentity");
		document.setDocHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
		document.setDocId("1234");
		document.setDocName("name");
		uinObj.setDocuments(Lists.newArrayList(document));
		uinObj.setUinData(("{\"ProofOfIdentity\":{\"format\":\"pdf\",\"version\":1.0,\""
				+ IdRepoConstants.FILE_NAME_ATTRIBUTE.getValue() + "\":\"fileReferenceID\"}}").getBytes());
		when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
		when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
		when(cbeffUtil.getBIRDataFromXML(Mockito.any()))
				.thenReturn(Collections.singletonList(rFinger.toBIRType(rFinger)));
		when(cbeffUtil.updateXML(Mockito.any(), Mockito.any())).thenReturn("value".getBytes());
		IdResponseDTO updateIdentity = proxyService.updateIdentity(request, "1234");
		assertEquals(ACTIVATED, updateIdentity.getResponse().getStatus());
	}

	@Test
	public void testIdentityUpdateDemoDocuments() throws Exception {
		when(fpProvider.convertFIRtoFMR(Mockito.any())).thenReturn(Collections.singletonList(rFinger));
		Uin uinObj = new Uin();
		uinObj.setUin("1234");
		uinObj.setUinRefId("1234");
		uinObj.setStatusCode(ACTIVATED);
		RequestDTO req = mapper.readValue(("{\"identity\":{\"ProofOfIdentity\":{\"format\":\"pdf\",\"type\":\"1.0\",\""
				+ IdRepoConstants.FILE_NAME_ATTRIBUTE.getValue()
				+ "\":\"fileReferenceID\"}},\"documents\":[{\"category\":\"ProofOfIdentity\",\"value\":\"dGVzdA\"}]}")
						.getBytes(),
				RequestDTO.class);
		req.setRegistrationId("27841457360002620190730095024");
		request.setRequest(req);
		UinDocument document = new UinDocument();
		document.setDoccatCode("ProofOfIdentity");
		document.setDocHash("W3LDtXpyxkl0YSifynsfhl7W-wWWtEb-ofkq-TGl1Lc");
		document.setDocId("1234");
		document.setDocName("name");
		uinObj.setDocuments(Lists.newArrayList(document));
		uinObj.setUinData(("{\"ProofOfIdentity\":{\"format\":\"pdf\",\"version\":1.0,\""
				+ IdRepoConstants.FILE_NAME_ATTRIBUTE.getValue() + "\":\"fileReferenceID\"}}").getBytes());
		when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
		when(uinRepo.existsByRegId(Mockito.any())).thenReturn(false);
		when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
		when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
		when(cbeffUtil.getBIRDataFromXML(Mockito.any()))
				.thenReturn(Collections.singletonList(rFinger.toBIRType(rFinger)));
		when(cbeffUtil.updateXML(Mockito.any(), Mockito.any())).thenReturn("value".getBytes());
		proxyService.updateIdentity(request, "1234");
		IdResponseDTO updateIdentity = proxyService.updateIdentity(request, "1234");
		assertEquals(ACTIVATED, updateIdentity.getResponse().getStatus());
	}

	@Test(expected = IdRepoAppException.class)
	public void testNowParseException() throws Throwable {
		try {
			MockEnvironment mockEnv = new MockEnvironment();
			mockEnv.merge((ConfigurableEnvironment) env);
			mockEnv.setProperty("mosip.utc-datetime-pattern", "abcd");
			ReflectionTestUtils.setField(service, "env", mockEnv);
			ReflectionTestUtils.invokeMethod(service, "now");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

	@Test
	public void testRetriveIdentityByRid_Valid() throws JsonProcessingException {
		String value = "6158236213";
		String ridValue = "27847657360002520190320095029";
		when(connection.getFile(Mockito.any(), Mockito.any()))
				.thenReturn(IOUtils.toInputStream("data", Charset.defaultCharset()));
		Uin uinObj = new Uin();
		uinObj.setUin(value);
		uinObj.setUinRefId(ridValue);
		UinDocument document = new UinDocument();
		document.setDoccatCode("ProofOfIdentity");
		document.setDocHash("5B72C3B57A72C6497461289FCA7B1F865ED6FB0596B446FEA1F92AF931A5D4B7");
		document.setDocId("1234");
		document.setDocName("name");
		uinObj.setDocuments(Collections.singletonList(document));
		String identity = "{\"ProofOfIdentity\":{\"format\":\"pdf\",\"version\":1.0,\"fileReference\":\"fileReferenceID\"}}";
		uinObj.setUinData(identity.getBytes());
		when(uinRepo.existsByUinHash(Mockito.any())).thenReturn(true);
		Mockito.when(uinRepo.getUinHashByRid(Mockito.anyString())).thenReturn(value);
		when(uinRepo.findByUinHash(Mockito.any())).thenReturn(uinObj);
		when(uinEncryptSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("7C9JlRD32RnFTzAmeTfIzg");
		when(uinHashSaltRepo.retrieveSaltById(Mockito.anyInt())).thenReturn("AG7JQI1HwFp_cI_DcdAQ9A");
		IdResponseDTO idResponseDTO = ReflectionTestUtils.invokeMethod(proxyService, "retrieveIdentityByRid", ridValue,
				"demo");
		assertEquals(identity, mapper.writeValueAsString(idResponseDTO.getResponse().getIdentity()));
	}

	@Test(expected = IdRepoAppException.class)
	public void testRetrieveIdentityByRid_Invalid() throws Throwable {
		try {
			Mockito.when(uinRepo.getUinHashByRid(Mockito.anyString())).thenReturn(null);
			ReflectionTestUtils.invokeMethod(proxyService, "retrieveIdentityByRid", "27847657360002520190320095029",
					"demo");
		} catch (UndeclaredThrowableException e) {
			throw e.getCause();
		}
	}

}
