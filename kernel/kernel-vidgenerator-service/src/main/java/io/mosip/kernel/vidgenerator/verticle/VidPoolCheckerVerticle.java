package io.mosip.kernel.vidgenerator.verticle;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import io.mosip.kernel.vidgenerator.constant.EventType;
import io.mosip.kernel.vidgenerator.constant.VidLifecycleStatus;
import io.mosip.kernel.vidgenerator.service.VidService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class VidPoolCheckerVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(VidPoolCheckerVerticle.class);

	private VidService vidService;

	private Environment environment;

	private long threshold;

	private ApplicationContext context;

	public VidPoolCheckerVerticle(final ApplicationContext context) {
		this.context = context;
		this.vidService = this.context.getBean(VidService.class);
		this.environment = this.context.getBean(Environment.class);
		this.threshold = environment.getProperty("mosip.kernel.vid.min-unused-threshold", Long.class);
	}

	private volatile AtomicBoolean locked = new AtomicBoolean(false);

	@Override
	public void start(Future<Void> startFuture) {
		EventBus eventBus = vertx.eventBus();
		MessageConsumer<String> checkPoolConsumer = eventBus.consumer(EventType.CHECKPOOL);
		DeliveryOptions deliveryOptions = new DeliveryOptions();
		deliveryOptions.setSendTimeout(environment.getProperty("mosip.kernel.vid.pool-population-timeout", Long.class));
		checkPoolConsumer.handler(handler -> {
			long noOfFreeVids = vidService.fetchVidCount(VidLifecycleStatus.AVAILABLE);
			LOGGER.info("no of vid free present are {}", noOfFreeVids);
			if (noOfFreeVids < threshold && !locked.get()) {
				locked.set(true);
				eventBus.send(EventType.GENERATEPOOL, noOfFreeVids, deliveryOptions, replyHandler -> {
					if (replyHandler.succeeded()) {
						locked.set(false);
						LOGGER.info("population of pool done");
					} else if (replyHandler.failed()) {
						locked.set(false);
						LOGGER.error("population failed with cause ", replyHandler.cause());
					}
				});
			} else {
				LOGGER.info("event type is send {} eventBus{}", handler.isSend(), eventBus);
				LOGGER.info("locked generation");
			}
		});

		MessageConsumer<String> initPoolConsumer = eventBus.consumer(EventType.INITPOOL);
		initPoolConsumer.handler(initPoolHandler -> {
			long start =System.currentTimeMillis();
			long noOfFreeVids = vidService.fetchVidCount(VidLifecycleStatus.AVAILABLE);
			LOGGER.info("no of vid free present are {}", noOfFreeVids);
			LOGGER.info("value of threshold is {} and lock is {}", threshold, locked.get());
			boolean isEligibleForPool = noOfFreeVids < threshold && !locked.get();
			LOGGER.info("is eligible for pool {}", isEligibleForPool);
			if (isEligibleForPool) {
				locked.set(true);
				eventBus.send(EventType.GENERATEPOOL, noOfFreeVids, deliveryOptions, replyHandler -> {
					if (replyHandler.succeeded()) {
						locked.set(false);
						deployHttpVerticle(start);
						LOGGER.info("population of init pool done");
					} else if (replyHandler.failed()) {
						locked.set(false);
						LOGGER.error("population failed with cause ", replyHandler.cause());
						initPoolHandler.fail(100, replyHandler.cause().getMessage());
					}
				});
			} else {
				deployHttpVerticle(start);
			}
		});
	}

	private  void deployHttpVerticle(long start) {
		Verticle httpVerticle =  new VidFetcherVerticle(context);
		DeploymentOptions opts = new DeploymentOptions();
		vertx.deployVerticle(httpVerticle, opts, res -> {
			if (res.failed()) {
				LOGGER.info("Failed to deploy verticle " + httpVerticle.getClass().getSimpleName()+" "+res.cause());
			} else if(res.succeeded()) {
				LOGGER.info("population of pool is done starting fetcher verticle");
			    LOGGER.info("Starting vidgenerator service... ");
			    LOGGER.info("service took {} ms to pool and start",(System.currentTimeMillis()-start));
			    LOGGER.info("Deployed verticle " + httpVerticle.getClass().getSimpleName());
			}
		});

}
}
