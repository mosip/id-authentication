package io.mosip.kernel.core.packetuploader.configuration;

import java.io.File;

import io.mosip.kernel.core.packetuploader.exceptions.MosipIllegalArgumentException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageHandler;
import com.jcraft.jsch.ChannelSftp.LsEntry;

@Configuration
@IntegrationComponentScan
@EnableIntegration
@PropertySource(value = {"classpath:packet-uploader-configuration.properties"})
public class PacketUploaderImplConfiguration {

	@Value("${uploader.host}")
	private String host;
	@Value("${uploader.port}")
	private int port;
	@Value("${uploader.user}")
	private String user;
	@Value("${uploader.privateKey}")
	private Resource privateKey;
	@Value("${uploader.privateKeyPassphrase}")
	private String privateKeyPassphrase;
	@Value("${uploader.sftpRemoteDirectory}")
	private String sftpRemoteDirectory;

	@Bean
	public SessionFactory<LsEntry> sftpSessionFactory() {
		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
		factory.setHost(host);
		factory.setPort(port);
		factory.setUser(user);
		factory.setPrivateKey(privateKey);
		factory.setPrivateKeyPassphrase(privateKeyPassphrase);
		factory.setAllowUnknownKeys(true);
		return factory;
	}

	@Bean
	@ServiceActivator(inputChannel = "toSftpChannel")
	public MessageHandler handler() {
		SftpMessageHandler handler = new SftpMessageHandler(sftpSessionFactory());
		handler.setRemoteDirectoryExpression(new LiteralExpression(sftpRemoteDirectory));
		handler.setFileNameGenerator(message -> {
			if (message.getPayload() instanceof File) {
				return ((File) message.getPayload()).getName();
			} else {
				throw new MosipIllegalArgumentException("File expected as payload.", "");
			}
		});
		return handler;
	}

}
