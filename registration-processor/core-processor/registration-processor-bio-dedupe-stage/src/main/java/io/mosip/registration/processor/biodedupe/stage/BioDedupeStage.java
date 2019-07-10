/**
 * 
 */
package io.mosip.registration.processor.biodedupe.stage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipRouter;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleAPIManager;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;

/**
 * The Class BioDedupeStage.
 *
 * @author Sowmya
 */
@Service
public class BioDedupeStage extends MosipVerticleAPIManager {

	/** The cluster manager url. */
	@Value("${vertx.cluster.configuration}")
	private String clusterManagerUrl;

	@Autowired
	BioDedupeProcessor bioDedupeProcessor;

	/** server port number. */
	@Value("${server.port}")
	private String port;

	/** Mosip router for APIs */
	@Autowired
	MosipRouter router;

	private MosipEventBus mosipEventBus = null;

	/**
	 * Deploy verticle.
	 */
	public void deployVerticle() {
		mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
		this.consumeAndSend(mosipEventBus, MessageBusAddress.BIO_DEDUPE_BUS_IN, MessageBusAddress.BIO_DEDUPE_BUS_OUT);
	}

	@Override
	public void start(){
		router.setRoute(this.postUrl(mosipEventBus.getEventbus(), MessageBusAddress.BIO_DEDUPE_BUS_IN, MessageBusAddress.BIO_DEDUPE_BUS_OUT));
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
			return bioDedupeProcessor.process(object, this.getClass().getSimpleName());
		}
	}
