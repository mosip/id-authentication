package io.mosip.registration.processor.abis.handler.stage;

import io.mosip.registration.processor.core.abstractverticle.MessageBusAddress;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.core.abstractverticle.MosipEventBus;
import io.mosip.registration.processor.core.abstractverticle.MosipVerticleManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AbisHandlerStage extends MosipVerticleManager {

    /** The cluster manager url. */
    @Value("${vertx.cluster.configuration}")
    private String clusterManagerUrl;


    public void deployVerticle() {
        MosipEventBus mosipEventBus = this.getEventBus(this, clusterManagerUrl, 50);
        this.consumeAndSend(mosipEventBus, MessageBusAddress.BIOMETRIC_IDENTIFICATION_HANDLER_BUS_IN,
                MessageBusAddress.BIOMETRIC_IDENTIFICATION_HANDLER_BUS_OUT);
    }

    @Override
    public MessageDTO process(MessageDTO object) {
        return null;
    }
}
