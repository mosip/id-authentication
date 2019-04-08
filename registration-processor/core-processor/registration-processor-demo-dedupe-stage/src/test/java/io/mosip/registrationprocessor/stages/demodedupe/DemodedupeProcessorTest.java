package io.mosip.registrationprocessor.stages.demodedupe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.kernel.core.fsadapter.exception.FSAdapterException;
import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicInfoDto;
import io.mosip.registration.processor.core.packet.dto.idjson.Document;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.IndividualDemographicDedupeEntity;
import io.mosip.registration.processor.packet.storage.entity.ManualVerificationEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.stages.demodedupe.DemoDedupe;
import io.mosip.registration.processor.stages.demodedupe.DemodedupeProcessor;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;

/**
 * The Class DemodedupeStageTest.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
@PrepareForTest({ JsonUtil.class, IOUtils.class, HMACUtils.class })
public class DemodedupeProcessorTest {

	/** The registration status service. */
	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The packet info manager. */
	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The manual verfication repository. */
	@Mock
	private BasePacketRepository<ManualVerificationEntity, String> manualVerficationRepository;

	/** The manual verification entity. */
	private ManualVerificationEntity manualVerificationEntity;

	/** The demographic dedupe repository. */
	@Mock
	private BasePacketRepository<IndividualDemographicDedupeEntity, String> demographicDedupeRepository;

	/** The demo dedupe. */
	@Mock
	private DemoDedupe demoDedupe;

	@Mock
	private InputStream inputStream;

	@Mock
	private FileSystemAdapter adapter;

	/** The dto. */
	private MessageDTO dto = new MessageDTO();

	/** The duplicate dtos. */
	private List<DemographicInfoDto> duplicateDtos = new ArrayList<>();

	/** The audit log request builder. */
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder = new AuditLogRequestBuilder();

	@Mock
	RegistrationExceptionMapperUtil registrationStatusMapperUtil;

	@InjectMocks
	private DemodedupeProcessor demodedupeProcessor;

	private InternalRegistrationStatusDto registrationStatusDto;

	private Identity identity = new Identity();

	private PacketMetaInfo packetMetaInfo;

	private String stageName = "DemoDedupeStage";

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {

		dto.setRid("2018701130000410092018110735");

		MockitoAnnotations.initMocks(this);

		DemographicInfoDto dto1 = new DemographicInfoDto();
		DemographicInfoDto dto2 = new DemographicInfoDto();

		duplicateDtos.add(dto1);
		duplicateDtos.add(dto2);

		registrationStatusDto = new InternalRegistrationStatusDto();
		registrationStatusDto.setRegistrationType("NEW");
		registrationStatusDto.setRegistrationId("2018701130000410092018110735");

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

		identity.setMetaData(Arrays.asList(registrationType, applicantType, isVerified, preRegistrationId));

		Document documentPob = new Document();
		documentPob.setDocumentCategory("PROOFOFDATEOFBIRTH");
		documentPob.setDocumentName("ProofOfBirth");
		Document document = new Document();
		document.setDocumentCategory("PROOFOFRELATIONSHIP");
		document.setDocumentName("ProofOfRelation");
		List<Document> documents = new ArrayList<Document>();
		documents.add(documentPob);
		documents.add(document);
		// Mockito.when(documentUtility.getDocumentList(any())).thenReturn(documents);
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
		List<String> sequence2 = new ArrayList<>();
		sequence2.add("audit");
		List<FieldValueArray> fieldValueArrayListSequence = new ArrayList<FieldValueArray>();
		FieldValueArray hashsequence2 = new FieldValueArray();
		hashsequence2.setValue(sequence2);
		fieldValueArrayListSequence.add(hashsequence2);
		identity.setHashSequence2(fieldValueArrayListSequence);
		packetMetaInfo.setIdentity(identity);

		Mockito.when(registrationStatusMapperUtil.getStatusCode(any())).thenReturn("ERROR");

		AuditResponseDto auditResponseDto = new AuditResponseDto();
		ResponseWrapper<AuditResponseDto>  responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(auditResponseDto);
		Mockito.doReturn(responseWrapper).when(auditLogRequestBuilder).createAuditRequestBuilder(
				"test case description", EventId.RPR_405.toString(), EventName.UPDATE.toString(),
				EventType.BUSINESS.toString(), "1234testcase", ApiName.AUDIT);

		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();

		registrationStatusDto.setRetryCount(null);
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(registrationStatusDto);

	}

	/**
	 * Test demo dedupe success.
	 *
	 * @throws Exception
	 */
	@Test
	public void testDemoDedupeSuccess() throws Exception {
		byte[] b = "sds".getBytes();
		List<DemographicInfoDto> emptyDuplicateDtoSet = new ArrayList<>();
		Mockito.when(adapter.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(b);
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(demoDedupe.performDedupe(anyString())).thenReturn(emptyDuplicateDtoSet);

		MessageDTO messageDto = demodedupeProcessor.process(dto, stageName);
		assertTrue(messageDto.getIsValid());

	}

	@Test
	public void testDemoDedupeSuccessNotDuplicateAfterAuth() throws Exception {

		List<DemographicInfoDto> emptyDuplicateDtoSet = new ArrayList<>();
		Mockito.when(demoDedupe.performDedupe(anyString())).thenReturn(duplicateDtos);
		Mockito.when(demoDedupe.authenticateDuplicates(anyString(), anyList())).thenReturn(false);
		registrationStatusDto.setRegistrationType("TEST");
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		MessageDTO messageDto = demodedupeProcessor.process(dto, stageName);
		assertTrue(messageDto.getIsValid());
		// assertEquals(false, messageDto.getIsValid());

	}

	/**
	 * Test demo dedupe potential match.
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDemoDedupePotentialMatch() throws Exception {
		byte[] b = "sds".getBytes();
		Mockito.when(adapter.getFile(anyString(), anyString())).thenReturn(inputStream);
		PowerMockito.mockStatic(JsonUtil.class);
		PowerMockito.mockStatic(IOUtils.class);
		PowerMockito.when(JsonUtil.class, "inputStreamtoJavaObject", inputStream, PacketMetaInfo.class)
				.thenReturn(packetMetaInfo);
		PowerMockito.when(IOUtils.class, "toByteArray", inputStream).thenReturn(b);
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(manualVerficationRepository.save(any())).thenReturn(manualVerificationEntity);
		Mockito.when(demoDedupe.performDedupe(anyString())).thenReturn(duplicateDtos);
		registrationStatusDto.setRetryCount(2);
		Mockito.when(demoDedupe.authenticateDuplicates(anyString(), anyList())).thenReturn(true);

		MessageDTO messageDto = demodedupeProcessor.process(dto, stageName);
		verify(packetInfoManager, times(1)).saveManualAdjudicationData(anyList(), anyString(), any());

	}

	/**
	 * Test demo dedupe failure.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws IntrospectionException
	 * @throws ParseException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testDemoDedupeFailure() throws ApisResourceAccessException, IOException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, ParseException, IntrospectionException {
		InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();

		Mockito.when(demoDedupe.performDedupe(anyString())).thenReturn(duplicateDtos);

		Mockito.when(demoDedupe.authenticateDuplicates(anyString(), anyList())).thenReturn(true);

		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		registrationStatusDto.setRetryCount(3);

		demodedupeProcessor.process(dto, stageName);

	}

	/**
	 * Test resource exception.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws IntrospectionException
	 * @throws ParseException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testResourceException() throws ApisResourceAccessException, IOException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, ParseException, IntrospectionException {
		Mockito.when(demoDedupe.performDedupe(anyString())).thenReturn(duplicateDtos);

		ApisResourceAccessException exp = new ApisResourceAccessException("errorMessage");
		Mockito.doThrow(exp).when(demoDedupe).authenticateDuplicates(anyString(), anyList());

		MessageDTO messageDto = demodedupeProcessor.process(dto, stageName);
		assertEquals(true, messageDto.getInternalError());
	}

	/**
	 * Test exception.
	 *
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws IntrospectionException
	 * @throws ParseException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testException() throws ApisResourceAccessException, IOException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, ParseException, IntrospectionException {
		Mockito.when(demoDedupe.performDedupe(anyString())).thenReturn(duplicateDtos);

		ApisResourceAccessException exp = new ApisResourceAccessException("errorMessage");
		Mockito.doThrow(exp).when(demoDedupe).authenticateDuplicates(anyString(), anyList());
		registrationStatusDto.setRegistrationType("TEST");
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		MessageDTO messageDto = demodedupeProcessor.process(dto, stageName);
		assertEquals(true, messageDto.getInternalError());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFSAdapterExceptionException()
			throws ApisResourceAccessException, IOException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ParseException, IntrospectionException {
		Mockito.when(demoDedupe.performDedupe(anyString())).thenReturn(duplicateDtos);

		FSAdapterException exp = new FSAdapterException("errorMessage", "test");
		Mockito.doThrow(exp).when(demoDedupe).authenticateDuplicates(anyString(), anyList());
		registrationStatusDto.setRegistrationType("TEST");
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		MessageDTO messageDto = demodedupeProcessor.process(dto, stageName);
		assertEquals(true, messageDto.getInternalError());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIntrospectionException()
			throws ApisResourceAccessException, IOException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ParseException, IntrospectionException {
		Mockito.when(demoDedupe.performDedupe(anyString())).thenReturn(duplicateDtos);

		IntrospectionException exp = new IntrospectionException("errorMessage");
		Mockito.doThrow(exp).when(demoDedupe).authenticateDuplicates(anyString(), anyList());
		registrationStatusDto.setRegistrationType("TEST");
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		MessageDTO messageDto = demodedupeProcessor.process(dto, stageName);
		assertEquals(true, messageDto.getInternalError());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testInvocationTargetException()
			throws ApisResourceAccessException, IOException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ParseException, IntrospectionException {
		Mockito.when(demoDedupe.performDedupe(anyString())).thenReturn(duplicateDtos);
		Exception e=new Exception();
		InvocationTargetException exp = new InvocationTargetException(e,"errorMessage");
		Mockito.doThrow(exp).when(demoDedupe).authenticateDuplicates(anyString(), anyList());
		registrationStatusDto.setRegistrationType("TEST");
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		MessageDTO messageDto = demodedupeProcessor.process(dto, stageName);
		assertEquals(true, messageDto.getInternalError());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIllegalAccessException()
			throws ApisResourceAccessException, IOException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ParseException, IntrospectionException {
		Mockito.when(demoDedupe.performDedupe(anyString())).thenReturn(duplicateDtos);
		IllegalAccessException exp = new IllegalAccessException("errorMessage");
		Mockito.doThrow(exp).when(demoDedupe).authenticateDuplicates(anyString(), anyList());
		registrationStatusDto.setRegistrationType("TEST");
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		MessageDTO messageDto = demodedupeProcessor.process(dto, stageName);
		assertEquals(true, messageDto.getInternalError());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testParseException()
			throws ApisResourceAccessException, IOException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ParseException, IntrospectionException {
		Mockito.when(demoDedupe.performDedupe(anyString())).thenReturn(duplicateDtos);
		ParseException exp = new ParseException("errorMessage", 1);
		Mockito.doThrow(exp).when(demoDedupe).authenticateDuplicates(anyString(), anyList());
		registrationStatusDto.setRegistrationType("TEST");
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		MessageDTO messageDto = demodedupeProcessor.process(dto, stageName);
		assertEquals(true, messageDto.getInternalError());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIllegalArgumentException()
			throws ApisResourceAccessException, IOException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ParseException, IntrospectionException {
		Mockito.when(demoDedupe.performDedupe(anyString())).thenReturn(duplicateDtos);
		IllegalArgumentException exp = new IllegalArgumentException("errorMessage");
		Mockito.doThrow(exp).when(demoDedupe).authenticateDuplicates(anyString(), anyList());
		registrationStatusDto.setRegistrationType("TEST");
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		MessageDTO messageDto = demodedupeProcessor.process(dto, stageName);
		assertEquals(true, messageDto.getInternalError());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testIOException()
			throws ApisResourceAccessException, IOException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, ParseException, IntrospectionException {
		Mockito.when(demoDedupe.performDedupe(anyString())).thenReturn(duplicateDtos);
		IOException exp = new IOException("errorMessage");
		Mockito.doThrow(exp).when(demoDedupe).authenticateDuplicates(anyString(), anyList());
		registrationStatusDto.setRegistrationType("TEST");
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		MessageDTO messageDto = demodedupeProcessor.process(dto, stageName);
		assertEquals(true, messageDto.getInternalError());
	}



}