package io.mosip.registration.processor.packet.receiver.config;

import java.io.File;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
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
	 * PacketReceiverService bean
	 * 
	 * @return
	 */
	@Bean
	public PacketReceiverService<File, MessageDTO> getPacketReceiverService(){
		return new PacketReceiverServiceImpl();
	}
	
	/**
	 * PacketReceiverStage bean
	 * 
	 * @return
	 */
	@Bean 
	public PacketReceiverStage getPacketReceiverStage() {
		return new PacketReceiverStage();
	}
	
	/**
	 * GlobalExceptionHandler bean
	 * 
	 * @return
	 */
	@Bean
	public PacketReceiverExceptionHandler getGlobalExceptionHandler() {
		return new PacketReceiverExceptionHandler();
	}
	
	@Bean
	public PacketReceiverResponseBuilder getPacketReceiverResponseBuilder() {
		return new PacketReceiverResponseBuilder();
	}
}
