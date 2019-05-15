package io.mosip.registartion.processor.abis.middleware.stage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dao.RegistrationStatusDao;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import io.mosip.registration.processor.status.service.RegistrationStatusService;
import io.vertx.core.Vertx;

@RunWith(MockitoJUnitRunner.class)
public class AbisMiddleWareStageTest {

	@Mock
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;

	@Mock
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Mock
	private BasePacketRepository<AbisRequestEntity, String> abisRequestRepositary;

	@Mock
	private BasePacketRepository<AbisResponseEntity, String> abisResponseRepositary;

	@Mock
	private BasePacketRepository<AbisResponseDetEntity, String> abisResponseDetailRepositary;

	@Mock
	private Utilities utility;

	@Mock
	private RegistrationStatusDao registrationStatusDao;

	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	/** The core audit request builder. */
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	private RegistrationStatusEntity regStatusEntity;
	private List<String> abisRefList;
	private List<AbisRequestDto> abisInsertIdentifyList;
	private AbisRequestDto identifyAbisReq;
	private List<MosipQueue> mosipQueueList;

	@InjectMocks
	AbisMiddleWareStage stage = new AbisMiddleWareStage() {

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

		@Override
		public void consume(MosipEventBus mosipEventBus, MessageBusAddress fromAddress) {

		}

	};

	@Before
	public void setUp() throws RegistrationProcessorCheckedException {
		InternalRegistrationStatusDto internalRegStatusDto = new InternalRegistrationStatusDto();
		internalRegStatusDto.setRegistrationId("");
		internalRegStatusDto.setLatestTransactionStatusCode("Demodedupe");
		Mockito.when(registrationStatusService.getRegistrationStatus(Mockito.anyString())).thenReturn(internalRegStatusDto);
		
		regStatusEntity = new RegistrationStatusEntity();
		regStatusEntity.setLatestRegistrationTransactionId("1234");
		Mockito.when(registrationStatusDao.findById(Mockito.anyString())).thenReturn(regStatusEntity);
		
		abisRefList = new ArrayList<>();
		abisRefList.add("88");
		Mockito.when(packetInfoManager.getReferenceIdByRid(Mockito.anyString())).thenReturn(abisRefList);

		abisInsertIdentifyList = new ArrayList<>();
		AbisRequestDto insertAbisReq = new AbisRequestDto();
		insertAbisReq.setRefRegtrnId("1234");
		insertAbisReq.setAbisAppCode("code1");
		insertAbisReq.setBioRefId("bio ref id");
		insertAbisReq.setRequestType("INSERT");
		insertAbisReq.setId("12345");
		insertAbisReq.setReqText("mosip".getBytes());

		AbisRequestDto identifyAbisReq = new AbisRequestDto();
		identifyAbisReq.setRefRegtrnId("1234");
		identifyAbisReq.setAbisAppCode("code1");
		identifyAbisReq.setBioRefId("bio ref id");
		identifyAbisReq.setRequestType("IDENTIFY");
		insertAbisReq.setId("123456");
		insertAbisReq.setReqText("mosip".getBytes());
		abisInsertIdentifyList.add(insertAbisReq);
		abisInsertIdentifyList.add(identifyAbisReq);

		mosipQueueList = new ArrayList<>();
		MosipQueue queue1 = new MosipQueue() {
			@Override
			public String getQueueName() {
				// TODO Auto-generated method stub
				return "code1";
			}

			@Override
			public void createConnection(String username, String password, String brokerUrl) {
				// TODO Auto-generated method stub

			}
		};
		mosipQueueList.add(queue1);

		List<String> abisInboundAddresses = new ArrayList<>();
		abisInboundAddresses.add("abis1-inbound-address");

		List<String> abisOutboundAddresses = new ArrayList<>();
		abisOutboundAddresses.add("abis1-outboundaddress");
		List<List<String>> abisInboundOutBounAddressList = new ArrayList<>();
		abisInboundOutBounAddressList.add(abisInboundAddresses);
		abisInboundOutBounAddressList.add(abisOutboundAddresses);

		Mockito.when(utility.getInboundOutBoundAddressList()).thenReturn(abisInboundOutBounAddressList);

	}

	@Test
	public void processTest() throws RegistrationProcessorCheckedException {

		Mockito.when(utility.getMosipQueuesForAbis()).thenReturn(mosipQueueList);
		Mockito.when(packetInfoManager.getInsertOrIdentifyRequest(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(abisInsertIdentifyList);

		Mockito.when(mosipQueueManager.send(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
		MessageDTO dto = new MessageDTO();
		dto.setRid("10003100030001520190422074511");
		

		stage.deployVerticle();
		stage.process(dto);
		assertTrue(dto.getIsValid());

	}
	
	@Test
	public void testemptyAbisRefListAndTransId() throws RegistrationProcessorCheckedException {
		Mockito.when(utility.getMosipQueuesForAbis()).thenReturn(mosipQueueList);
		Mockito.when(packetInfoManager.getInsertOrIdentifyRequest(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(abisInsertIdentifyList);

		Mockito.when(mosipQueueManager.send(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
		MessageDTO dto = new MessageDTO();
		dto.setRid("10003100030001520190422074511");
		Mockito.when(packetInfoManager.getReferenceIdByRid(Mockito.anyString())).thenReturn(null);

		stage.deployVerticle();
		stage.process(dto);
		assertFalse(dto.getIsValid());
		
		stage.process(dto);
		assertFalse(dto.getIsValid());
		
		
		
	}

}
