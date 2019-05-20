package io.mosip.registration.processor.abis.handler.stage.test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.List;

import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.registration.processor.abis.handler.stage.AbisHandlerStage;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.abis.AbisApplicationDto;
import io.mosip.registration.processor.core.packet.dto.abis.RegBioRefDto;
import io.mosip.registration.processor.core.packet.dto.abis.RegDemoDedupeListDto;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.rest.client.audit.dto.AuditResponseDto;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.Vertx;


/**
 * The Class AbisHandlerStageTest.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
public class AbisHandlerStageTest {

	/** The audit log request builder. */
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The registration status service. */
	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The packet info manager. */
	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	/** The registration status dto. */
	@Mock
	private InternalRegistrationStatusDto registrationStatusDto;

	/** The abis application dtos. */
	List<AbisApplicationDto> abisApplicationDtos = new ArrayList<>();
	
	/** The bio ref dtos. */
	List<RegBioRefDto> bioRefDtos = new ArrayList<>();
	
	/** The reg demo dedupe list dto list. */
	List<RegDemoDedupeListDto> regDemoDedupeListDtoList = new ArrayList<>();

	List<AbisRequestDto> abisRequestDtoList = new ArrayList<>();

	/** The abis handler stage. */
	@InjectMocks
	private AbisHandlerStage abisHandlerStage = new AbisHandlerStage() {
		@Override
		public MosipEventBus getEventBus(Object verticleName, String url, int instanceNumber) {
			vertx = Vertx.vertx();

			return new MosipEventBus(vertx) {
			};
		}

		@Override
		public void consumeAndSend(MosipEventBus mosipEventBus, MessageBusAddress fromAddress,
				MessageBusAddress toAddress) {
		}
	};

	/**
	 * Sets the up.
	 */
	@Before
	public void setUp() {
		ReflectionTestUtils.setField(abisHandlerStage, "maxResults", 30);
		ReflectionTestUtils.setField(abisHandlerStage, "targetFPIR", 30);

		AbisApplicationDto dto = new AbisApplicationDto();
		dto.setCode("ABIS1");
		abisApplicationDtos.add(dto);
		
		Mockito.doNothing().when(registrationStatusDto).setLatestTransactionStatusCode(any());
		Mockito.doNothing().when(registrationStatusService).updateRegistrationStatus(any());

		Mockito.when(packetInfoManager.getAbisRequestsByBioRefId(any())).thenReturn(abisRequestDtoList);
		
		AuditResponseDto auditResponseDto = new AuditResponseDto();
		ResponseWrapper<AuditResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(auditResponseDto);
		Mockito.when(auditLogRequestBuilder.createAuditRequestBuilder(any(), any(), any(), any(), any(), any(), any()))
				.thenReturn(responseWrapper);
	}

	/**
	 * Test deploy verticle.
	 */
	@Test
	public void testDeployVerticle() {
		abisHandlerStage.deployVerticle();
	}

	/**
	 * Test demo to abis handler TO middleware success.
	 */
	@Test
	public void testDemoToAbisHandlerTOMiddlewareSuccess() {
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(registrationStatusDto.getLatestTransactionTypeCode()).thenReturn("DEMOGRAPHIC_VERIFICATION");
		Mockito.when(registrationStatusDto.getLatestRegistrationTransactionId())
				.thenReturn("dd7b7d20-910a-4b84-be21-c9f211318563");
		Mockito.when(packetInfoManager.getIdentifyByTransactionId(any(), any())).thenReturn(Boolean.FALSE);
		Mockito.when(packetInfoManager.getAllAbisDetails()).thenReturn(abisApplicationDtos);

		Mockito.when(packetInfoManager.getBioRefIdByRegId(any())).thenReturn(bioRefDtos);

		Mockito.doNothing().when(packetInfoManager).saveBioRef(any());
		Mockito.doNothing().when(packetInfoManager).saveAbisRequest(any());

		RegDemoDedupeListDto regDemoDedupeListDto = new RegDemoDedupeListDto();
		regDemoDedupeListDto.setMatchedRegId("10003100030001520190422074511");
		regDemoDedupeListDtoList.add(regDemoDedupeListDto);
		Mockito.when(packetInfoManager.getDemoListByTransactionId(any())).thenReturn(regDemoDedupeListDtoList);

		MessageDTO dto = new MessageDTO();
		dto.setRid("10003100030001520190422074511");
		MessageDTO result = abisHandlerStage.process(dto);

		assertTrue(result.getMessageBusAddress().getAddress().equalsIgnoreCase("abis-middle-ware-bus-in"));
	}

	/**
	 * Test bio to abis handler to middleware success.
	 */
	@Test
	public void testBioToAbisHandlerToMiddlewareSuccess() {
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(registrationStatusDto.getLatestTransactionTypeCode()).thenReturn("DEMOGRAPHIC_VERIFICATION");
		Mockito.when(registrationStatusDto.getLatestRegistrationTransactionId())
				.thenReturn("dd7b7d20-910a-4b84-be21-c9f211318563");
		Mockito.when(packetInfoManager.getIdentifyByTransactionId(any(), any())).thenReturn(Boolean.FALSE);
		Mockito.when(packetInfoManager.getAllAbisDetails()).thenReturn(abisApplicationDtos);

		RegBioRefDto regBioRefDto = new RegBioRefDto();
		regBioRefDto.setBioRefId("1234567890");
		bioRefDtos.add(regBioRefDto);
		Mockito.when(packetInfoManager.getBioRefIdByRegId(any())).thenReturn(bioRefDtos);

		Mockito.doNothing().when(packetInfoManager).saveBioRef(any());
		Mockito.doNothing().when(packetInfoManager).saveAbisRequest(any());

		RegDemoDedupeListDto regDemoDedupeListDto = new RegDemoDedupeListDto();
		regDemoDedupeListDto.setMatchedRegId("10003100030001520190422074511");
		regDemoDedupeListDtoList.add(regDemoDedupeListDto);
		Mockito.when(packetInfoManager.getDemoListByTransactionId(any())).thenReturn(regDemoDedupeListDtoList);

		MessageDTO dto = new MessageDTO();
		dto.setRid("10003100030001520190422074511");
		MessageDTO result = abisHandlerStage.process(dto);

		assertTrue(result.getMessageBusAddress().getAddress().equalsIgnoreCase("abis-middle-ware-bus-in"));
	}
	
	/**
	 * Test middleware to abis handler to demo success.
	 */
	@Test
	public void testMiddlewareToAbisHandlerToDemoSuccess() {
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(registrationStatusDto.getLatestTransactionTypeCode()).thenReturn("DEMOGRAPHIC_VERIFICATION");
		Mockito.when(registrationStatusDto.getLatestRegistrationTransactionId())
				.thenReturn("dd7b7d20-910a-4b84-be21-c9f211318563");
		Mockito.when(packetInfoManager.getIdentifyByTransactionId(any(), any())).thenReturn(Boolean.TRUE);
		
		MessageDTO dto = new MessageDTO();
		dto.setRid("10003100030001520190422074511");
		MessageDTO result = abisHandlerStage.process(dto);

		assertTrue(result.getMessageBusAddress().getAddress().equalsIgnoreCase("demo-dedupe-bus-in"));
	}
	
	/**
	 * Test middleware to abis handler to bio success.
	 */
	@Test
	public void testMiddlewareToAbisHandlerToBioSuccess() {
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(registrationStatusDto.getLatestTransactionTypeCode()).thenReturn("BIOGRAPHIC_VERIFICATION");
		Mockito.when(registrationStatusDto.getLatestRegistrationTransactionId())
				.thenReturn("dd7b7d20-910a-4b84-be21-c9f211318563");
		Mockito.when(packetInfoManager.getIdentifyByTransactionId(any(), any())).thenReturn(Boolean.TRUE);
		
		MessageDTO dto = new MessageDTO();
		dto.setRid("10003100030001520190422074511");
		MessageDTO result = abisHandlerStage.process(dto);

		assertTrue(result.getMessageBusAddress().getAddress().equalsIgnoreCase("bio-dedupe-bus-in"));
	}
	
	/**
	 * Test demo dedupe data not found.
	 */
	@Test
	public void testDemoDedupeDataNotFound() {
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(registrationStatusDto.getLatestTransactionTypeCode()).thenReturn("DEMOGRAPHIC_VERIFICATION");
		Mockito.when(registrationStatusDto.getLatestRegistrationTransactionId())
				.thenReturn("dd7b7d20-910a-4b84-be21-c9f211318563");
		Mockito.when(packetInfoManager.getIdentifyByTransactionId(any(), any())).thenReturn(Boolean.FALSE);
		Mockito.when(packetInfoManager.getAllAbisDetails()).thenReturn(abisApplicationDtos);

		Mockito.when(packetInfoManager.getBioRefIdByRegId(any())).thenReturn(bioRefDtos);

		Mockito.doNothing().when(packetInfoManager).saveBioRef(any());
		Mockito.doNothing().when(packetInfoManager).saveAbisRequest(any());

		Mockito.when(packetInfoManager.getDemoListByTransactionId(any())).thenReturn(regDemoDedupeListDtoList);

		MessageDTO dto = new MessageDTO();
		dto.setRid("10003100030001520190422074511");
		MessageDTO result = abisHandlerStage.process(dto);

		assertTrue(result.getInternalError());
		
	}
	
	@Test
	public void testReprocessInsert() {
		Mockito.when(registrationStatusService.getRegistrationStatus(any())).thenReturn(registrationStatusDto);
		Mockito.when(registrationStatusDto.getLatestTransactionTypeCode()).thenReturn("BIOGRAPHIC_VERIFICATION");
		Mockito.when(registrationStatusDto.getLatestRegistrationTransactionId())
				.thenReturn("dd7b7d20-910a-4b84-be21-c9f211318563");
		Mockito.when(packetInfoManager.getIdentifyByTransactionId(any(), any())).thenReturn(Boolean.FALSE);
		Mockito.when(packetInfoManager.getAllAbisDetails()).thenReturn(abisApplicationDtos);
		
		RegBioRefDto bioRefDto = new RegBioRefDto();
		bioRefDtos.add(bioRefDto);
		Mockito.when(packetInfoManager.getBioRefIdByRegId(any())).thenReturn(bioRefDtos);

		Mockito.doNothing().when(packetInfoManager).saveBioRef(any());

		AbisRequestDto abisRequestDto = new AbisRequestDto();
		abisRequestDto.setAbisAppCode("ABIS1");
		abisRequestDto.setStatusCode("IN-PROGRESS");
		abisRequestDtoList.add(abisRequestDto);
		Mockito.when(packetInfoManager.getAbisRequestsByBioRefId(any())).thenReturn(abisRequestDtoList);

		Mockito.doNothing().when(packetInfoManager).saveAbisRequest(any());

		Mockito.when(packetInfoManager.getDemoListByTransactionId(any())).thenReturn(regDemoDedupeListDtoList);

		MessageDTO dto = new MessageDTO();
		dto.setRid("10003100030001520190422074511");
		MessageDTO result = abisHandlerStage.process(dto);

		assertTrue(result.getMessageBusAddress().getAddress().equalsIgnoreCase("abis-middle-ware-bus-in"));
	}

}
