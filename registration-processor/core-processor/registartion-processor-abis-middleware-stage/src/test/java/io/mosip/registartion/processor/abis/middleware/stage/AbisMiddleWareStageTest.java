package io.mosip.registartion.processor.abis.middleware.stage;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utilities.class })
@PowerMockIgnore({ "javax.management.*", "javax.net.ssl.*" })
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
	private RegistrationStatusDao registrationStatusDao;

	/** The core audit request builder. */
	@Mock
	private AuditLogRequestBuilder auditLogRequestBuilder;

	@Mock
	private Utilities utility;

	@Mock
	private RegistrationStatusService<String, InternalRegistrationStatusDto, RegistrationStatusDto> registrationStatusService;

	@InjectMocks
	private AbisMiddleWareStage stage = new AbisMiddleWareStage() {
		@Override
		public MosipEventBus getEventBus(Object verticleName, String url, int instanceNumber) {
			vertx = Vertx.vertx();

			return new MosipEventBus(vertx) {
			};
		}

		@Override
		public void consume(MosipEventBus mosipEventBus, MessageBusAddress fromAddress) {
		}

		@Override
		public void send(MosipEventBus mosipEventBus, MessageBusAddress toAddress, MessageDTO message) {
		}
	};

	@Test
	public void testDeployVerticle() throws RegistrationProcessorCheckedException {
		List<MosipQueue> mosipQueueList = new ArrayList<>();
		MosipQueue queue = new MosipQueue() {

			@Override
			public String getQueueName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void createConnection(String username, String password, String brokerUrl) {
				// TODO Auto-generated method stub

			}
		};
		Mockito.when(utility.getMosipQueuesForAbis()).thenReturn(mosipQueueList);

	}

	@Test
	public void processTest() {
		InternalRegistrationStatusDto regDto = new InternalRegistrationStatusDto();
		regDto.setRegistrationId("");
		regDto.setLatestTransactionStatusCode("Demodedupe");
		Mockito.when(registrationStatusService.getRegistrationStatus(anyString())).thenReturn(regDto);
		List<String> abisRefList = new ArrayList<>();
		abisRefList.add("88");

		Mockito.when(packetInfoManager.getReferenceIdByRid(anyString())).thenReturn(abisRefList);
		RegistrationStatusEntity regStatusEntity = new RegistrationStatusEntity();
		regStatusEntity.setLatestRegistrationTransactionId("1234");
		Mockito.when(registrationStatusDao.findById(anyString())).thenReturn(regStatusEntity);
		List<AbisRequestDto> abisInsertIdentifyList = new ArrayList<>();
		AbisRequestDto insertAbisReq = new AbisRequestDto();
		insertAbisReq.setRefRegtrnId("1234");
		insertAbisReq.setAbisAppCode("code1");
		insertAbisReq.setBioRefId("bio ref id");
		insertAbisReq.setRequestType("INSERT");
		insertAbisReq.setId("12345");

		AbisRequestDto identifyAbisReq = new AbisRequestDto();
		identifyAbisReq.setRefRegtrnId("1234");
		identifyAbisReq.setAbisAppCode("code1");
		identifyAbisReq.setBioRefId("bio ref id");
		identifyAbisReq.setRequestType("IDENTIFY");
		insertAbisReq.setId("123456");
		abisInsertIdentifyList.add(insertAbisReq);
		abisInsertIdentifyList.add(identifyAbisReq);

		Mockito.when(packetInfoManager.getInsertOrIdentifyRequest(anyString(), anyString()))
				.thenReturn(abisInsertIdentifyList);

		Mockito.when(mosipQueueManager.send(any(), any(), any())).thenReturn(true);
	}

}
