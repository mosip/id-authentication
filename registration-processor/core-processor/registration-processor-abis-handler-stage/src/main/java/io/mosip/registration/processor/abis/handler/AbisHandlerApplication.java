package io.mosip.registration.processor.abis.handler;

import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.abis.handler.stage.AbisHandlerStage;

public class AbisHandlerApplication {

    public static void main(String[] args){
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan("io.mosip.registration.processor.abis.handler.config",
                "io.mosip.registration.processor.status.config",
                "io.mosip.registration.processor.rest.client.config",
                "io.mosip.registration.processor.packet.storage.config",
                "io.mosip.registration.processor.core.config",
                "io.mosip.registration.processor.core.kernel.beans");
        ctx.refresh();
        AbisHandlerStage handlerStage = ctx.getBean(AbisHandlerStage.class);
        handlerStage.deployVerticle();

        MessageDTO dto = new MessageDTO();
        dto.setRid("10003100030001520190422074511");
        handlerStage.process(dto);
    }
}
