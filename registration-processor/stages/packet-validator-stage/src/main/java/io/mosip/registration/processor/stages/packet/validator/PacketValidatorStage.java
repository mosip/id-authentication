/**
 * 
 */
package io.mosip.registration.processor.stages.packet.validator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;

/**
 * The Class PacketValidatorStage.
 *
 * @author M1022006
 * @author Girish Yarru
 */

@RefreshScope
@Service
public class PacketValidatorStage extends MosipVerticleManager {

	/** Paacket validate Processor */
	@Autowired
	PacketValidateProcessor packetvalidateprocessor;

	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;
	/** The secs. */


	/** The mosip event bus. */
	MosipEventBus mosipEventBus = null;


	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		mosipEventBus = this.getEventBus(this.getClass(), clusterManagerUrl);
     	MessageDTO dto=new MessageDTO();
		dto.setRid("10031100110032120190307124851");
		this.process(dto);
		//this.send(mosipEventBus ,MessageBusAddress.PACKET_VALIDATOR_BUS_OUT,dto);
	}



	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * io.mosip.registration.processor.core.spi.eventbus.EventBusManager#process(
	 * java.lang.Object)
	 */

	@Override
	public MessageDTO process(MessageDTO object) {
		return packetvalidateprocessor.process(object);
	}

}
