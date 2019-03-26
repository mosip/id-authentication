package io.mosip.registration.processor.packet.uploader.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.mosip.registration.processor.packet.uploader.archiver.util.PacketArchiver;
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
}
