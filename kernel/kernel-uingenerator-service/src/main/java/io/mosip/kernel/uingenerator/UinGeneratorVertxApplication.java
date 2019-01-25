package io.mosip.kernel.uingenerator;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.mosip.kernel.uingenerator.config.UinGeneratorConfiguration;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorServerVerticle;
import io.mosip.kernel.uingenerator.verticle.UinGeneratorVerticle;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * Uin Generator vertx application
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class UinGeneratorVertxApplication {

	/**
	 * The field for Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(UinGeneratorVertxApplication.class);

	/**
	 * The main function to start vertx app
	 * 
	 * @param args
	 *            args
	 */
	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(UinGeneratorConfiguration.class);
		VertxOptions options = new VertxOptions();
		Vertx vertx = Vertx.vertx(options);
		Verticle[] verticles = { new UinGeneratorVerticle(context), new UinGeneratorServerVerticle(context) };
		Stream.of(verticles).forEach(verticle -> vertx.deployVerticle(verticle, stringAsyncResult -> {
			if (stringAsyncResult.succeeded()) {
				LOGGER.info("Succesfully deployed {}", verticle.getClass().getSimpleName());
			} else {
				LOGGER.info("Failed to deploy {} \\nCause: {}", verticle.getClass().getSimpleName(),
						stringAsyncResult.cause());
			}
		}));
	}
}