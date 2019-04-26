package io.mosip.registration.processor.packet.manager.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import io.mosip.registration.processor.core.exception.JschConnectionException;
import io.mosip.registration.processor.core.exception.SftpFileOperationException;
import io.mosip.registration.processor.core.packet.dto.SftpJschConnectionDto;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.config.PacketManagerConfigTest;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.service.impl.FileManagerImpl;

/**
 * The Class FileManagerTest.
 *
 * @author M1022006
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@RefreshScope
@ContextConfiguration(classes = PacketManagerConfigTest.class)
public class FileManagerTest {

	/** The file manager. */
	@Autowired
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	/** The file. */
	private File file;

	/** The env. */
	@MockBean
	private Environment env;
	
	@Mock 
	private Environment env1;

	@Mock
	private SftpJschConnectionDto sftpDto = new SftpJschConnectionDto();

	/** The virus scan enc. */
	@Value("${VIRUS_SCAN_ENC}")
	private String virusScanEnc;

	/** The virus scan dec. */
	@Value("${VIRUS_SCAN_DEC}")
	private String virusScanDec;
	//@InjectMocks
	

	@Mock
	private ChannelSftp sftp = new ChannelSftp();

	@Mock
	private Session session;
	
	@InjectMocks
	private FileManagerImpl impl=new FileManagerImpl() {
		@Override
		public ChannelSftp getSftpConnection(SftpJschConnectionDto sftpConnectionDto) throws JschConnectionException {
			System.out.println("Hello");
			return sftp;
		
	}};

	@Mock
	private InputStream is;

	//SshServer sshd;
	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		ReflectionTestUtils.setField(impl, "extention", ".zip");
		file = new File(classLoader.getResource("1001.zip").getFile());
		is = new FileInputStream(file);
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_ENC.toString())).thenReturn(virusScanEnc);
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString())).thenReturn(virusScanDec);
		when(env1.getProperty(DirectoryPathDto.VIRUS_SCAN_ENC.toString())).thenReturn(virusScanEnc);
		when(env1.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString())).thenReturn(virusScanDec);
		//when(env.getProperty(Mockito.a))
		sftpDto.setHost("localhost");
		sftpDto.setPort(9700);
		sftpDto.setPpkFileLocation("src/test/resources");
		sftpDto.setProtocal("http");
		sftpDto.setUser("System");
		/*sshd = SshServer.setUpDefaultServer();
		sshd.setPort(22999);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));
		sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
			
		public boolean authenticate(String username, String password, ServerSession session) {
		// TODO Auto-generated method stub
		return true;
		}
		});
		CommandFactory myCommandFactory = new CommandFactory() {
		public Command createCommand(String command) {
		System.out.println("Command: " + command);
		return null;
		}
		};
		sshd.setCommandFactory(new ScpCommandFactory(myCommandFactory));
		List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>(); 
		namedFactoryList.add(new SftpSubsystem.Factory()); 
		sshd.setSubsystemFactories(namedFactoryList); sshd.start(); 		
		JSch jsch=new JSch();
		Channel channel;
		Hashtable<String,String> config = new Hashtable<>();
		config.put("StrictHostKeyChecking", "no");
		JSch.setConfig(config);
		//jsch.addIdentity("src/test/resources/Mosip_Private_key.ppk");
		session=jsch.getSession("remote-username", "localhost", 7878);
        session.setPassword("remote-password");
		
		session.connect();
		channel=session.openChannel("http");
		channel.connect();
		sftp=(ChannelSftp) channel;
		sftp.connect();*/

	}
//@After
//public void teardown() throws Exception { sshd.stop(); }

	/**
	 * Gets the put and if file exists and copy method check.
	 *
	 * @return the put and if file exists and copy method check
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void getPutAndIfFileExistsAndCopyMethodCheck() throws IOException {
		String fileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_ENC);
		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.VIRUS_SCAN_ENC, fileNameWithoutExtn);
		assertTrue(exists);
		fileManager.copy(fileNameWithoutExtn, DirectoryPathDto.VIRUS_SCAN_ENC, DirectoryPathDto.VIRUS_SCAN_DEC);
		boolean fileExists = fileManager.checkIfFileExists(DirectoryPathDto.VIRUS_SCAN_DEC, fileNameWithoutExtn);
		assertTrue(fileExists);
	}

	@Test
	public void testFilemanagerGetFile() throws IOException {
		File newFile = new File("Abc.zip");
		String fileName = newFile.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_ENC);
		File f = fileManager.getFile(DirectoryPathDto.VIRUS_SCAN_ENC, fileNameWithoutExtn);
		assertEquals(f.getName(), newFile.getName());

	}

	@Test
	//@Ignore
	public void testGetFileByteArray() throws Exception {
		File newFile = new File("Abc.zip");
		String fileName = newFile.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		//Mockito.when(jSch.getSession(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(session);
		Mockito.doNothing().when(session).connect();
		Mockito.when(session.openChannel(Mockito.any())).thenReturn(sftp);
		Mockito.doNothing().when(sftp).connect();
		Mockito.when(sftp.get(Mockito.any())).thenReturn(is);
		impl.getFile(DirectoryPathDto.VIRUS_SCAN_ENC, fileNameWithoutExtn, sftpDto);

	}
	
	@Test(expected=SftpFileOperationException.class)
	public void testSftpExceptoin() throws Exception
	{
		File newFile = new File("Abc.zip");
		String fileName = newFile.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		//Mockito.when(jSch.getSession(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(session);
		Mockito.doNothing().when(session).connect();
		Mockito.when(session.openChannel(Mockito.any())).thenReturn(sftp);
		Mockito.doNothing().when(sftp).connect();
		Mockito.when(sftp.get(Mockito.any())).thenThrow(new SftpException(0, fileNameWithoutExtn));
		impl.getFile(DirectoryPathDto.VIRUS_SCAN_ENC, fileNameWithoutExtn, sftpDto);
		
	}
	
	@Test
	public void testCopyFile() throws Exception
	{
		File newFile = new File("Abc.zip");
		String fileName = newFile.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		//Mockito.when(jSch.getSession(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(session);
		Mockito.doNothing().when(session).connect();
		Mockito.when(session.openChannel(Mockito.any())).thenReturn(sftp);
		Mockito.doNothing().when(sftp).connect();
		Mockito.when(sftp.get(Mockito.any())).thenReturn(is);
		impl.copy(fileNameWithoutExtn,DirectoryPathDto.VIRUS_SCAN_ENC,DirectoryPathDto.VIRUS_SCAN_DEC ,sftpDto);
	}
	
	@Test 
	public void testCleanUpFile() throws Exception
	{
		File newFile = new File("Abc.zip");
		String fileName = newFile.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		//Mockito.when(jSch.getSession(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(session);
		Mockito.doNothing().when(session).connect();
		Mockito.when(session.openChannel(Mockito.any())).thenReturn(sftp);
		Mockito.doNothing().when(sftp).connect();
		Mockito.when(sftp.get(Mockito.any())).thenReturn(is);
		impl.cleanUp(fileNameWithoutExtn,DirectoryPathDto.VIRUS_SCAN_ENC,DirectoryPathDto.VIRUS_SCAN_DEC ,sftpDto);
	}
	
	@Test(expected=SftpFileOperationException.class)
	public void testSftpExceptoinForCopyFile() throws Exception
	{
		File newFile = new File("Abc.zip");
		String fileName = newFile.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		//Mockito.when(jSch.getSession(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(session);
		Mockito.doNothing().when(session).connect();
		Mockito.when(session.openChannel(Mockito.any())).thenReturn(sftp);
		Mockito.doNothing().when(sftp).connect();
		Mockito.when(sftp.get(Mockito.any())).thenThrow(new SftpException(0, fileNameWithoutExtn));
		impl.copy(fileNameWithoutExtn,DirectoryPathDto.VIRUS_SCAN_ENC,DirectoryPathDto.VIRUS_SCAN_DEC ,sftpDto);
	}
	
	@Test(expected=SftpFileOperationException.class)
	public void testCleanUpException() throws Exception
	{
		File newFile = new File("Abc.zip");
		String fileName = newFile.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		//Mockito.when(jSch.getSession(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(session);
		Mockito.doNothing().when(session).connect();
		Mockito.when(session.openChannel(Mockito.any())).thenReturn(sftp);
		Mockito.doNothing().when(sftp).connect();
		Mockito.when(sftp.get(Mockito.any())).thenThrow(new SftpException(0, fileNameWithoutExtn));
		impl.cleanUp(fileNameWithoutExtn,DirectoryPathDto.VIRUS_SCAN_ENC,DirectoryPathDto.VIRUS_SCAN_DEC ,sftpDto);	
	}

}
