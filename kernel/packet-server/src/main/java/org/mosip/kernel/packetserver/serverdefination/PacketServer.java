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
import org.apache.tomcat.util.codec.binary.Base64;
import org.mosip.kernel.packetserver.configuration.ServerConfiguration;

public class PacketServer extends Thread {

	private final SshServer server;
	private ServerConfiguration serverConfiguration;
	private boolean isRunning;

	public PacketServer(ServerConfiguration serverConfiguration) {
		this.server = SshServer.setUpDefaultServer();
		this.serverConfiguration = serverConfiguration;
	}

	public void afterPropertiesSet() throws InvalidKeySpecException, NoSuchAlgorithmException {
		PublicKey allowed = loadAllowedKey();
		this.server.setHost(this.serverConfiguration.getHost());
		this.server.setPort(this.serverConfiguration.getPort());
		this.server.setKeyPairProvider(
				new SimpleGeneratorHostKeyProvider(new File(this.serverConfiguration.getHostKeyFileName())));
		this.server.setSubsystemFactories(Collections.<NamedFactory<Command>>singletonList(new SftpSubsystemFactory()));
		this.server.setFileSystemFactory(
				new VirtualFileSystemFactory(new File(this.serverConfiguration.getSftpRemoteDirectory()).toPath()));
		this.server.setCommandFactory(new ScpCommandFactory());
		List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<>();
		userAuthFactories.add(new UserAuthPasswordFactory());
		userAuthFactories.add(new UserAuthPublicKeyFactory());
		this.server.setUserAuthFactories(userAuthFactories);
		this.server
				.setPasswordAuthenticator((username, key, session) -> key.equals(this.serverConfiguration.getPassword())
						&& username.equals(this.serverConfiguration.getUsername()));
		this.server
				.setPublickeyAuthenticator((username, key, session) -> key.equals(allowed) && username.equals("demo"));
	}

	private PublicKey loadAllowedKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decodeBase64(this.serverConfiguration.getPublicKey()));
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}

	@Override
	public void run() {

		try {
			this.server.start();
			this.isRunning = true;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void destroy() {
		if (this.isRunning) {
			try {
				this.server.stop(false);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			} finally {
				this.isRunning = false;
			}
		}
	}

}
