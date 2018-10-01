package org.mosip.kernel.packetserver.serverdefination;

import java.io.File;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.UserAuth;
import org.apache.sshd.server.auth.password.UserAuthPasswordFactory;
import org.apache.sshd.server.auth.pubkey.UserAuthPublicKeyFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.mosip.kernel.packetserver.constants.PacketServerExceptionConstants;
import org.mosip.kernel.packetserver.exception.MosipIllegalStateException;
import org.mosip.kernel.packetserver.exception.MosipInvalidSpecException;
import org.mosip.kernel.packetserver.packetutils.PacketUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * SSHD Server Defination for Mosip
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 */
@Component
public class PacketServer {

	/**
	 * {@link PacketUtils} instance for utility
	 */
	@Autowired
	PacketUtils packetUtils;
	/**
	 * {@link SshServer} instance
	 */
	private SshServer server;

	/**
	 * Host name for server
	 */
	@Value("${packetserver.host}")
	private String host;
	/**
	 * Port Number
	 */
	@Value("${packetserver.port}")
	private int port;
	/**
	 * Public key for private key authentication
	 */
	@Value("${packetserver.publicKey}")
	private String publicKey;
	/**
	 * Host key file name
	 */
	@Value("${packetserver.keyPairGenerator}")
	private String hostKeyFileName;
	/**
	 * SFTP home location
	 */
	@Value("${packetserver.sftpRemoteDirectory}")
	private String sftpRemoteDirectory;
	/**
	 * Username for authentication
	 */
	@Value("${packetserver.username}")
	private String username;
	/**
	 * Password for authentication
	 */
	@Value("${packetserver.password}")
	private String password;

	/**
	 * This will initialize all variables from property file after application
	 * starts
	 */
	@EventListener(ApplicationReadyEvent.class)
	private void afterPropertiesSet() {
		this.server = SshServer.setUpDefaultServer();
		PublicKey allowed = loadAllowedKey();
		this.server.setHost(host);
		this.server.setPort(port);
		this.server.setKeyPairProvider(
				new SimpleGeneratorHostKeyProvider(new File(hostKeyFileName)));
		this.server.setSubsystemFactories(
				Collections.<NamedFactory<Command>>singletonList(
						new SftpSubsystemFactory()));
		this.server.setFileSystemFactory(new VirtualFileSystemFactory(
				new File(sftpRemoteDirectory).toPath()));
		this.server.setCommandFactory(new ScpCommandFactory());
		List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<>();
		userAuthFactories.add(new UserAuthPasswordFactory());
		userAuthFactories.add(new UserAuthPublicKeyFactory());
		this.server.setUserAuthFactories(userAuthFactories);
		this.server.setPasswordAuthenticator((user, key,
				session) -> key.equals(password) && user.equals(username));
		this.server.setPublickeyAuthenticator((user, key,
				session) -> key.equals(allowed) && user.equals(username));
		this.start();
	}

	/**
	 * This will fetch allowed public key for public key authentication
	 * 
	 * @return allowed {@link PublicKey}
	 * @throws NoSuchAlgorithmException
	 *             to be thrown when algorithm is not present
	 * @throws InvalidKeySpecException
	 *             to be thrown when
	 */
	private PublicKey loadAllowedKey() {
		byte[] keyBytes = packetUtils.getFileBytes(publicKey);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(
				Base64.decodeBase64(keyBytes));
		PublicKey key = null;
		try {
			KeyFactory kf = KeyFactory.getInstance("RSA");
			key = kf.generatePublic(spec);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			throw new MosipInvalidSpecException(
					PacketServerExceptionConstants.MOSIP_INVALID_SPEC_EXCEPTION);
		}
		return key;
	}

	/**
	 * This starts the server
	 */
	private void start() {
		try {
			this.server.start();
		} catch (IOException e) {
			throw new MosipIllegalStateException(
					PacketServerExceptionConstants.MOSIP_ILLEGAL_STATE_EXCEPTION,
					e.getCause());
		}
	}

	/**
	 * This stops the server when context will be closed
	 */
	@EventListener(ContextClosedEvent.class)
	private void stop() {
		try {
			server.stop(false);
		} catch (IOException e) {
			throw new MosipIllegalStateException(
					PacketServerExceptionConstants.MOSIP_ILLEGAL_STATE_EXCEPTION,
					e.getCause());
		}
	}

}
