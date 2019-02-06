package io.mosip.registration.processor.packet.manager.config;
	
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.service.impl.FileManagerImpl;

/**
 * The Class PacketManagerConfig.
 */
@Configuration
public class PacketManagerConfig {
	
	@Bean
	public FileManager<DirectoryPathDto, InputStream> filemanager() {
		return new FileManagerImpl();
	}
}
