package io.mosip.kernel.core.packetuploader.gateway;

import java.io.File;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface PacketUploaderGateway {

	@Gateway
	void upload(File inputFile);
	
}
