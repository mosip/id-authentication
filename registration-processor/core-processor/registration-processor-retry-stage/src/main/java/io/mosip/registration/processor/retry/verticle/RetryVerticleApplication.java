package io.mosip.registration.processor.retry.verticle;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.registration.processor.retry.verticle.stages.RetryStage;

/**
 * The Class RetryVerticleApplication.
 *
 * @author Jyoti prakash nayak
 */
public class RetryVerticleApplication {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan("io.mosip.registration.processor.rest.client.config",
				"io.mosip.registration.processor.retry.verticle.config",
				"io.mosip.registration.processor.core.config");
		context.refresh();
		RetryStage validateBean = context.getBean(RetryStage.class);
		validateBean.deployVerticle();
	}

}
