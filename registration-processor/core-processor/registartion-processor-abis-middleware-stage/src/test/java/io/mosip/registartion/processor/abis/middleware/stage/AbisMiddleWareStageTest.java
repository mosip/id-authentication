package io.mosip.registartion.processor.abis.middleware.stage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.util.ByteSequence;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import io.mosip.registration.processor.abis.queue.dto.AbisQueueDetails;
import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseEntity;
import io.mosip.registration.processor.packet.storage.exception.TablenotAccessibleException;
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

	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Mock
	private PacketInfoDao packetInfoDao;

	private RegistrationStatusEntity regStatusEntity;
	private List<String> abisRefList;
	private List<AbisRequestDto> abisInsertIdentifyList;
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
		Mockito.when(registrationStatusService.getRegistrationStatus(Mockito.anyString()))
				.thenReturn(internalRegStatusDto);

		regStatusEntity = new RegistrationStatusEntity();
		regStatusEntity.setLatestRegistrationTransactionId("1234");
		Mockito.when(registrationStatusDao.findById(Mockito.anyString())).thenReturn(regStatusEntity);

		abisRefList = new ArrayList<>();
		abisRefList.add("88");
		Mockito.when(packetInfoManager.getReferenceIdByRid(Mockito.anyString())).thenReturn(abisRefList);

		abisInsertIdentifyList = new ArrayList<>();
		AbisRequestDto insertAbisReq = new AbisRequestDto();
		insertAbisReq.setRefRegtrnId("10001100010027120190430071052");
		insertAbisReq.setAbisAppCode("Abis1");
		insertAbisReq.setBioRefId("d1070375-0960-4e90-b12c-72ab6186444d");
		insertAbisReq.setRequestType("INSERT");
		insertAbisReq.setId("f4b1f6fd-466c-462f-aa8b-c218596542ec");
		insertAbisReq.setStatusCode("IN_PROGRESS");
		insertAbisReq.setReqText("mosip".getBytes());

		AbisRequestDto insertAlreadyProcessed = new AbisRequestDto();
		insertAlreadyProcessed.setRefRegtrnId("de7c4893-bf6f-46b4-a4d5-5cd458d5c7e2");
		insertAlreadyProcessed.setAbisAppCode("Abis2");
		insertAlreadyProcessed.setBioRefId("d1070375-0960-4e90-b12c-72ab6186444d");
		insertAlreadyProcessed.setRequestType("INSERT");
		insertAlreadyProcessed.setId("f4b1f6fd-466c-462f-aa8b-c218596542ed");
		insertAlreadyProcessed.setStatusCode("ALREADY_PROCESSED");
		insertAlreadyProcessed.setReqText("mosip".getBytes());

		AbisRequestDto identifyAbisReq = new AbisRequestDto();
		identifyAbisReq.setRefRegtrnId("de7c4893-bf6f-46b4-a4d5-5cd458d5c7e2");
		identifyAbisReq.setAbisAppCode("Abis1");
		identifyAbisReq.setBioRefId("d1070375-0960-4e90-b12c-72ab6186444d");
		identifyAbisReq.setRequestType("IDENTIFY");
		insertAbisReq.setId("f4b1f6fd-466c-462f-aa8b-c218596542ee");
		insertAbisReq.setReqText("mosip".getBytes());

		AbisRequestDto identifyAbisReq1 = new AbisRequestDto();
		identifyAbisReq1.setRefRegtrnId("de7c4893-bf6f-46b4-a4d5-5cd458d5c7e2");
		identifyAbisReq1.setAbisAppCode("Abis2");
		identifyAbisReq1.setBioRefId("d1070375-0960-4e90-b12c-72ab6186444d");
		identifyAbisReq1.setRequestType("IDENTIFY");
		identifyAbisReq1.setId("f4b1f6fd-466c-462f-aa8b-c218596542ef");
		identifyAbisReq1.setReqText("mosip".getBytes());

		abisInsertIdentifyList.add(insertAbisReq);
		abisInsertIdentifyList.add(identifyAbisReq);
		abisInsertIdentifyList.add(identifyAbisReq1);
		abisInsertIdentifyList.add(insertAlreadyProcessed);

		mosipQueueList = new ArrayList<>();
		MosipQueue queue1 = new MosipQueue() {
			@Override
			public String getQueueName() {
				// TODO Auto-generated method stub
				return "Abis1";
			}

			@Override
			public void createConnection(String username, String password, String brokerUrl) {
				// TODO Auto-generated method stub

			}
		};

		MosipQueue queue2 = new MosipQueue() {
			@Override
			public String getQueueName() {
				// TODO Auto-generated method stub
				return "Abis2";
			}

			@Override
			public void createConnection(String username, String password, String brokerUrl) {
				// TODO Auto-generated method stub

			}
		};
		mosipQueueList.add(queue1);
		mosipQueueList.add(queue2);

		List<String> abisInboundAddresses = new ArrayList<>();
		abisInboundAddresses.add("abis1-inbound-address");

		List<String> abisOutboundAddresses = new ArrayList<>();
		abisOutboundAddresses.add("abis1-outboundaddress");
		List<List<String>> abisInboundOutBounAddressList = new ArrayList<>();
		abisInboundOutBounAddressList.add(abisInboundAddresses);
		abisInboundOutBounAddressList.add(abisOutboundAddresses);

		AbisQueueDetails abisQueue = new AbisQueueDetails();
		abisQueue.setMosipQueue(queue1);
		abisQueue.setInboundQueueName("abis1-inbound-Queue");
		abisQueue.setOutboundQueueName("abis1-outbound-Queue");
		abisQueue.setName("Abis1");

		AbisQueueDetails abisQueue1 = new AbisQueueDetails();
		abisQueue1.setMosipQueue(queue2);
		abisQueue1.setInboundQueueName("abis2-inbound-Queue");
		abisQueue1.setOutboundQueueName("abis2-outbound-Queue");
		abisQueue1.setName("Abis2");
		List<AbisQueueDetails> abisQueueList = new ArrayList<>();
		abisQueueList.add(abisQueue);
		abisQueueList.add(abisQueue1);

		Mockito.when(utility.getAbisQueueDetails()).thenReturn(abisQueueList);
		Mockito.when(packetInfoManager.getBatchIdByRequestId(Mockito.anyString()))
				.thenReturn("69098823-eba8-4aa9-bb64-9e0d36bd64a9");
		List<String> bioRefId = new ArrayList<>();
		bioRefId.add("d1070375-0960-4e90-b12c-72ab6186444d");
		Mockito.when(packetInfoManager.getReferenceIdByBatchId(Mockito.anyString())).thenReturn(bioRefId);
		List<String> abisMatchedRefIds = new ArrayList<>();
		abisMatchedRefIds.add("d1070375-0960-4e90-b12c-72ab6186444d");
		Mockito.when(packetInfoDao.getAbisRefRegIdsByMatchedRefIds(Mockito.any())).thenReturn(abisMatchedRefIds);
		List<String> transIdList = new ArrayList<>();
		transIdList.add("1234");
		Mockito.when(packetInfoManager.getAbisTransactionIdByRequestId(Mockito.anyString())).thenReturn(transIdList);
		//
		// packetInfoManager.getIdentifyReqListByTransactionId(
		// transactionIdList.get(0), AbisStatusCode.IDENTIFY.toString())
		List<AbisRequestDto> abisIdentifyRequestDtoList = new ArrayList<>();
		AbisRequestDto abisIdentifyRequestDto = new AbisRequestDto();
		abisIdentifyRequestDto.setAbisAppCode("Abis1");
		abisIdentifyRequestDto.setBioRefId("d1070375-0960-4e90-b12c-72ab6186444d");
		abisIdentifyRequestDto.setId("98509c18-ff22-46b7-a8c7-b4dec1d00c85");
		abisIdentifyRequestDto.setReqText("mosip".getBytes());
		abisIdentifyRequestDto.setReqBatchId("d87e6e28-4234-4433-b45d-0313c2aeca01");
		abisIdentifyRequestDtoList.add(abisIdentifyRequestDto);
		Mockito.when(packetInfoManager.getIdentifyReqListByTransactionId(Mockito.any(), Mockito.any()))
				.thenReturn(abisIdentifyRequestDtoList);

		// Mockito.when(utility.getInboundOutBoundAddressList()).thenReturn(abisInboundOutBounAddressList);

	}

	@Test
	public void processTest() throws RegistrationProcessorCheckedException {

		Mockito.when(packetInfoManager.getInsertOrIdentifyRequest(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(abisInsertIdentifyList);

		Mockito.when(mosipQueueManager.send(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
		MessageDTO dto = new MessageDTO();
		dto.setRid("10003100030001520190422074511");

		stage.deployVerticle();
		stage.process(dto);
		assertTrue(dto.getIsValid());

		// test for insert request list is empty
		List<AbisRequestDto> abisInsertIdentifyList = new ArrayList<>();
		AbisRequestDto identifyAbisReq = new AbisRequestDto();
		identifyAbisReq.setRefRegtrnId("d87e6e28-4234-4433-b45d-0313c2aeca01");
		identifyAbisReq.setAbisAppCode("Abis1");
		identifyAbisReq.setBioRefId("d1070375-0960-4e90-b12c-72ab6186444d");
		identifyAbisReq.setRequestType("IDENTIFY");
		identifyAbisReq.setReqText("mosip".getBytes());
		abisInsertIdentifyList.add(identifyAbisReq);
		Mockito.when(packetInfoManager.getInsertOrIdentifyRequest(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(abisInsertIdentifyList);
		stage.process(dto);
		assertTrue(dto.getIsValid());

	}

	@Test
	public void testVariousScenarious() throws RegistrationProcessorCheckedException {
		// Mockito.when(utility.getMosipQueuesForAbis()).thenReturn(mosipQueueList);
		Mockito.when(packetInfoManager.getInsertOrIdentifyRequest(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(abisInsertIdentifyList);
		Mockito.when(mosipQueueManager.send(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
		MessageDTO dto = new MessageDTO();
		dto.setRid("10003100030001520190422074511");
		Mockito.when(packetInfoManager.getReferenceIdByRid(Mockito.anyString())).thenReturn(null);
		stage.deployVerticle();
		stage.process(dto);
		assertFalse(dto.getIsValid());

		// test for null transactionId
		Mockito.when(registrationStatusDao.findById(Mockito.anyString())).thenReturn(null);
		Mockito.when(packetInfoManager.getReferenceIdByRid(Mockito.anyString())).thenReturn(abisRefList);
		stage.process(dto);
		assertFalse(dto.getIsValid());

		// test for empty insertidentify request List
		Mockito.when(registrationStatusDao.findById(Mockito.anyString())).thenReturn(regStatusEntity);
		Mockito.when(packetInfoManager.getInsertOrIdentifyRequest(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(new ArrayList<AbisRequestDto>());
		Mockito.when(packetInfoManager.getReferenceIdByRid(Mockito.anyString())).thenReturn(abisRefList);
		stage.process(dto);
		assertFalse(dto.getIsValid());

		// test for send to queue failed
		Mockito.when(registrationStatusDao.findById(Mockito.anyString())).thenReturn(regStatusEntity);
		Mockito.when(packetInfoManager.getInsertOrIdentifyRequest(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(abisInsertIdentifyList);
		Mockito.when(packetInfoManager.getReferenceIdByRid(Mockito.anyString())).thenReturn(abisRefList);
		Mockito.when(mosipQueueManager.send(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(false);
		stage.process(dto);

		// test for exception while sending to queue
		Mockito.when(registrationStatusDao.findById(Mockito.anyString())).thenReturn(regStatusEntity);
		Mockito.when(packetInfoManager.getInsertOrIdentifyRequest(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(abisInsertIdentifyList);
		Mockito.when(packetInfoManager.getReferenceIdByRid(Mockito.anyString())).thenReturn(abisRefList);
		Mockito.when(mosipQueueManager.send(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenThrow(new NullPointerException());
		stage.process(dto);
		assertFalse(dto.getIsValid());

	}

	// test for unknown exception occured
	@Test
	public void testException() {
		Mockito.when(packetInfoManager.getInsertOrIdentifyRequest(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(abisInsertIdentifyList);
		Mockito.when(packetInfoManager.getReferenceIdByRid(Mockito.anyString())).thenReturn(abisRefList);
		Mockito.when(mosipQueueManager.send(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
		Mockito.when(registrationStatusDao.findById(Mockito.anyString())).thenThrow(new TablenotAccessibleException());
		MessageDTO dto = new MessageDTO();
		dto.setRid("10003100030001520190422074511");
		stage.process(dto);
		assertFalse(dto.getIsValid());
	}

	@Test
	public void testConsumerListener() throws RegistrationProcessorCheckedException {
		String failedInsertResponse = "{\"id\":\"mosip.abis.insert\",\"requestId\":\"5b64e806-8d5f-4ba1-b641-0b55cf40c0e1\",\"timestamp\":\"1558001992\",\"returnValue\":2,\"failureReason\":7}\r\n"
				+ "";
		ActiveMQBytesMessage amq = new ActiveMQBytesMessage();
		ByteSequence byteSeq = new ByteSequence();
		byteSeq.setData(failedInsertResponse.getBytes());
		amq.setContent(byteSeq);
		Vertx vertx = Mockito.mock(Vertx.class);
		MosipEventBus evenBus = new MosipEventBus(vertx);
		MosipQueue queue = Mockito.mock(MosipQueue.class);
		AbisRequestDto abisCommonRequestDto = new AbisRequestDto();
		abisCommonRequestDto.setRequestType("INSERT");
		Mockito.when(packetInfoManager.getAbisRequestByRequestId(Mockito.any())).thenReturn(abisCommonRequestDto);
		stage.consumerListener(amq, "abis1_inboundAddress", queue, evenBus);

		String sucessfulResponse = "{\"id\":\"mosip.abis.insert\",\"requestId\":\"5b64e806-8d5f-4ba1-b641-0b55cf40c0e1\",\"timestamp\":\"1558001992\",\"returnValue\":1,\"failureReason\":null}\r\n"
				+ "";
		byteSeq.setData(sucessfulResponse.getBytes());
		amq.setContent(byteSeq);
		AbisRequestDto abisCommonRequestDto1 = new AbisRequestDto();
		abisCommonRequestDto1.setRequestType("INSERT");
		abisCommonRequestDto1.setAbisAppCode("Abis1");
		Mockito.when(packetInfoManager.getAbisRequestByRequestId(Mockito.any())).thenReturn(abisCommonRequestDto1);
		stage.consumerListener(amq, "abis1_inboundAddress", queue, evenBus);

	}

	@Test
	public void batchIdNull() throws RegistrationProcessorCheckedException {
		String sucessfulResponse = "{\"id\":\"mosip.abis.insert\",\"requestId\":\"5b64e806-8d5f-4ba1-b641-0b55cf40c0e1\",\"timestamp\":\"1558001992\",\"returnValue\":1,\"failureReason\":null}\r\n"
				+ "";
		ActiveMQBytesMessage amq1 = new ActiveMQBytesMessage();
		ByteSequence byteSeq1 = new ByteSequence();
		byteSeq1.setData(sucessfulResponse.getBytes());
		amq1.setContent(byteSeq1);
		Vertx vertx1 = Mockito.mock(Vertx.class);
		MosipEventBus eventBus1 = new MosipEventBus(vertx1);
		MosipQueue queue1 = Mockito.mock(MosipQueue.class);
		AbisRequestDto abisCommonRequestDto1 = new AbisRequestDto();
		abisCommonRequestDto1.setRequestType("INSERT");
		abisCommonRequestDto1.setAbisAppCode("Abis1");
		Mockito.when(packetInfoManager.getAbisRequestByRequestId(Mockito.any())).thenReturn(abisCommonRequestDto1);
		Mockito.when(packetInfoManager.getBatchIdByRequestId(Mockito.anyString())).thenReturn(null);
		stage.consumerListener(amq1, "abis1_inboundAddress", queue1, eventBus1);

	}

	//
	@Test
	public void testIdentifyConsumerListener() throws RegistrationProcessorCheckedException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		Mockito.when(packetInfoManager.getBatchStatusbyBatchId(Mockito.anyString())).thenReturn(null);

		// test for identify succes response - no duplicates
		String identifySucessfulResponse = "{\"id\":\"mosip.abis.identify\",\"requestId\":\"8a3effd4-5fba-44e0-8cbb-3083ba098209\",\"timestamp\":\"1558001992\",\"returnValue\":1,\"failureReason\":null,\"candidateList\":null}";
		ActiveMQBytesMessage amq1 = new ActiveMQBytesMessage();
		ByteSequence byteSeq1 = new ByteSequence();
		byteSeq1.setData(identifySucessfulResponse.getBytes());
		amq1.setContent(byteSeq1);
		Vertx vertx1 = Mockito.mock(Vertx.class);
		MosipEventBus evenBus1 = new MosipEventBus(vertx1);
		MosipQueue queue1 = Mockito.mock(MosipQueue.class);
		AbisRequestDto abisCommonRequestDto1 = new AbisRequestDto();
		abisCommonRequestDto1.setRequestType("IDENTIFY");
		Mockito.when(packetInfoManager.getAbisRequestByRequestId(Mockito.any())).thenReturn(abisCommonRequestDto1);
		stage.consumerListener(amq1, "abis1_inboundAddress", queue1, evenBus1);
		// test for identify failed response
		String identifyFailedResponse = "{\"id\":\"mosip.abis.identify\",\"requestId\":\"8a3effd4-5fba-44e0-8cbb-3083ba098209\",\"timestamp\":\"1558001992\",\"returnValue\":2,\"failureReason\":3,\"candidateList\":null}";
		byteSeq1.setData(identifyFailedResponse.getBytes());
		amq1.setContent(byteSeq1);
		stage.consumerListener(amq1, "abis1_inboundAddress", queue1, evenBus1);
		// test for identify response - with duplicates
		String duplicateIdentifySuccessResponse = "{\"id\":\"mosip.abis.identify\",\"requestId\":\"f4b1f6fd-466c-462f-aa8b-c218596542ec\",\"timestamp\":\"1558413054\",\"returnValue\":1,\"failureReason\":null,\"candidateList\":{\"count\":\"1\",\"candidates\":[{\"referenceId\":\"d1070375-0960-4e90-b12c-72ab6186444d\",\"scaledScore\":\"100\",\"internalScore\":null,\"analytics\":null,\"scores\":null}]}}";
		byteSeq1.setData(duplicateIdentifySuccessResponse.getBytes());
		amq1.setContent(byteSeq1);
		stage.consumerListener(amq1, "abis1_inboundAddress", queue1, evenBus1);

	}
}