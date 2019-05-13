package io.mosip.registration.processor.biodedupe.stage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.RegistrationExceptionMapperUtil;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseEntity;
import io.mosip.registration.processor.packet.storage.utils.ABISHandlerUtil;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.status.dao.RegistrationStatusDao;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.Vertx;

/**
 * The Class BioDedupeStageTest.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
@PrepareForTest({ Utilities.class })
public class BioDedupeProcessorTest {

	/** The registration status service. */
	@Mock
	RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The packet info manager. */
	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The bio dedupe service. */
	@Mock
	private BioDedupeService bioDedupeService;

	@Mock
	private RegistrationStatusDao registrationStatusDao;

	@Mock
	private PacketInfoDao packetInfoDao;

	/** The dto. */
	MessageDTO dto = new MessageDTO();

	/** The vertx. */
	private Vertx vertx;

	/** The registration status dto. */
	InternalRegistrationStatusDto registrationStatusDto = new InternalRegistrationStatusDto();

	/** The audit log request builder. */
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The matched reg ids. */
	List<String> matchedRegIds = new ArrayList<String>();

	@Mock
	RegistrationExceptionMapperUtil registrationStatusMapperUtil;

	@InjectMocks
	private BioDedupeProcessor bioDedupeProcessor;

	private String stageName = "BioDedupeStage";

	@Mock
	Utilities utilities;

	@Mock
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Mock
	RegistrationStatusEntity entity = new RegistrationStatusEntity();

	@Mock
	private ABISHandlerUtil abisHandlerUtil;

	private static final String NEW = "NEW";

	private static final String POST_API_PROCESS = "POST_API_PROCESS";

	private RegistrationProcessorIdentity regProcessorIdentityJson = new RegistrationProcessorIdentity();

	@Mock
	ObjectMapper mapIdentityJsonStringToObject;

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		ReflectionTestUtils.setField(bioDedupeProcessor, "ageLimit", "4");

		AuditResponseDto auditResponseDto = new AuditResponseDto();
		ResponseWrapper<AuditResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(auditResponseDto);
		Mockito.doReturn(responseWrapper).when(auditLogRequestBuilder).createAuditRequestBuilder(
				"test case description", EventId.RPR_405.toString(), EventName.UPDATE.toString(),
				EventType.BUSINESS.toString(), "1234testcase", ApiName.AUDIT);
		dto.setRid("reg1234");

		registrationStatusDto.setRegistrationId("reg1234");
		registrationStatusDto.setRegistrationType("new");
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		Mockito.when(abisHandlerUtil.getPacketStatus(any(), any())).thenReturn(NEW);
		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any()))
				.thenReturn("1233445566".getBytes("UTF-16"));
		Mockito.when(registrationStatusMapperUtil.getStatusCode(any())).thenReturn("ERROR");

		entity.setLatestRegistrationTransactionId("t123");
		Mockito.when(registrationStatusDao.findById(any())).thenReturn(entity);
	}

	/**
	 * Test bio dedupe success.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testNewInsertionPostProcessing() throws Exception {

		MessageDTO messageDto = bioDedupeProcessor.process(dto, stageName);
		assertTrue(messageDto.getMessageBusAddress().toString()
				.equalsIgnoreCase(MessageBusAddress.ABIS_HANDLER_BUS_IN.toString()));

	}

	@Test
	public void testNewInsertionToUinSuccess() throws Exception {

		Mockito.when(restClientService.getApi(any(), any(), any(), any(), any())).thenReturn(null);
		Mockito.when(utilities.getApplicantAge(any())).thenReturn(2);
		MessageDTO messageDto = bioDedupeProcessor.process(dto, stageName);

		assertTrue(messageDto.getIsValid());

	}

	@Test
	public void testNewIdentifyToUINStage() throws Exception {
		Mockito.when(abisHandlerUtil.getPacketStatus(any(), any())).thenReturn(POST_API_PROCESS);
		MessageDTO messageDto = bioDedupeProcessor.process(dto, stageName);

		assertTrue(messageDto.getIsValid());

	}

	@Test
	public void testNewIdentifyToManualStage() throws Exception {

		List<String> list = new ArrayList<>();
		list.add("1");
		Mockito.when(abisHandlerUtil.getPacketStatus(any(), any())).thenReturn("POST_API_PROCESS");

		Mockito.when(abisHandlerUtil.getUniqueRegIds(any(), any())).thenReturn(list);
		MessageDTO messageDto = bioDedupeProcessor.process(dto, stageName);

		assertFalse(messageDto.getIsValid());

	}

	@Test
	public void testeHandlerIdentifyManualStage() throws Exception {

		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.class, "getJson", anyString(), anyString()).thenReturn("");

		registrationStatusDto.setRegistrationId("reg1234");
		registrationStatusDto.setRegistrationType("Update");
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(registrationStatusDto);
		MessageDTO messageDto = bioDedupeProcessor.process(dto, stageName);

		assertFalse(messageDto.getIsValid());
	}

	@Test
	public void testBioDeDupUpdatePacketHandlerProcessingSuccess() {
		Mockito.when(abisHandlerUtil.getPacketStatus(any(), any())).thenReturn("Handler");
		AbisResponseDetEntity abisDet = new AbisResponseDetEntity();
		abisDet.setCrBy("mosip");

		AbisResponseEntity abis = new AbisResponseEntity();
		abis.setStatusCode("status");

		List<AbisResponseDetEntity> abisResponseDetEntities = new ArrayList<>();

		abisResponseDetEntities.add(abisDet);
		// Mockito.when(bioDedupDao.getAbisResponseDetailRecords(any(),
		// any())).thenReturn(abisResponseDetEntities);
		MessageDTO messageDto = bioDedupeProcessor.process(dto, stageName);

		assertTrue(messageDto.getMessageBusAddress().getAddress().equalsIgnoreCase("uin-generator-bus-in"));
	}

	@Test
	public void testBioDeDupUpdatePacketHandlerProcessingFailure() {
		Mockito.when(abisHandlerUtil.getPacketStatus(any(), any())).thenReturn("POST_API_PROCESS");
		AbisResponseDetEntity abisDet = new AbisResponseDetEntity();
		abisDet.setCrBy("mosip");

		AbisResponseEntity abis = new AbisResponseEntity();
		abis.setStatusCode("status");

		List<AbisResponseDetEntity> abisResponseDetEntities = new ArrayList<>();

		abisResponseDetEntities.add(abisDet);
		// Mockito.when(bioDedupDao.getAbisResponseDetailRecords(any(),
		// any())).thenReturn(abisResponseDetEntities);
		MessageDTO messageDto = bioDedupeProcessor.process(dto, stageName);

		assertTrue(messageDto.getInternalError());
	}

	@Test
	public void testBioDeDupUpdatePacketHandlerProcessingBiometricHandler() {
		Mockito.when(abisHandlerUtil.getPacketStatus(any(), any())).thenReturn("POST_API_PROCESS");
		AbisResponseDetEntity abisDet = new AbisResponseDetEntity();
		abisDet.setCrBy("mosip");

		AbisResponseEntity abis = new AbisResponseEntity();
		abis.setStatusCode("status");

		List<AbisResponseDetEntity> abisResponseDetEntities = new ArrayList<>();

		abisResponseDetEntities.add(abisDet);
		// Mockito.when(bioDedupDao.getAbisResponseDetailRecords(any(),
		// any())).thenReturn(abisResponseDetEntities);
		MessageDTO messageDto = bioDedupeProcessor.process(dto, stageName);

		assertEquals(messageDto.getMessageBusAddress().getAddress(), "abis-handler-bus-in");
	}
}
