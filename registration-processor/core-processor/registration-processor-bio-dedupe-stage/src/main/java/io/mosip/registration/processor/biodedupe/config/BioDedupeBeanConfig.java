package io.mosip.registration.processor.biodedupe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.registration.processor.bio.dedupe.service.impl.BioDedupeServiceImpl;
import io.mosip.registration.processor.biodedupe.stage.BioDedupeProcessor;
import io.mosip.registration.processor.biodedupe.stage.BioDedupeStage;
import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;

@Configuration
public class BioDedupeBeanConfig {

	@Bean
	public BioDedupeService getBioDedupeService() {
		return new BioDedupeServiceImpl();
	}

	@Bean
	public BioDedupeStage getBioDedupeStage() {
		return new BioDedupeStage();
	}

	@Bean
	public BioDedupeProcessor getBioDedupeProcessor() {
		return new BioDedupeProcessor();
	}

	@Bean
	public PacketInfoDao getPacketInfoDao() {
		return new PacketInfoDao();
	}

}
