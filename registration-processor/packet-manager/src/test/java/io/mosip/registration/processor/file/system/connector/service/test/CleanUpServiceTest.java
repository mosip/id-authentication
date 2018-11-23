
package io.mosip.registration.processor.file.system.connector.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.PacketManagerApp;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInSourceException;
import io.mosip.registration.processor.packet.manager.service.impl.FileManagerImpl;

/**
 * @author M1022006
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PacketManagerApp.class)
@RefreshScope
public class CleanUpServiceTest {

	@Autowired
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	@Mock
	private FileManagerImpl fileManagerImpl;

	private File file;

	private Environment env = mock(Environment.class);

	@Autowired
	private Environment testEnvironment;

	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		file = new File(classLoader.getResource("1001.zip").getFile());
		when(env.getProperty("LANDING_ZONE")).thenReturn(testEnvironment.getProperty("LANDING_ZONE"));
		when(env.getProperty("VIRUS_SCAN")).thenReturn(testEnvironment.getProperty("VIRUS_SCAN"));

	}

	@Test
	public void cleanUpFileSuccessCheck() throws IOException {
		String fileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.LANDING_ZONE);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN);
		fileManager.cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileNameWithoutExtn);
		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.LANDING_ZONE, fileNameWithoutExtn);
		assertFalse(exists);

	}

	@Test(expected = FileNotFoundInDestinationException.class)
	public void cleanUpFileDestinationFailureCheck() throws IOException {

		String fileName = "Destination.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileNameWithoutExtn);

	}

	@Test(expected = FileNotFoundInSourceException.class)
	public void cleanUpFileSourceFailureCheck() throws IOException {

		String fileName = "1002.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN);

		fileManager.cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileNameWithoutExtn);

	}

	@Test
	public void cleanUpFileChildSuccessCheck() throws IOException {
		String childFileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(childFileName);
		fileManager.put("child" + File.separator + fileNameWithoutExtn, new FileInputStream(file),
				DirectoryPathDto.LANDING_ZONE);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN);
		fileManager.cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileNameWithoutExtn,
				"child");

		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.LANDING_ZONE,
				"child" + File.separator + childFileName);
		assertFalse(exists);

	}

	@Test(expected = FileNotFoundInDestinationException.class)
	public void cleanUpFileChildDestinationFailureCheck() throws IOException {

		String fileName = "Destination.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileNameWithoutExtn,
				"child");
	}

	@Test(expected = FileNotFoundInSourceException.class)
	public void cleanUpFileChildSourceFailureCheck() throws IOException {

		String fileName = "1002.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN);

		fileManager.cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileNameWithoutExtn,
				"child");
	}
	
	@Test
	public void deleteSuccess() throws FileNotFoundException, IOException {
		String fileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.LANDING_ZONE);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN);
		
		fileManager.deletePacket(DirectoryPathDto.LANDING_ZONE, fileNameWithoutExtn);
		
		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.LANDING_ZONE,fileNameWithoutExtn);
		assertEquals("Deleted file",false, exists);
	}
	
	@Test(expected = FileNotFoundInSourceException.class)
	public void deleteFail() throws FileNotFoundException, IOException {
		fileManager.deletePacket(DirectoryPathDto.FTP_ZONE, "test");

	}
	
	@Test
	public void copyTest() throws FileNotFoundException, IOException {
		String fileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.LANDING_ZONE);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN);
		fileManager.copy(fileNameWithoutExtn,DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN);
	}

}
