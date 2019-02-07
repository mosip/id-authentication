package io.mosip.registration.processor.packet.storage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.spi.packetmanager.PacketInfoManager;
import io.mosip.registration.processor.packet.storage.dao.PacketInfoDao;
import io.mosip.registration.processor.packet.storage.dto.ApplicantInfoDto;
import io.mosip.registration.processor.packet.storage.service.impl.PacketInfoManagerImpl;
import io.mosip.registration.processor.packet.storage.utils.Utilities;

@Configuration
public class PacketStorageBeanConfig {
	
	@Bean
	public PacketInfoManager<Identity, ApplicantInfoDto> getPacketInfoManager() {
		return new PacketInfoManagerImpl();
	}
	
	@Bean
	public PacketInfoDao getPacketInfoDao() {
		return new PacketInfoDao();
	}
	
	@Bean
	public Utilities getUtilities() {
		return new Utilities();
	}
}
