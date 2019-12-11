package io.mosip.kernel.pridgenerator.verticle;

import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.idgenerator.spi.PridGenerator;
import io.mosip.kernel.pridgenerator.constant.EventType;
import io.mosip.kernel.pridgenerator.constant.PridLifecycleStatus;
import io.mosip.kernel.pridgenerator.entity.PridEntity;
import io.mosip.kernel.pridgenerator.generator.PridWriter;
import io.mosip.kernel.pridgenerator.utils.MetaDataUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class PridPopulatorVerticle extends AbstractVerticle {

	private static final Logger LOGGER = LoggerFactory.getLogger(PridPopulatorVerticle.class);

	private long pridToGenerate;

	private Environment environment;

	private PridWriter pridWriter;

	private MetaDataUtil metaDataUtil;

	private PridGenerator<String> pridGenerator;

	@SuppressWarnings("unchecked")
	public PridPopulatorVerticle(final ApplicationContext context) {
		this.environment = context.getBean(Environment.class);
		this.pridToGenerate = environment.getProperty("mosip.kernel.prid.prids-to-generate", Long.class);
		this.pridWriter = context.getBean("pridWriter", PridWriter.class);
		this.metaDataUtil = context.getBean("metaDataUtil", MetaDataUtil.class);
		this.pridGenerator = context.getBean(PridGenerator.class);
	}

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		vertx.eventBus().consumer(EventType.GENERATEPOOL, handler -> {
			long noOfFreeprids = Long.parseLong(handler.body().toString());
			long noOfpridsToGenerate = pridToGenerate - noOfFreeprids;
			LOGGER.info("Persisting {} prids in pool", noOfpridsToGenerate);
			long count = 0;
			while (count < pridToGenerate) {
				String prid = pridGenerator.generateId();
				PridEntity entity = new PridEntity();
				entity.setPrid(prid);
				entity.setStatus(PridLifecycleStatus.AVAILABLE);
				metaDataUtil.setCreateMetaData(entity);
				boolean isPersisted = pridWriter.persistPrids(entity);
				if (isPersisted) {
					count++;
				}
			}
			handler.reply("pool population successfull");

			LOGGER.info("No of prids persisted are {}", count);
		});
	}
}
