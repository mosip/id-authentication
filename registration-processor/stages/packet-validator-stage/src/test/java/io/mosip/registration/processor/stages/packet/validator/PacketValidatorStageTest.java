package io.mosip.registration.processor.stages.packet.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.ExceptionJSONInfoDTO;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.MainResponseDTO;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.ReverseDatasyncReponseDTO;
import io.mosip.registration.processor.core.spi.filesystem.adapter.FileSystemAdapter;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.filesystem.ceph.adapter.impl.utils.PacketFiles;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.stages.utils.CheckSumValidation;
import io.mosip.registration.processor.stages.utils.DocumentUtility;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.Vertx;

/**
 * The Class PacketValidatorStageTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ JsonUtil.class, IOUtils.class, HMACUtils.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class PacketValidatorStageTest {

	/** The input stream. */
	@Mock
	private InputStream inputStream;

	/** The filesystem ceph adapter impl. */
	@Mock
	private FileSystemAdapter<InputStream, Boolean> filesystemCephAdapterImpl;

	/** The registration status service. */
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The packet info manager. */
	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Mock
	InternalRegistrationStatusDto registrationStatusDto;

	@Mock
	CheckSumValidation checkSumValidation = new CheckSumValidation(filesystemCephAdapterImpl, registrationStatusDto);

	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The dto. */
	MessageDTO dto = new MessageDTO();

	/** The packet validator stage. */
	@InjectMocks
	private PacketValidatorStage packetValidatorStage = new PacketValidatorStage() {
		@Override
		public MosipEventBus getEventBus(Class<?> verticleName, String url) {
			vertx = Vertx.vertx();

			return new MosipEventBus(vertx) {
			};
		}

		@Override
		public void sendMessage(MosipEventBus mosipEventBus, MessageDTO message) {
		}
	};

	/**
	 * Test deploy verticle.
	 */
	@Test
	public void testDeployVerticle() {
		packetValidatorStage.deployVerticle();
	}

	/** The audit log request builder. */
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder = new AuditLogRequestBuilder();

	/** The packet meta info. */
	private PacketMetaInfo packetMetaInfo;

	/** The identity. */
	Identity identity = new Identity();

	/** The dto. */
	InternalRegistrationStatusDto statusDto;
	/** The list. */
	List<InternalRegistrationStatusDto> list;

	/** The list appender. */
	private ListAppender<ILoggingEvent> listAppender;

	/** The document utility. */
	@Mock
	DocumentUtility documentUtility;

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {

		statusDto = new InternalRegistrationStatusDto();
		statusDto.setRegistrationId("2018701130000410092018110735");
		statusDto.setStatusCode("PACKET_UPLOADED_TO_FILESYSTEM");
		registrationStatusService.addRegistrationStatus(statusDto);
		list = new ArrayList<InternalRegistrationStatusDto>();

		listAppender = new ListAppender<>();

		dto.setRid("2018701130000410092018110735");

		MockitoAnnotations.initMocks(this);
		packetMetaInfo = new PacketMetaInfo();

		FieldValue registrationType = new FieldValue();
		registrationType.setLabel("registrationType");
		registrationType.setValue("New");

		FieldValue applicantType = new FieldValue();
		applicantType.setLabel("applicantType");
		applicantType.setValue("Child");

		FieldValue isVerified = new FieldValue();
		isVerified.setLabel("isVerified");
		isVerified.setValue("Verified");
		
		FieldValue preRegistrationId = new FieldValue();
		preRegistrationId.setLabel("preRegistrationId");
		preRegistrationId.setValue("2018701130000410092018110736");
		

		identity.setMetaData(Arrays.asList(registrationType, applicantType, isVerified,preRegistrationId));

		Document documentPob = new Document();
		documentPob.setDocumentCategory("PROOFOFDATEOFBIRTH");
		documentPob.setDocumentName("ProofOfBirth");
		Document document = new Document();
		document.setDocumentCategory("PROOFOFRELATIONSHIP");
		document.setDocumentName("ProofOfRelation");
		List<Document> documents = new ArrayList<Document>();
		documents.add(documentPob);
		documents.add(document);
		identity.setDocuments(documents);
		Mockito.when(documentUtility.checkSum(anyString())).thenReturn(true);
		Mockito.when(documentUtility.getDocumentList(any())).thenReturn(documents);
		List<FieldValueArray> fieldValueArrayList = new ArrayList<FieldValueArray>();

		FieldValueArray applicantBiometric = new FieldValueArray();
		applicantBiometric.setLabel(PacketFiles.APPLICANTBIOMETRICSEQUENCE.name());
		List<String> applicantBiometricValues = new ArrayList<String>();
		applicantBiometricValues.add(PacketFiles.BOTHTHUMBS.name());
		applicantBiometric.setValue(applicantBiometricValues);
		fieldValueArrayList.add(applicantBiometric);
		FieldValueArray introducerBiometric = new FieldValueArray();
		introducerBiometric.setLabel(PacketFiles.INTRODUCERBIOMETRICSEQUENCE.name());
		List<String> introducerBiometricValues = new ArrayList<String>();
		introducerBiometricValues.add("introducerLeftThumb");
		introducerBiometric.setValue(introducerBiometricValues);
		fieldValueArrayList.add(introducerBiometric);
		FieldValueArray applicantDemographic = new FieldValueArray();
		applicantDemographic.setLabel(PacketFiles.APPLICANTDEMOGRAPHICSEQUENCE.name());
		List<String> applicantDemographicValues = new ArrayList<String>();
		applicantDemographicValues.add(PacketFiles.APPLICANTPHOTO.name());
		applicantDemographicValues.add("ProofOfBirth");
		applicantDemographicValues.add("ProofOfRelation");
		applicantDemographicValues.add("ProofOfAddress");
		applicantDemographicValues.add("ProofOfIdentity");
		applicantDemographic.setValue(applicantDemographicValues);
		fieldValueArrayList.add(applicantDemographic);
		identity.setHashSequence(fieldValueArrayList);
		packetMetaInfo.setIdentity(identity);

		AuditResponseDto auditResponseDto = new AuditResponseDto();
		Mockito.doReturn(auditResponseDto).when(auditLogRequestBuilder).createAuditRequestBuilder(
				"test case description", EventId.RPR_405.toString(), EventName.UPDATE.toString(),
				EventType.BUSINESS.toString(), "1234testcase");

		String test = "1234567890";
		byte[] data = "1234567890".getBytes();

		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		registrationStatusDto = new InternalRegistrationStatusDto();
		registrationStatusDto.setRegistrationId("2018701130000410092018110735");
		registrationStatusDto.setStatusCode("PACKET_UPLOADED_TO_FILESYSTEM");
		listAppender.start();
		list.add(registrationStatusDto);
		Mockito.when(registrationStatusService.getByStatus(anyString())).thenReturn(list);
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.TRUE);

		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);

		PowerMockito.mockStatic(HMACUtils.class);
		PowerMockito.doNothing().when(HMACUtils.class, "update", data);
		PowerMockito.when(HMACUtils.class, "digestAsPlainText", anyString().getBytes()).thenReturn(test);

		Mockito.doNothing().when(packetInfoManager).savePacketData(packetMetaInfo.getIdentity());
		MainResponseDTO<ReverseDatasyncReponseDTO> mainResponseDTO = new MainResponseDTO<>();
		ReverseDatasyncReponseDTO reverseDatasyncReponseDTO = new ReverseDatasyncReponseDTO();
		reverseDatasyncReponseDTO.setAlreadyStoredPreRegIds("2");
		reverseDatasyncReponseDTO.setCountOfStoredPreRegIds("2");
		reverseDatasyncReponseDTO.setTransactionId("07e3cea5-251d-11e9-a794-af3f5a85c414");
		mainResponseDTO.setErr(null);
		mainResponseDTO.setStatus(true);
		mainResponseDTO.setResTime("2019-01-31T05:57:02.816Z");
		mainResponseDTO.setResponse(reverseDatasyncReponseDTO);
		List<String> preRegIds = new ArrayList<>();
		preRegIds.add("12345678");
		preRegIds.add("123456789");
		//Mockito.when(packetInfoManager.getRegOsiPreRegId(Matchers.any())).thenReturn(preRegIds);
		Mockito.when(restClientService.postApi(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(),
				Matchers.any())).thenReturn(mainResponseDTO);

	}

	/**
	 * Test structural validation success.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testStructuralValidationSuccess() throws Exception {

		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertTrue(messageDto.getIsValid());

	}

	/**
	 * Test structural document validation failure.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testStructuralDocumentValidationFailure() throws Exception {
		packetMetaInfo = new PacketMetaInfo();
		Identity identity = new Identity();

		FieldValue registrationType = new FieldValue();
		registrationType.setLabel("registrationType");
		registrationType.setValue("New");

		FieldValue applicantType = new FieldValue();
		applicantType.setLabel("applicantType");
		applicantType.setValue("Child");

		FieldValue isVerified = new FieldValue();
		isVerified.setLabel("isVerified");
		isVerified.setValue("Verified");

		identity.setMetaData(Arrays.asList(registrationType, applicantType, isVerified));

		Document documentPob = new Document();
		documentPob.setDocumentCategory("pob");
		documentPob.setDocumentName("ProofOfBirth");
		Document document = new Document();
		document.setDocumentCategory("ProofOfRelation");
		document.setDocumentName("ProofOfRelation");
		List<Document> documents = new ArrayList<Document>();
		documents.add(documentPob);
		documents.add(document);
		identity.setDocuments(documents);

		List<FieldValueArray> fieldValueArrayList = new ArrayList<FieldValueArray>();

		FieldValueArray applicantBiometric = new FieldValueArray();
		applicantBiometric.setLabel(PacketFiles.APPLICANTBIOMETRICSEQUENCE.name());
		List<String> applicantBiometricValues = new ArrayList<String>();
		applicantBiometricValues.add(PacketFiles.BOTHTHUMBS.name());
		applicantBiometric.setValue(applicantBiometricValues);
		fieldValueArrayList.add(applicantBiometric);
		FieldValueArray introducerBiometric = new FieldValueArray();
		introducerBiometric.setLabel(PacketFiles.INTRODUCERBIOMETRICSEQUENCE.name());
		List<String> introducerBiometricValues = new ArrayList<String>();
		introducerBiometricValues.add("introducerLeftThumb");
		introducerBiometric.setValue(introducerBiometricValues);
		fieldValueArrayList.add(introducerBiometric);
		FieldValueArray applicantDemographic = new FieldValueArray();
		applicantDemographic.setLabel(PacketFiles.APPLICANTDEMOGRAPHICSEQUENCE.name());
		List<String> applicantDemographicValues = new ArrayList<String>();
		applicantDemographicValues.add(PacketFiles.APPLICANTPHOTO.name());
		applicantDemographicValues.add("ProofOfBirth");
		applicantDemographicValues.add("ProofOfAddress");
		applicantDemographic.setValue(applicantDemographicValues);
		fieldValueArrayList.add(applicantDemographic);
		identity.setHashSequence(fieldValueArrayList);
		packetMetaInfo.setIdentity(identity);

		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);
		MessageDTO messageDto = packetValidatorStage.process(dto);

		assertFalse(messageDto.getIsValid());

	}

	/**
	 * Test structural validation success for adult.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testStructuralValidationSuccessForAdult() throws Exception {
		listAppender.start();

		list.add(statusDto);

		FieldValue registrationType = new FieldValue();
		registrationType.setLabel("registrationType");
		registrationType.setValue("New");

		FieldValue applicantType = new FieldValue();
		applicantType.setLabel("applicantType");
		applicantType.setValue("Adult");

		FieldValue isVerified = new FieldValue();
		isVerified.setLabel("isVerified");
		isVerified.setValue("Verified");

		identity.setMetaData(Arrays.asList(registrationType, applicantType, isVerified));

		Document documentPob = new Document();
		documentPob.setDocumentCategory("PROOFOFDATEOFBIRTH");
		documentPob.setDocumentName("ProofOfBirth");

		Document document = new Document();
		document.setDocumentCategory("PROOFOFADDRESS");
		document.setDocumentName("ProofOfAddress");

		Document document2 = new Document();
		document2.setDocumentCategory("PROOFOFIDENTITY");
		document2.setDocumentName("ProofOfIdentity");

		List<Document> documents = new ArrayList<Document>();
		documents.add(documentPob);
		documents.add(document);
		documents.add(document2);
		identity.setDocuments(documents);
		Mockito.when(documentUtility.getDocumentList(any())).thenReturn(documents);
		List<FieldValueArray> fieldValueArrayList = new ArrayList<FieldValueArray>();

		FieldValueArray applicantBiometric = new FieldValueArray();
		applicantBiometric.setLabel(PacketFiles.APPLICANTBIOMETRICSEQUENCE.name());
		List<String> applicantBiometricValues = new ArrayList<String>();
		applicantBiometricValues.add(PacketFiles.BOTHTHUMBS.name());
		applicantBiometric.setValue(applicantBiometricValues);
		fieldValueArrayList.add(applicantBiometric);
		FieldValueArray introducerBiometric = new FieldValueArray();
		introducerBiometric.setLabel(PacketFiles.INTRODUCERBIOMETRICSEQUENCE.name());
		List<String> introducerBiometricValues = new ArrayList<String>();
		introducerBiometricValues.add("introducerLeftThumb");
		introducerBiometric.setValue(introducerBiometricValues);
		fieldValueArrayList.add(introducerBiometric);
		FieldValueArray applicantDemographic = new FieldValueArray();
		applicantDemographic.setLabel(PacketFiles.APPLICANTDEMOGRAPHICSEQUENCE.name());
		List<String> applicantDemographicValues = new ArrayList<String>();
		// applicantDemographicValues.add(PacketFiles.DEMOGRAPHICINFO.name());
		applicantDemographicValues.add(PacketFiles.APPLICANTPHOTO.name());
		applicantDemographicValues.add("ProofOfBirth");
		applicantDemographicValues.add("ProofOfRelation");
		applicantDemographicValues.add("ProofOfAddress");
		applicantDemographicValues.add("ProofOfIdentity");
		applicantDemographic.setValue(applicantDemographicValues);
		fieldValueArrayList.add(applicantDemographic);
		identity.setHashSequence(fieldValueArrayList);
		packetMetaInfo.setIdentity(identity);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);
		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertTrue(messageDto.getIsValid());

	}

	/**
	 * Test check sum validation failure.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testCheckSumValidationFailure() throws Exception {
		String test = "123456789";
		byte[] data = "1234567890".getBytes();

		Mockito.when(documentUtility.checkSum(anyString())).thenReturn(false);
		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.TRUE);

		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);

		PowerMockito.mockStatic(HMACUtils.class);
		PowerMockito.doNothing().when(HMACUtils.class, "update", data);
		PowerMockito.when(HMACUtils.class, "digestAsPlainText", anyString().getBytes()).thenReturn(test);

		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertFalse(messageDto.getIsValid());

	}

	/**
	 * Test files validation failure.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testFilesValidationFailure() throws Exception {

		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);

		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.FALSE);

		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertFalse(messageDto.getIsValid());
	}

	/**
	 * Test exceptions.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testExceptions() throws Exception {

		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);

		packetMetaInfo.getIdentity().setHashSequence(null);

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.TRUE);

		MessageDTO messageDto = packetValidatorStage.process(dto);

		assertEquals(true, messageDto.getInternalError());

	}

	/**
	 * Test IO exceptions.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testIOExceptions() throws Exception {

		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);

		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenThrow(new IOException());

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.TRUE);

		MessageDTO messageDto = packetValidatorStage.process(dto);

		assertEquals(true, messageDto.getInternalError());

	}

	/**
	 * Test check sum validation failure with retry count.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testCheckSumValidationFailureWithRetryCount() throws Exception {
		String test = "123456789";
		byte[] data = "1234567890".getBytes();
		Mockito.when(documentUtility.checkSum(anyString())).thenReturn(false);
		Mockito.when(filesystemCephAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();
		registrationStatusDto.setRetryCount(1);
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);
		Mockito.when(filesystemCephAdapterImpl.checkFileExistence(anyString(), anyString())).thenReturn(Boolean.TRUE);

		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(data);

		PowerMockito.mockStatic(HMACUtils.class);
		PowerMockito.doNothing().when(HMACUtils.class, "update", data);
		PowerMockito.when(HMACUtils.class, "digestAsPlainText", anyString().getBytes()).thenReturn(test);

		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertFalse(messageDto.getIsValid());

	}

	/**
	 * Data access exception test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void dataAccessExceptionTest() throws Exception {

		Mockito.when(registrationStatusService.getRegistrationStatus(anyString()))
				.thenThrow(new DataAccessException("") {
				});

		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertEquals(true, messageDto.getInternalError());

	}

	/**
	 * Gets the by satus exception test.
	 *
	 * @return the by satus exception test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getBySatusExceptionTest() throws Exception {

		listAppender.start();

		Mockito.doThrow(TablenotAccessibleException.class).when(registrationStatusService)
				.getByStatus(any(String.class));

		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertEquals(true, messageDto.getInternalError());

	}

	@Test
	public void testPreRegIdsAreNull() {
		//Mockito.when(packetInfoManager.getRegOsiPreRegId(Matchers.any())).thenReturn(null);
		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertTrue(messageDto.getIsValid());

	}

	@Test
	public void reverseDataSyncHttpClientErrorException() throws ApisResourceAccessException {
		ApisResourceAccessException apisResourceAccessException = Mockito.mock(ApisResourceAccessException.class);
		HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST,
				"Invalid request");
		Mockito.when(apisResourceAccessException.getCause()).thenReturn(httpClientErrorException);
		Mockito.when(restClientService.postApi(any(), any(), any(), any(), any()))
				.thenThrow(apisResourceAccessException);
		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertTrue(messageDto.getIsValid());

	}

	@Test
	public void reverseDataSyncServerErrorExceptionTest() throws ApisResourceAccessException {

		ApisResourceAccessException apisResourceAccessException = Mockito.mock(ApisResourceAccessException.class);
		HttpServerErrorException httpServerErrorException = new HttpServerErrorException(
				HttpStatus.INTERNAL_SERVER_ERROR,
				"MainResponseDTO(err=ExceptionJSONInfoDTO(errorCode=PRG_CORE_REQ_001, message=INVALID_REQUEST_ID), status=false, resTime=2019-01-30T09:42:24.441Z, response=null)");
		Mockito.when(apisResourceAccessException.getCause()).thenReturn(httpServerErrorException);
		Mockito.when(restClientService.postApi(any(), any(), any(), any(), any()))
				.thenThrow(apisResourceAccessException);
		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertTrue(messageDto.getIsValid());

	}

	@Test
	public void reverseDataSyncErrorTest() throws ApisResourceAccessException {
		MainResponseDTO<ReverseDatasyncReponseDTO> mainResponseDTO = new MainResponseDTO<>();
		ExceptionJSONInfoDTO exceptionJsonInfoDto = new ExceptionJSONInfoDTO();
		exceptionJsonInfoDto.setErrorCode("PRG_CORE_REQ_001");
		exceptionJsonInfoDto.setMessage("INVALID_REQUEST_ID");
		mainResponseDTO.setErr(exceptionJsonInfoDto);
		mainResponseDTO.setStatus(true);
		mainResponseDTO.setResTime("2019-01-31T05:57:02.816Z");
		mainResponseDTO.setResponse(null);
		List<String> preRegIds = new ArrayList<>();
		preRegIds.add("12345678");
		preRegIds.add("123456789");
		//Mockito.when(packetInfoManager.getRegOsiPreRegId(Matchers.any())).thenReturn(preRegIds);
		Mockito.when(restClientService.postApi(Matchers.any(), Matchers.any(), Matchers.any(), Matchers.any(),
				Matchers.any())).thenReturn(mainResponseDTO);

		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertTrue(messageDto.getIsValid());
	}

	@Test
	public void apiResourceExceptionTest() throws ApisResourceAccessException {
		ApisResourceAccessException apisResourceAccessException = new ApisResourceAccessException(
				"Packet Decryption failure");
		Mockito.when(restClientService.postApi(any(), any(), any(), any(), any()))
				.thenThrow(apisResourceAccessException);
		MessageDTO messageDto = packetValidatorStage.process(dto);
		assertTrue(messageDto.getIsValid());

	}
}