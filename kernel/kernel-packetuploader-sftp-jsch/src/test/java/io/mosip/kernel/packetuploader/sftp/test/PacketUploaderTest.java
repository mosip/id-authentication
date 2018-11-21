package io.mosip.kernel.packetuploader.sftp.test;

import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import io.mosip.kernel.core.packetuploader.exception.ConnectionException;
import io.mosip.kernel.core.packetuploader.exception.EmptyPathException;
import io.mosip.kernel.core.packetuploader.exception.IllegalConfigurationException;
import io.mosip.kernel.core.packetuploader.exception.IllegalIdentityException;
import io.mosip.kernel.core.packetuploader.exception.NullConfigurationException;
import io.mosip.kernel.core.packetuploader.exception.NullPathException;
import io.mosip.kernel.core.packetuploader.exception.PacketSizeException;
import io.mosip.kernel.core.packetuploader.spi.PacketUploader;
import io.mosip.kernel.packetuploader.sftp.model.SFTPChannel;
import io.mosip.kernel.packetuploader.sftp.model.SFTPServer;
@RunWith(SpringRunner.class)
@SpringBootTest
public class PacketUploaderTest {

	
	
	
	@Autowired
	private PacketUploader<SFTPServer,SFTPChannel> packetUploader;
	
	private static PacketServer server;
	private SFTPServer configuration;
	private Path sftpFolder;

	@BeforeClass
	public static void startServer() throws Exception {
		server = new PacketServer();
		server.afterPropertiesSet();
		if (!server.isRunning()) {
			server.start();
		}
	}

	@Before
	public void setUp() {
		configuration = new SFTPServer("127.0.0.1", 10022, "testUser", "testpassword", "/");
	}

	@AfterClass
	public static void stopServer() {
		if (server.isRunning()) {
			server.stop();
		}
	}

	@Before
	@After
	public void cleanSftpFolder() throws IOException {
		sftpFolder = Files.createTempDirectory("SFTP_UPLOAD_TEST");
		server.setHomeFolder(sftpFolder);
		Files.walk(sftpFolder).filter(Files::isRegularFile).map(Path::toFile).forEach(File::delete);
	}

	@Test
	public void testCreateSFTPChannel() throws ConnectionException {

		assertThat(packetUploader.createSFTPChannel(configuration), isA(SFTPChannel.class));

	}

	@Test
	public void testUpload() throws IOException, ConnectionException {
		Path tempFile = new ClassPathResource("/packet3.zip").getFile().toPath();

		assertEquals(0, Files.list(sftpFolder).count());
		SFTPChannel channel = packetUploader.createSFTPChannel(configuration);
		packetUploader.upload(channel, tempFile.toString());

		List<Path> paths = Files.list(sftpFolder).collect(Collectors.toList());
		assertEquals(1, paths.size());
		assertEquals(tempFile.getFileName(), paths.get(0).getFileName());
		packetUploader.releaseConnection(channel);
	}

	@Test
	public void testCreateSFTPIdentityChannel() throws ConnectionException, IOException {
		String path = new ClassPathResource("/id_rsa1").getFile().getPath();
		configuration = new SFTPServer("127.0.0.1", 10022, "testUser", path, null, "/");
		assertThat(packetUploader.createSFTPChannel(configuration), isA(SFTPChannel.class));

	}

	@Test(expected = IllegalIdentityException.class)
	public void testCreateSFTPOllegalIdentityChannel() throws ConnectionException, IOException {
		String path = new ClassPathResource("/id_rsa1").getPath();
		configuration = new SFTPServer("127.0.0.1", 10022, "testUser", path, null, "/");
		packetUploader.createSFTPChannel(configuration);

	}

	@Test
	public void testReleaseConnection() throws ConnectionException, JSchException {
		ChannelSftp channelSftp = Mockito.mock(ChannelSftp.class);
		Session session = Mockito.mock(Session.class);
		SFTPChannel channel = new SFTPChannel(channelSftp, null);
		when(channelSftp.getSession()).thenReturn(session);
		doNothing().when(session).disconnect();
		doNothing().when(channelSftp).exit();
		packetUploader.releaseConnection(channel);
		Mockito.verify(session, times(1)).disconnect();
	}

	@Test(expected = ConnectionException.class)
	public void testCreateSFTPConnectionExceptionChannel() throws ConnectionException {
		configuration = new SFTPServer("127.0.0.1", 10022, "user", "testpassword", "/");
		assertThat(packetUploader.createSFTPChannel(configuration), isA(SFTPChannel.class));

	}

	@Test(expected = EmptyPathException.class)
	public void testUploadSftpEmptyPathexception() throws IOException, ConnectionException {
		SFTPChannel channel = packetUploader.createSFTPChannel(configuration);
		packetUploader.upload(channel, "");
	}

	@Test(expected = PacketSizeException.class)
	public void testUploadSftpSizeException() throws IOException, ConnectionException {
		SFTPChannel channel = packetUploader.createSFTPChannel(configuration);
		Path tempFile = new ClassPathResource("/packet4.zip").getFile().toPath();
		packetUploader.upload(channel, tempFile.toString());
	}

	@Test(expected = PacketSizeException.class)
	public void testUploadSFTPException() throws IOException, ConnectionException {
		SFTPChannel channel = packetUploader.createSFTPChannel(configuration);

		packetUploader.upload(channel, "/fakePath");
	}

	@Test(expected = NullPathException.class)
	public void testUploadSftpNullPathException() throws IOException, ConnectionException {
		SFTPChannel channel = packetUploader.createSFTPChannel(configuration);
		packetUploader.upload(channel, null);
	}

	@Test(expected = IllegalConfigurationException.class)
	public void testCreateSFTPEmptyHostChannel() throws ConnectionException {
		configuration = new SFTPServer("", 10022, "testUser", "testpassword", "/");
		assertThat(packetUploader.createSFTPChannel(configuration), isA(SFTPChannel.class));

	}

	@Test(expected = NullConfigurationException.class)
	public void testCreateSFTPNullHostChannel() throws ConnectionException {
		configuration = new SFTPServer(null, 10022, "testUser", "testpassword", "/");
		assertThat(packetUploader.createSFTPChannel(configuration), isA(SFTPChannel.class));

	}

	@Test(expected = IllegalConfigurationException.class)
	public void testCreateSFTPEmptyUserChannel() throws ConnectionException {
		configuration = new SFTPServer("127.0.0.1", 10022, "", "testpassword", "/");
		assertThat(packetUploader.createSFTPChannel(configuration), isA(SFTPChannel.class));

	}

	@Test(expected = NullConfigurationException.class)
	public void testCreateSFTPNullUserChannel() throws ConnectionException {
		configuration = new SFTPServer("127.0.0.1", 10022, null, "testpassword", "/");
		assertThat(packetUploader.createSFTPChannel(configuration), isA(SFTPChannel.class));

	}

	@Test(expected = IllegalConfigurationException.class)
	public void testCreateSFTPEmptyDirectoryChannel() throws ConnectionException {
		configuration = new SFTPServer("127.0.0.1", 10022, "testUser", "testpassword", "");
		assertThat(packetUploader.createSFTPChannel(configuration), isA(SFTPChannel.class));

	}

	@Test(expected = NullConfigurationException.class)
	public void testCreateSFTPNullDirectoryChannel() throws ConnectionException {
		configuration = new SFTPServer("127.0.0.1", 10022, "testUser", "testpassword", null);
		assertThat(packetUploader.createSFTPChannel(configuration), isA(SFTPChannel.class));

	}

	@Test(expected = IllegalConfigurationException.class)
	public void testCreateSFTPEmptyKeyChannel() throws ConnectionException {
		configuration = new SFTPServer("127.0.0.1", 10022, "testUser", "", null, "/");
		assertThat(packetUploader.createSFTPChannel(configuration), isA(SFTPChannel.class));

	}

	@Test(expected = IllegalConfigurationException.class)
	public void testCreateSFTPIllegalKeyChannel() throws ConnectionException {
		configuration = new SFTPServer("127.0.0.1", 10022, "testUser", null, null, "/");
		configuration.setPassword("");
		assertThat(packetUploader.createSFTPChannel(configuration), isA(SFTPChannel.class));

	}

	@Test(expected = IllegalConfigurationException.class)
	public void testCreateSFTPEmptyPasswordChannel() throws ConnectionException {
		configuration = new SFTPServer("127.0.0.1", 10022, "testUser", "", "/");
		configuration.setPrivateKeyFileName("");
		assertThat(packetUploader.createSFTPChannel(configuration), isA(SFTPChannel.class));

	}

	@Test(expected = NullConfigurationException.class)
	public void testCreateSFTPNullPasswordChannel() throws ConnectionException {
		configuration = new SFTPServer("127.0.0.1", 10022, "testUser", null, "/");
		assertThat(packetUploader.createSFTPChannel(configuration), isA(SFTPChannel.class));

	}

	@Test(expected = IllegalConfigurationException.class)
	public void testCreateSFTPMinPortChannel() throws ConnectionException {
		configuration = new SFTPServer("127.0.0.1", -1, "testUser", "testpassword", "/");
		assertThat(packetUploader.createSFTPChannel(configuration), isA(SFTPChannel.class));

	}

	@Test(expected = IllegalConfigurationException.class)
	public void testCreateSFTPMaxPortChannel() throws ConnectionException {
		configuration = new SFTPServer("127.0.0.1", 65536, "testUser", "testpassword", "/");
		packetUploader.createSFTPChannel(configuration);

	}

	@Test(expected = NullConfigurationException.class)
	public void testCreateNUllConfigurationtChannel() throws ConnectionException {

		packetUploader.createSFTPChannel(null);

	}
}
