package io.mosip.registration.processor.stages.demodedupe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipRouter;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;

/**
 * The Class DemodedupeStage.
 *
 * @author M1048358 Alok Ranjan
 */

@RefreshScope
@Service
public class DemoDedupeStage extends MosipVerticleAPIManager {

	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	/** server port number. */
	@Value("${server.port}")
	private String port;
	
	private MosipEventBus mosipEventBus = null;
	@Autowired
	DemodedupeProcessor demodedupeProcessor;

	/** Mosip router for APIs */
	@Autowired
	MosipRouter router;
	

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.DEMO_DEDUPE_BUS_IN, MessageBusAddress.DEMO_DEDUPE_BUS_OUT);
	}

	@Override
	public void start(){
		router.setRoute(this.postUrl(mosipEventBus.getEventbus(), MessageBusAddress.DEMO_DEDUPE_BUS_IN, MessageBusAddress.DEMO_DEDUPE_BUS_OUT));
		this.createServer(router.getRouter(), Integer.parseInt(port));
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
		return demodedupeProcessor.process(object, this.getClass().getSimpleName());

	}

}