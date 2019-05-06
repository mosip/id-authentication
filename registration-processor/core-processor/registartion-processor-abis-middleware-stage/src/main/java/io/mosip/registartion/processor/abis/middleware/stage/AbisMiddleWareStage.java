package io.mosip.registartion.processor.abis.middleware.stage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.abis.AbisRequestDto;
import io.mosip.registration.processor.core.queue.factory.MosipQueue;
import io.mosip.registration.processor.core.queue.factory.QueueListener;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.core.spi.queue.MosipQueueConnectionFactory;
import io.mosip.registration.processor.core.spi.queue.MosipQueueManager;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;

public class AbisMiddleWareStage extends MosipVerticleManager {

	/** The mosip connection factory. */
	@Autowired
	private MosipQueueConnectionFactory<MosipQueue> mosipConnectionFactory;
	/** The mosip queue manager. */
	@Autowired
	private MosipQueueManager<MosipQueue, byte[]> mosipQueueManager;

	@Autowired
	private PacketInfoManager<Identity, ApplicantInfoDto> packetInfoManager;

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
						"INSERT");

				for (int i = 0; i < abisInsertRequestList.size(); i++) {
					boolean isAddedToQueue = sendToQueue(queue, abisInsertRequestList.get(i),
							abisInboundAddresses.get(i));

				}

				// send dto to queue

				List<AbisRequestDto> abisIdentifyRequestList = packetInfoManager.getInsertOrIdentifyRequest(abisRefId,
						"IDENTIFY");

				for (int i = 0; i < abisIdentifyRequestList.size(); i++) {
					boolean isAddedToQueue = sendToQueue(queue, abisIdentifyRequestList.get(i),
							abisInboundAddresses.get(i));

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

	public void cosnumerListener(Message message) {

	}

	private boolean sendToQueue(MosipQueue queue, AbisRequestDto abisRequestDto, String abisQueueAddress)
			throws IOException {
		boolean isAddedToQueue;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(abisRequestDto);
		oos.flush();
		byte[] abisRequestDtoBytes = bos.toByteArray();
		isAddedToQueue = mosipQueueManager.send(queue, abisRequestDtoBytes, abisQueueAddress);

		return isAddedToQueue;
	}

}
