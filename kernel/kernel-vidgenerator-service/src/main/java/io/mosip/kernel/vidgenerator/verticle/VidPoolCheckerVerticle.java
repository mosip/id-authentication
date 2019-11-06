package io.mosip.kernel.vidgenerator.verticle;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import io.mosip.kernel.vidgenerator.constant.EventType;
import io.mosip.kernel.vidgenerator.constant.VidLifecycleStatus;
import io.mosip.kernel.vidgenerator.service.VidService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class VidPoolCheckerVerticle extends AbstractVerticle  {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VidPoolCheckerVerticle.class);
	
	private VidService vidService;
	
	private Environment environment;
	
	private long theshold;
	
	public VidPoolCheckerVerticle(final ApplicationContext context) {
	this.vidService = context.getBean(VidService.class);
	this.environment=context.getBean(Environment.class);
	this.theshold = environment.getProperty("mosip.kernel.vid.min-unused-threshold", Long.class);
	}
	
	private volatile AtomicBoolean locked=new AtomicBoolean(false);

	@Override
	public void start(Future<Void> startFuture){
		vertx.eventBus().consumer(EventType.CHECKPOOL, handler -> {
			long noOfFreeVids=vidService.fetchVidCount(VidLifecycleStatus.AVAILABLE);
			if(noOfFreeVids<theshold && !locked.get()) {
				locked.set(true);
				LOGGER.info("changing lock to true");
				vertx.eventBus().publish(EventType.GENERATEPOOL, EventType.GENERATEPOOL);
			}else {
				LOGGER.info("locked generation");
			}
		
		});
		
		vertx.eventBus().consumer(EventType.RESETLOCK, handler -> {
		  locked.set(false);
		  LOGGER.info("reseting locked to false");
		});
	}

}
