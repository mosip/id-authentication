package io.mosip.registration.processor.packet.receiver.config;

import java.io.File;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import io.mosip.kernel.core.virusscanner.spi.VirusScanner;
import io.mosip.kernel.virusscanner.clamav.impl.VirusScannerImpl;
import io.mosip.registration.processor.core.abstractverticle.MessageDTO;
import io.mosip.registration.processor.packet.receiver.builder.PacketReceiverResponseBuilder;
import io.mosip.registration.processor.packet.receiver.exception.handler.PacketReceiverExceptionHandler;
import io.mosip.registration.processor.packet.receiver.service.PacketReceiverService;
import io.mosip.registration.processor.packet.receiver.service.impl.PacketReceiverServiceImpl;
import io.mosip.registration.processor.packet.receiver.stage.PacketReceiverStage;

/**
 * The Class PacketReceiverConfig.
 */
/**
 * @author Mukul Puspam
 *
 */
@Configuration
@EnableAspectJAutoProxy
public class PacketReceiverConfig {

	/**
	 * PacketReceiverService bean.
	 *
	 * @return the packet receiver service
	 */
	@Bean
	public PacketReceiverService<File, MessageDTO> getPacketReceiverService() {
		return new PacketReceiverServiceImpl();
	}

	/**
	 * PacketReceiverStage bean.
	 *
	 * @return the packet receiver stage
	 */
	@Bean
	public PacketReceiverStage getPacketReceiverStage() {
		return new PacketReceiverStage();
	}

	/**
	 * GlobalExceptionHandler bean.
	 *
	 * @return the global exception handler
	 */
	@Bean
	public PacketReceiverExceptionHandler getGlobalExceptionHandler() {
		return new PacketReceiverExceptionHandler();
	}

	/**
	 * Gets the packet receiver response builder.
	 *
	 * @return the packet receiver response builder
	 */
	@Bean
	public PacketReceiverResponseBuilder getPacketReceiverResponseBuilder() {
		return new PacketReceiverResponseBuilder();
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
