package io.mosip.registration.processor.reprocessor;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.reprocessor.stage.ReprocessorStage;

public class ReprocessorApplication {
	
	public static void main( String[] args )
    {
    	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.scan("io.mosip.registration.processor.reprocessor.config");
		ctx.refresh();
		ReprocessorStage reprocessorStage = ctx.getBean(ReprocessorStage.class);
		reprocessorStage.deployVerticle();
    }

}
