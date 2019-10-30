package io.mosip.kernel.vidgenerator.verticle;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import io.mosip.kernel.vidgenerator.constant.VidSchedulerConstants;
import io.mosip.kernel.vidgenerator.service.VidService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class VidExpiryVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(VidExpiryVerticle.class);

	private VidService vidService;

	private Environment environment;

	public VidExpiryVerticle(final ApplicationContext context) {
		this.environment = context.getBean(Environment.class);
		this.vidService = context.getBean(VidService.class);
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		vertx.deployVerticle(VidSchedulerConstants.CEYLON_SCHEDULER, this::schedulerResult);
	}

	public void schedulerResult(AsyncResult<String> result) {
		if (result.succeeded()) {
			LOGGER.info("scheduler verticle deployment successfull");
			cronScheduling(vertx);
		} else if (result.failed()) {
			LOGGER.info("scheduler verticle deployment failed with cause ",result.cause());
		}
	}

	/**
	 * This method does the cron scheduling by fetchin cron expression from config
	 * server
	 *
	 * @param vertx the vertx
	 */
	private void cronScheduling(Vertx vertx) {

		EventBus eventBus = vertx.eventBus();
		
		MessageConsumer<JsonObject> consumer = eventBus.consumer(VidSchedulerConstants.NAME_VALUE);
		consumer.handler (
		    message -> {
		        vidService.expireAndRenew();
		      }
		);

		JsonObject timer = new JsonObject()
				.put(VidSchedulerConstants.TYPE, environment.getProperty(VidSchedulerConstants.TYPE_VALUE))
				.put(VidSchedulerConstants.SECONDS, environment.getProperty(VidSchedulerConstants.SECONDS_VALUE))
				.put(VidSchedulerConstants.MINUTES, environment.getProperty(VidSchedulerConstants.MINUTES_VALUE))
				.put(VidSchedulerConstants.HOURS, environment.getProperty(VidSchedulerConstants.HOURS_VALUE))
				.put(VidSchedulerConstants.DAY_OF_MONTH,
						environment.getProperty(VidSchedulerConstants.DAY_OF_MONTH_VALUE))
				.put(VidSchedulerConstants.MONTHS, environment.getProperty(VidSchedulerConstants.MONTHS_VALUE))
				.put(VidSchedulerConstants.DAYS_OF_WEEK,
						environment.getProperty(VidSchedulerConstants.DAYS_OF_WEEK_VALUE));

		eventBus.send(VidSchedulerConstants.CHIME,
				new JsonObject().put(VidSchedulerConstants.OPERATION, VidSchedulerConstants.OPERATION_VALUE)
						.put(VidSchedulerConstants.NAME, VidSchedulerConstants.NAME_VALUE)
						.put(VidSchedulerConstants.DESCRIPTION, timer),
				res -> {
					if (res.succeeded()) {
						LOGGER.info("VIDRevokerschedular started");
					} else if (res.failed()) {
						LOGGER.info("VIDRevokerschedular failed with cause {}", res.cause());
						vertx.close();
					}
				});

	}
}
