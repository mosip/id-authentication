package io.mosip.registration.processor.abis.handler;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.abis.handler.stage.AbisHandlerStage;

/**
 * The Class AbisHandlerApplication.
 * 
 * @author M1048358 Alok Ranjan
 */
public class AbisHandlerApplication {

    public static void main(String[] args){
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan("io.mosip.registration.processor.abis.handler.config",
                "io.mosip.registration.processor.status.config",
                "io.mosip.registration.processor.rest.client.config",
                "io.mosip.registration.processor.packet.storage.config",
                "io.mosip.registration.processor.core.config",
                "io.mosip.registration.processor.core.kernel.beans",
                "io.mosip.registration.processor.packet.manager.config");
        ctx.refresh();
        AbisHandlerStage handlerStage = ctx.getBean(AbisHandlerStage.class);
        handlerStage.deployVerticle();
    }
}
