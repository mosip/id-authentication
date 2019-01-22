package io.mosip.kernel.uingenerator.verticle;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.mosip.kernel.uingenerator.entity.UinEntity;
import io.mosip.kernel.uingenerator.generator.UinProcesser;
import io.mosip.kernel.uingenerator.generator.UinWriter;
import io.vertx.core.AbstractVerticle;

/**
 * Verticle instance for Uin Generator
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class UinGeneratorVerticle extends AbstractVerticle {

	/**
	 * The field for logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UinGeneratorVerticle.class);

	/**
	 * Field for UinProcesser
	 */
	private UinProcesser uinProcesser;

	/**
	 * Field for UinWriter
	 */
	private UinWriter uinWriter;

	/**
	 * Initialize beans
	 * 
	 * @param context
	 *            context
	 */
	public UinGeneratorVerticle(final ApplicationContext context) {
		uinProcesser = (UinProcesser) context.getBean("uinProcesser");
		uinWriter = (UinWriter) context.getBean("uinWriter");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.vertx.core.AbstractVerticle#start()
	 */
	@Override
	public void start() {
		vertx.eventBus().consumer(UinGeneratorConstant.UIN_GENERATOR_ADDRESS, receivedMessage -> {
			if (receivedMessage.body().equals(UinGeneratorConstant.GENERATE_UIN)) {
				vertx.executeBlocking(future -> {
					List<UinEntity> uins = uinProcesser.generateUins();
					uinWriter.write(uins);
					future.complete();
				}, res -> LOGGER.info("Generated and persisted uins"));
			}
		});
	}
}
