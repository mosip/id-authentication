package io.mosip.kernel.uingenerator.verticle;

import org.springframework.context.ApplicationContext;

import io.mosip.kernel.uingenerator.constant.UINHealthConstants;
import io.mosip.kernel.uingenerator.constant.UinGeneratorConstant;
import io.mosip.kernel.uingenerator.generator.UinProcesser;
import io.vertx.core.AbstractVerticle;

/**
 * Verticle instance for Uin Generator
 * 
 * @author Dharmesh Khandelwal
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
public class UinGeneratorVerticle extends AbstractVerticle {

	/**
	 * The field for logger
	 */
	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(UinGeneratorVerticle.class);

	/**
	 * Field for UinProcesser
	 */
	private UinProcesser uinProcesser;

	/**
	 * Initialize beans
	 * 
	 * @param context context
	 */
	public UinGeneratorVerticle(final ApplicationContext context) {
		uinProcesser = (UinProcesser) context.getBean("uinProcesser");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.vertx.core.AbstractVerticle#start()
	 */
	@Override
	public void start() {
		vertx.eventBus().consumer(UinGeneratorConstant.UIN_GENERATOR_ADDRESS, receivedMessage -> {
   			if (receivedMessage.body().equals(UinGeneratorConstant.GENERATE_UIN) && uinProcesser.shouldGenerateUins()) {
   				vertx.executeBlocking(future -> {
					uinProcesser.generateUins();
					future.complete();
				}, result -> {
					if (result.succeeded()) {
						
						// LOGGER.info("Generated and persisted uins");
					} else {
						
						// LOGGER.info("Uin Genaration failed", result.cause());
					}
				});
			}
   			receivedMessage.reply(UINHealthConstants.ACTIVE);
		});
	}
}