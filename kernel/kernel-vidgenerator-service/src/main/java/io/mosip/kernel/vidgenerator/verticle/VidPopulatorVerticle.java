package io.mosip.kernel.vidgenerator.verticle;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.idgenerator.spi.VidGenerator;
import io.mosip.kernel.vidgenerator.constant.EventType;
import io.mosip.kernel.vidgenerator.constant.VidLifecycleStatus;
import io.mosip.kernel.vidgenerator.entity.VidEntity;
import io.mosip.kernel.vidgenerator.generator.VidWriter;
import io.mosip.kernel.vidgenerator.utils.MetaDataUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class VidPopulatorVerticle extends AbstractVerticle {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VidPopulatorVerticle.class);

	
	private long vidToGenerate;
	
	private Environment environment;
	
    private VidWriter vidWriter;
	
	private MetaDataUtil metaDataUtil; 
	
	private VidGenerator<String> vidGenerator;
	
	@SuppressWarnings("unchecked")
	public VidPopulatorVerticle(final ApplicationContext context) {
	this.environment=context.getBean(Environment.class);
	this.vidToGenerate=environment.getProperty("mosip.kernel.vid.vids-to-generate", Long.class);
	this.vidWriter=context.getBean("vidWriter",VidWriter.class);
	this.metaDataUtil=context.getBean("metaDataUtil",MetaDataUtil.class);
	this.vidGenerator=context.getBean(VidGenerator.class);
	}


	@Override
	public void start(Future<Void> startFuture) throws Exception {
	vertx.eventBus().consumer(EventType.GENERATEPOOL, handler -> {
		long noOfFreeVids = Long.parseLong(handler.body().toString());
		long noOfVidsToGenerate = vidToGenerate-noOfFreeVids;
		LOGGER.info("Persisting {} vids in pool",noOfVidsToGenerate);
		long count=0;
		while(count<vidToGenerate) {
			String vid = vidGenerator.generateId();
			VidEntity entity = new VidEntity();
			entity.setVid(vid);
			entity.setStatus(VidLifecycleStatus.AVAILABLE);
			metaDataUtil.setCreateMetaData(entity);
			vidWriter.persistVids(entity);
			count++;
		}
		handler.reply("pool population successfull");
		
		LOGGER.info("No of vids persisted are {}",count);
	});
	}
}
