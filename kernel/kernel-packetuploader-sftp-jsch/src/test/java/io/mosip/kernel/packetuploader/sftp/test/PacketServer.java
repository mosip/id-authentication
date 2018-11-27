package io.mosip.kernel.packetuploader.sftp.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.springframework.core.io.ClassPathResource;


public class PacketServer{


	private SshServer server;
    
	private String host="127.0.0.1";
    
	private int port=10022; 
    
	private String publicKey="/id_rsa.pub";
    
	private String username="testUser";
	
	private String password="testpassword";
	
	private boolean running;

	private String keyPairGenerator;
	
	public void afterPropertiesSet() throws InvalidKeySpecException, IOException, NoSuchAlgorithmException {
		this.server = SshServer.setUpDefaultServer();
		keyPairGenerator = Files.createTempDirectory("hostkey.ser").toString();

		PublicKey allowed = loadAllowedKey();
		this.server.setHost(host);
		this.server.setPort(port);
		this.server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File(keyPairGenerator)));
		this.server.setSubsystemFactories(Collections.<NamedFactory<Command>>singletonList(new SftpSubsystemFactory()));
		this.server.setCommandFactory(new ScpCommandFactory());
		List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<>();
		userAuthFactories.add(new UserAuthPasswordFactory());
		userAuthFactories.add(new UserAuthPublicKeyFactory());
		this.server.setUserAuthFactories(userAuthFactories);
		this.server.setPasswordAuthenticator((user, key, session) -> key.equals(password) && user.equals(username));
		this.server.setPublickeyAuthenticator((user, key, session) -> key.equals(allowed) && user.equals(username));
		this.start();
	}

	
	public void setHomeFolder(Path path) {
		server.setFileSystemFactory(new VirtualFileSystemFactory(path));
	}

	private PublicKey loadAllowedKey() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
		byte[] keyBytes = Files.readAllBytes(new ClassPathResource(publicKey).getFile().toPath());
		X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decodeBase64(keyBytes));
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);

	}

	public void start() {
		try {
			this.server.start();
			this.running = true;
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}

	public void stop() {
		if (this.running) {
			try {
				server.stop(false);
			} catch (IOException e) {
				throw new IllegalStateException();
			} finally {
				this.running = false;
			}
		}
	}

	public boolean isRunning() {
		return this.running;
	}

}
