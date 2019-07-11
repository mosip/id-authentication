package io.mosip.registration.processor.abis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.mosip.registration.processor.abis.messagequeue.AbisMessageQueueImpl;
import io.mosip.registration.processor.abis.service.AbisService;
import io.mosip.registration.processor.abis.service.impl.AbisServiceImpl;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisIdentifyResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisInsertResponseDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisPingRequestDto;
import io.mosip.registration.processor.core.packet.dto.abis.AbisPingResponseDto;
import io.mosip.registration.processor.packet.storage.utils.Utilities;

/**
 * The Class RegistrationAbisConfig.
 */
@Configuration
public class RegistrationAbisConfig {
	@Bean
	public AbisMessageQueueImpl getAbisMessageQueueImpl() {
		return new AbisMessageQueueImpl();
	}

	@Bean
	public AbisService getAbisService() {
		return new AbisServiceImpl() ;
				}
}
