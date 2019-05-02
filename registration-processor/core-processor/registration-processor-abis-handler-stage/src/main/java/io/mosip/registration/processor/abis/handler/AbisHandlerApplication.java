package io.mosip.registration.processor.abis.handler;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.abis.handler.stage.AbisHandlerStage;

public class AbisHandlerApplication {

    public static void main(String[] args){
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan("io.mosip.registration.processor.biometric.identification.config",
                "io.mosip.registration.processor.status.config",
                "io.mosip.registration.processor.rest.client.config",
                "io.mosip.registration.processor.packet.storage.config",
                "io.mosip.registration.processor.core.config",
                "io.mosip.registration.processor.core.kernel.beans");
        ctx.refresh();
        AbisHandlerStage handlerStage = new AbisHandlerStage();
        handlerStage.deployVerticle();
    }
}
