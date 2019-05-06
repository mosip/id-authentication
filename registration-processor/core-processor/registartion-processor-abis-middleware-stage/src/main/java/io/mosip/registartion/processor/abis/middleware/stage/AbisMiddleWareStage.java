package io.mosip.registartion.processor.abis.middleware.stage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Message;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.abis.AbisCommonRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisResponseDto;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.queue.factory.QueueListener;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisRequestPKEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseDetEntity;
import io.mosip.registration.processor.packet.storage.entity.AbisResponseEntity;
import io.mosip.registration.processor.packet.storage.repository.BasePacketRepository;

public class AbisMiddleWareStage extends MosipVerticleManager {

	/** The mosip connection factory. */
	@Autowired
	private MosipQueueConnectionFactory<MosipQueue> mosipConnectionFactory;
	/** The mosip queue manager. */
	@Autowired
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

	@Autowired
	private BasePacketRepository<AbisRequestEntity, String> abisRequestRepositary;

	private MosipQueue queue;
	private MosipEventBus mosipEventBus;

	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;
	/** The type of queue. */
	@Value("${registration.processor.queue.typeOfQueue}")
	private String typeOfQueue;
	/** The username. */
	@Value("${registration.processor.queue.username}")
	private String username;

	/** The password. */
	@Value("${registration.processor.queue.password}")
	private String password;

	@Value("${registration.processor.abis.inbound.queue1}")
	private String abisInboundAddress1;
	@Value("${registration.processor.abis.inbound.queue2}")
	private String abisInboundAddress2;
	@Value("${registration.processor.abis.inbound.queue3}")
	private String abisInboundAddress3;

	@Value("${registration.processor.abis.outbound.queue1}")
	private String abisOutboundAddress1;
	@Value("${registration.processor.abis.outbound.queue1}")
	private String abisOutboundAddress2;
	@Value("${registration.processor.abis.outbound.queue1}")
	private String abisOutboundAddress3;

	/** The url. */
	@Value("${registration.processor.queue.url}")
	private String url;
	
	private static final String INSERT = "INSERT";
	private static final String IDENTIFY = "IDENTIFY";


	public void deployVerticle() {
		queue = getQueueConnection();
		if (queue != null) {

			QueueListener listener = new QueueListener() {
				@Override
				public void setListener(Message message) {
					cosnumerListener(message);
				}
			};

			mosipQueueManager.consume(queue, abisOutboundAddress1, listener);
			mosipQueueManager.consume(queue, abisOutboundAddress2, listener);
			mosipQueueManager.consume(queue, abisOutboundAddress3, listener);
			mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
			this.consumeAndSend(mosipEventBus, MessageBusAddress.ABIS_MIDDLEWARE_BUS_IN,
					MessageBusAddress.ABIS_MIDDLEWARE_BUS_OUT);
		}
	}

	@Override
	public MessageDTO process(MessageDTO object) {
		List<String> abisInboundAddresses = new ArrayList<>();
		abisInboundAddresses.add(abisInboundAddress1);
		abisInboundAddresses.add(abisInboundAddress2);
		abisInboundAddresses.add(abisInboundAddress3);

		List<String> abisOutboundAddresses = new ArrayList<>();
		abisOutboundAddresses.add(abisOutboundAddress1);
		abisOutboundAddresses.add(abisOutboundAddress2);
		abisOutboundAddresses.add(abisOutboundAddress3);

		List<String> abisRefList = packetInfoManager.getReferenceIdByRid(object.getRid());
		if (abisRefList != null && !abisRefList.isEmpty()) {
			try {
				String abisRefId = abisRefList.get(0);
				List<AbisRequestDto> abisInsertRequestList = packetInfoManager.getInsertOrIdentifyRequest(abisRefId,
						INSERT);

				for (int i = 0; i < abisInsertRequestList.size(); i++) {
					AbisInsertRequestDto abisInsertRequestDto = new AbisInsertRequestDto();
					abisInsertRequestDto.setId("mosip.abis.insert");
					abisInsertRequestDto.setVer("1.0");
					abisInsertRequestDto.setTimestamp(String.valueOf(LocalDateTime.now()));
					//abisInsertRequestDto.setReferenceId(abisRefId);
					//abisInsertRequestDto.setReferenceURL(referenceURL);
					////abisInsertRequestDto.setRequestId(requestId);
					
					boolean isAddedToQueue = sendToQueue(queue, abisInsertRequestDto,
							abisInboundAddresses.get(i));

					updateAbisRequest(isAddedToQueue, abisInsertRequestList.get(i));
				}

				// send dto to queue

				List<AbisRequestDto> abisIdentifyRequestList = packetInfoManager.getInsertOrIdentifyRequest(abisRefId,
						IDENTIFY);

				for (int i = 0; i < abisIdentifyRequestList.size(); i++) {
					AbisIdentifyRequestDto abisIdentifyRequestDto = new AbisIdentifyRequestDto();
					boolean isAddedToQueue = sendToQueue(queue, abisIdentifyRequestDto,
							abisInboundAddresses.get(i));
					updateAbisRequest(isAddedToQueue, abisInsertRequestList.get(i));

				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return null;
	}

	private MosipQueue getQueueConnection() {
		return mosipConnectionFactory.createConnection(typeOfQueue, username, password, url);
	}

	public void cosnumerListener(Message message){
		
		String response = new String(((ActiveMQBytesMessage) message).getContent().data);
		
		AbisResponseDto abisResponseDto;
		try {
			abisResponseDto = JsonUtil.objectMapperReadValue(response, AbisResponseDto.class);

		if (abisResponseDto != null) {
			//update table
			

		}
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private boolean sendToQueue(MosipQueue queue, AbisCommonRequestDto abisInsertRequestDto, String abisQueueAddress)
			throws IOException {
		boolean isAddedToQueue;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(abisInsertRequestDto);
		oos.flush();
		byte[] abisRequestDtoBytes = bos.toByteArray();
		isAddedToQueue = mosipQueueManager.send(queue, abisRequestDtoBytes, abisQueueAddress);

		return isAddedToQueue;
	}

	private void updateAbisRequest(boolean isAddedToQueue, AbisRequestDto abisRequestDto) {
		if (isAddedToQueue) {

			AbisRequestEntity abisReqEntity = convertAbisRequestDtoToAbisRequestEntity(abisRequestDto);
			abisReqEntity.setStatusCode("SENT");
			abisReqEntity.setStatusComment("Sent sucessfully to ABIS");
			abisRequestRepositary.update(abisReqEntity);
		}

	}
	
	private AbisRequestEntity convertAbisRequestDtoToAbisRequestEntity(AbisRequestDto abisRequestDto) {
		AbisRequestEntity abisReqEntity = new AbisRequestEntity();
		abisReqEntity.setAbisAppCode(abisRequestDto.getAbisAppCode());
		abisReqEntity.setBioRefId(abisRequestDto.getBioRefId());
		abisReqEntity.setCrBy(abisRequestDto.getCrBy());
		abisReqEntity.setIsDeleted(false);
		abisReqEntity.setLangCode(abisRequestDto.getLangCode());
		abisReqEntity.setRefRegtrnId(abisRequestDto.getRefRegtrnId());
		abisReqEntity.setReqBatchId(abisRequestDto.getReqBatchId());
		abisReqEntity.setReqText(abisRequestDto.getReqText());
		abisReqEntity.setRequestDtimes(abisRequestDto.getRequestDtimes());
		abisReqEntity.setRequestType(abisRequestDto.getRequestType());
		abisReqEntity.setStatusCode(abisRequestDto.getStatusCode());
		abisReqEntity.setStatusComment(abisRequestDto.getStatusComment());
		abisReqEntity.setUpdBy(abisRequestDto.getUpdBy());
		
		return abisReqEntity;
		
	}
		
	private AbisResponseEntity updateAbisResponse(boolean isAddedToQueue, AbisResponseDto abisResponseDto) {
		if (isAddedToQueue) {

			AbisResponseEntity abisResponseEntity = new AbisResponseEntity();
			abisResponseEntity.setCrBy(abisResponseDto.getCrBy());
			abisResponseEntity.setIsDeleted(false);
			abisResponseEntity.setLangCode(abisResponseDto.getLangCode());
			abisResponseEntity.setRespDtimes(abisResponseDto.getRespDtimes());
			abisResponseEntity.setRespText(abisResponseDto.getRespText());
			abisResponseEntity.setStatusCode(abisResponseDto.getStatusCode());
			abisResponseEntity.setStatusComment(abisResponseDto.getStatusComment());
			abisResponseEntity.setUpdBy(abisResponseDto.getUpdBy());
			return abisResponseEntity;
		}
		return null;
	}

}
