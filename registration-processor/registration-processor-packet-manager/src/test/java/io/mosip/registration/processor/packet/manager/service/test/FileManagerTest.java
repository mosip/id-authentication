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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
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
@RefreshScope
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = PacketManagerConfigTest.class)
public class FileManagerTest {

	/** The file. */
	private File file;

	/** The env. */
	@Mock
	private Environment env;

	@Mock
	private SftpJschConnectionDto sftpDto = new SftpJschConnectionDto();

	/** The virus scan enc. */
	@Value("${ARCHIVE_LOCATION}")
	private String ARCHIVE_LOCATION;

	/** The virus scan dec. */
	@Value("${LANDING_ZONE}")
	private String LANDING_ZONE;

	@Value("${registration.processor.packet.ext}")
	private String extention;

	@Mock
	private ChannelSftp sftp;

	@Mock
	private Session session;

	JSch jSch = new JSch();

	@InjectMocks
	private FileManager<DirectoryPathDto, InputStream> impl = new FileManagerImpl() {
		@Override
		public ChannelSftp getSftpConnection(SftpJschConnectionDto sftpConnectionDto) throws JschConnectionException {
			return sftp;

		}
	};

	@Mock
	private InputStream is;

	/**
	 * Sets the up.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		ReflectionTestUtils.setField(impl, "extension", ".zip");
		file = new File(classLoader.getResource("1001.zip").getFile());
		is = new FileInputStream(file);
		when(env.getProperty(DirectoryPathDto.ARCHIVE_LOCATION.toString())).thenReturn(ARCHIVE_LOCATION);
		when(env.getProperty(DirectoryPathDto.LANDING_ZONE.toString())).thenReturn(LANDING_ZONE);
		sftpDto.setHost("localhost");
		sftpDto.setPort(8080);
		sftpDto.setProtocal("http");
		sftpDto.setUser("System");
	}

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
		impl.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.ARCHIVE_LOCATION);
		boolean exists = impl.checkIfFileExists(DirectoryPathDto.ARCHIVE_LOCATION, fileNameWithoutExtn);
		assertTrue(exists);
		impl.copy(fileNameWithoutExtn, DirectoryPathDto.ARCHIVE_LOCATION, DirectoryPathDto.LANDING_ZONE);
		boolean fileExists = impl.checkIfFileExists(DirectoryPathDto.LANDING_ZONE, fileNameWithoutExtn);
		assertTrue(fileExists);
	}

	@Test
	public void testFilemanagerGetFile() throws IOException {
		File newFile = new File("Abc.zip");
		String fileName = newFile.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		impl.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.ARCHIVE_LOCATION);
		File f = impl.getFile(DirectoryPathDto.ARCHIVE_LOCATION, fileNameWithoutExtn);
		assertEquals(f.getName(), newFile.getName());

	}

	@Test
	public void testGetFileByteArray() throws Exception {
		File newFile = new File("Abc.zip");
		String fileName = newFile.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		// Mockito.when(jSch.getSession(Mockito.any(), Mockito.any(),
		// Mockito.any())).thenReturn(session);
		Mockito.doNothing().when(session).connect();
		Mockito.when(session.openChannel(Mockito.any())).thenReturn(sftp);
		Mockito.doNothing().when(sftp).connect();
		Mockito.when(sftp.get(Mockito.any())).thenReturn(is);
		impl.getFile(DirectoryPathDto.ARCHIVE_LOCATION, fileNameWithoutExtn, sftpDto);

	}

	@Test(expected = SftpFileOperationException.class)
	public void testSftpExceptoin() throws Exception {
		File newFile = new File("Abc.zip");
		String fileName = newFile.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		Mockito.doNothing().when(session).connect();
		Mockito.when(session.openChannel(Mockito.any())).thenReturn(sftp);
		Mockito.doNothing().when(sftp).connect();
		Mockito.when(sftp.get(Mockito.any())).thenThrow(new SftpException(0, fileNameWithoutExtn));
		impl.getFile(DirectoryPathDto.ARCHIVE_LOCATION, fileNameWithoutExtn, sftpDto);

	}

	@Test
	public void testCopyFile() throws Exception {
		File newFile = new File("Abc.zip");
		String fileName = newFile.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		Mockito.doNothing().when(session).connect();
		Mockito.when(session.openChannel(Mockito.any())).thenReturn(sftp);
		Mockito.doNothing().when(sftp).connect();
		Mockito.when(sftp.get(Mockito.any())).thenReturn(is);
		impl.copy(fileNameWithoutExtn, DirectoryPathDto.ARCHIVE_LOCATION, DirectoryPathDto.LANDING_ZONE, sftpDto);
	}

	@Test
	public void testCleanUpFile() throws Exception {
		File newFile = new File("Abc.zip");
		String fileName = newFile.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		Mockito.doNothing().when(session).connect();
		Mockito.when(session.openChannel(Mockito.any())).thenReturn(sftp);
		Mockito.doNothing().when(sftp).connect();
		Mockito.when(sftp.get(Mockito.any())).thenReturn(is);
		impl.cleanUp(fileNameWithoutExtn, DirectoryPathDto.ARCHIVE_LOCATION, DirectoryPathDto.LANDING_ZONE, sftpDto);
	}

	@Test(expected = SftpFileOperationException.class)
	public void testSftpExceptoinForCopyFile() throws Exception {
		File newFile = new File("Abc.zip");
		String fileName = newFile.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		// Mockito.when(jSch.getSession(Mockito.any(), Mockito.any(),
		// Mockito.any())).thenReturn(session);
		Mockito.doNothing().when(session).connect();
		Mockito.when(session.openChannel(Mockito.any())).thenReturn(sftp);
		Mockito.doNothing().when(sftp).connect();
		Mockito.when(sftp.get(Mockito.any())).thenThrow(new SftpException(0, fileNameWithoutExtn));
		impl.copy(fileNameWithoutExtn, DirectoryPathDto.ARCHIVE_LOCATION, DirectoryPathDto.LANDING_ZONE, sftpDto);
	}

	@Test(expected = SftpFileOperationException.class)
	public void testCleanUpException() throws Exception {
		File newFile = new File("Abc.zip");
		String fileName = newFile.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		Mockito.doNothing().when(session).connect();
		Mockito.when(session.openChannel(Mockito.any())).thenReturn(sftp);
		Mockito.doNothing().when(sftp).connect();
		Mockito.when(sftp.get(Mockito.any())).thenThrow(new SftpException(0, fileNameWithoutExtn));
		impl.cleanUp(fileNameWithoutExtn, DirectoryPathDto.ARCHIVE_LOCATION, DirectoryPathDto.LANDING_ZONE, sftpDto);
	}

}