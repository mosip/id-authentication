
package io.mosip.registration.processor.packet.manager.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.config.PacketManagerConfigTest;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInSourceException;
import io.mosip.registration.processor.packet.manager.service.impl.FileManagerImpl;

/**
 * @author M1022006
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = PacketManagerConfigTest.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CleanUpServiceTest {

	@Autowired
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	@Mock
	private FileManagerImpl fileManagerImpl;

	private File file;

	@Mock
	private Environment env;

	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		file = new File(classLoader.getResource("1001.zip").getFile());
		// when(env.getProperty("VIRUS_SCAN_ENC")).thenReturn(testEnvironment.getProperty("VIRUS_SCAN_ENC"));
		// when(env.getProperty("VIRUS_SCAN_DEC")).thenReturn(testEnvironment.getProperty("VIRUS_SCAN_DEC"));

	}

	@Test
	@Ignore
	public void cleanUpFileSuccessCheck() throws IOException {
		String fileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_ENC);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_DEC);
		fileManager.cleanUpFile(DirectoryPathDto.VIRUS_SCAN_ENC, DirectoryPathDto.VIRUS_SCAN_DEC, fileNameWithoutExtn);
		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.VIRUS_SCAN_ENC, fileNameWithoutExtn);
		assertFalse(exists);

	}

	@Test(expected = FileNotFoundInDestinationException.class)
	@Ignore
	public void cleanUpFileDestinationFailureCheck() throws IOException {

		String fileName = "Destination.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.cleanUpFile(DirectoryPathDto.VIRUS_SCAN_ENC, DirectoryPathDto.VIRUS_SCAN_DEC, fileNameWithoutExtn);

	}

	@Test(expected = FileNotFoundInSourceException.class)
	@Ignore
	public void cleanUpFileSourceFailureCheck() throws IOException {

		String fileName = "1002.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_DEC);

		fileManager.cleanUpFile(DirectoryPathDto.VIRUS_SCAN_ENC, DirectoryPathDto.VIRUS_SCAN_DEC, fileNameWithoutExtn);

	}

	@Test
	@Ignore
	public void cleanUpFileChildSuccessCheck() throws IOException {
		String childFileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(childFileName);
		fileManager.put("child" + File.separator + fileNameWithoutExtn, new FileInputStream(file),
				DirectoryPathDto.VIRUS_SCAN_ENC);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_DEC);
		fileManager.cleanUpFile(DirectoryPathDto.VIRUS_SCAN_ENC, DirectoryPathDto.VIRUS_SCAN_DEC, fileNameWithoutExtn,
				"child");

		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.VIRUS_SCAN_ENC,
				"child" + File.separator + childFileName);
		assertFalse(exists);

	}

	@Test(expected = FileNotFoundInDestinationException.class)
	@Ignore
	public void cleanUpFileChildDestinationFailureCheck() throws IOException {

		String fileName = "Destination.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.cleanUpFile(DirectoryPathDto.VIRUS_SCAN_ENC, DirectoryPathDto.VIRUS_SCAN_DEC, fileNameWithoutExtn,
				"child");
	}

	@Test(expected = FileNotFoundInSourceException.class)
	@Ignore
	public void cleanUpFileChildSourceFailureCheck() throws IOException {

		String fileName = "1002.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_DEC);

		fileManager.cleanUpFile(DirectoryPathDto.VIRUS_SCAN_ENC, DirectoryPathDto.VIRUS_SCAN_DEC, fileNameWithoutExtn,
				"child");
	}

	@Test
	@Ignore
	public void deleteSuccess() throws FileNotFoundException, IOException {
		String fileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_ENC);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_DEC);

		fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_ENC, fileNameWithoutExtn);
		fileManager.put("child" + File.separator + fileNameWithoutExtn, new FileInputStream(file),
				DirectoryPathDto.VIRUS_SCAN_ENC);
		fileManager.deleteFolder(DirectoryPathDto.VIRUS_SCAN_ENC, "child");

		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.VIRUS_SCAN_ENC, fileNameWithoutExtn);
		assertEquals("Deleted file", false, exists);
	}

	@Test
	@Ignore
	public void copyTest() throws FileNotFoundException, IOException {
		String fileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_ENC);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_DEC);
		fileManager.copy(fileNameWithoutExtn, DirectoryPathDto.VIRUS_SCAN_ENC, DirectoryPathDto.VIRUS_SCAN_DEC);
	}

}
