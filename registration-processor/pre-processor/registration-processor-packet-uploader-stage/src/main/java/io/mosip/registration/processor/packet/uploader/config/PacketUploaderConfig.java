package io.mosip.registration.processor.packet.uploader.config;

import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.kernel.virusscanner.clamav.impl.VirusScannerImpl;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.packet.uploader.archiver.util.PacketArchiver;
import io.mosip.registration.processor.packet.uploader.service.PacketUploaderService;
import io.mosip.registration.processor.packet.uploader.service.impl.PacketUploaderServiceImpl;
import io.mosip.registration.processor.packet.uploader.stage.PacketUploaderStage;


/**
 * @author Mukul Puspam
 *
 */
@Configuration
public class PacketUploaderConfig {

	/**
	 * PacketUploaderStage Bean
	 * @return
	 */
	@Bean
	public PacketUploaderStage getPacketUploaderStage() {
		return new PacketUploaderStage();
	}
	
	/**
	 * PacketArchiver Bean
	 * @return
	 */
	@Bean
	public PacketArchiver getPacketArchiver() {
		return new PacketArchiver();
	}
	
	
	@Bean
	public PacketUploaderService<MessageDTO> getPacketUploaderService() {
		return new PacketUploaderServiceImpl();
	}
	
	/**
	 * Virus scanner service.
	 *
	 * @return the virus scanner
	 */
	@Bean
	public VirusScanner<Boolean, InputStream> virusScannerService() {
		return new VirusScannerImpl();
	}


}
