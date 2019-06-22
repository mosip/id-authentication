
package io.mosip.registration.processor.packet.manager.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.packet.manager.config.PacketManagerConfigTest;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInDestinationException;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInSourceException;
import io.mosip.registration.processor.packet.manager.service.impl.FileManagerImpl;

/**
 * @author M1022006
 *
 */
@RefreshScope
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = PacketManagerConfigTest.class)
@TestPropertySource(locations = "classpath:application.properties")
public class CleanUpServiceTest {

	@InjectMocks
	private FileManagerImpl fileManager = new FileManagerImpl() {
		@Override
		public String getExtension() {
			return ".zip";
		}
	};

	private File file;

	/** The virus scan enc. */
	@Value("${ARCHIVE_LOCATION}")
	private String ARCHIVE_LOCATION;

	/** The virus scan dec. */
	@Value("${LANDING_ZONE}")
	private String LANDING_ZONE;

	@Value("${registration.processor.packet.ext}")
	private String extention;

	@Mock
	private Environment env;

	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		file = new File(classLoader.getResource("1001.zip").getFile());
		// Mockito.when(env.getProperty(any()).thenReturn("");
		when(env.getProperty(DirectoryPathDto.ARCHIVE_LOCATION.toString())).thenReturn(ARCHIVE_LOCATION);
		when(env.getProperty(DirectoryPathDto.LANDING_ZONE.toString())).thenReturn(LANDING_ZONE);
		when(env.getProperty("registration.processor.packet.ext")).thenReturn(extention);

	}

	@Test
	public void cleanUpFileSuccessCheck() throws IOException {
		String fileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.ARCHIVE_LOCATION);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.LANDING_ZONE);
		fileManager.cleanUpFile(DirectoryPathDto.ARCHIVE_LOCATION, DirectoryPathDto.LANDING_ZONE, fileNameWithoutExtn);
		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.ARCHIVE_LOCATION, fileNameWithoutExtn);
		assertFalse(exists);

	}

	@Test
	public void cleanUpFileIOExceptionTest() throws IOException {
		String fileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.ARCHIVE_LOCATION);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.LANDING_ZONE);
		fileManager.cleanUpFile(DirectoryPathDto.ARCHIVE_LOCATION, DirectoryPathDto.LANDING_ZONE, fileNameWithoutExtn);
		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.ARCHIVE_LOCATION, fileNameWithoutExtn);
		assertFalse(exists);

	}

	@Test(expected = FileNotFoundInDestinationException.class)
	public void cleanUpFileDestinationFailureCheck() throws IOException {

		String fileName = "Destination.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.cleanUpFile(DirectoryPathDto.ARCHIVE_LOCATION, DirectoryPathDto.LANDING_ZONE, fileNameWithoutExtn);
		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.LANDING_ZONE, fileNameWithoutExtn);
		assertFalse(exists);//
	}

	@Test(expected = FileNotFoundInSourceException.class)
	public void cleanUpFileSourceFailureCheck() throws IOException {

		String fileName = "1002.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.LANDING_ZONE);

		fileManager.cleanUpFile(DirectoryPathDto.ARCHIVE_LOCATION, DirectoryPathDto.LANDING_ZONE, fileNameWithoutExtn);
		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.ARCHIVE_LOCATION, fileNameWithoutExtn);
		assertFalse(exists);

	}

	@Test
	public void cleanUpFileChildSuccessCheck() throws IOException {
		String childFileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(childFileName);
		fileManager.put("child" + File.separator + fileNameWithoutExtn, new FileInputStream(file),
				DirectoryPathDto.ARCHIVE_LOCATION);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.LANDING_ZONE);
		fileManager.cleanUpFile(DirectoryPathDto.ARCHIVE_LOCATION, DirectoryPathDto.LANDING_ZONE, fileNameWithoutExtn,
				"child");

		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.ARCHIVE_LOCATION,
				"child" + File.separator + childFileName);
		assertFalse(exists);

	}

	@Test(expected = FileNotFoundInDestinationException.class)
	public void cleanUpFileChildDestinationFailureCheck() throws IOException {

		String fileName = "Destination.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.cleanUpFile(DirectoryPathDto.ARCHIVE_LOCATION, DirectoryPathDto.LANDING_ZONE, fileNameWithoutExtn,
				"child");
		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.ARCHIVE_LOCATION, fileNameWithoutExtn);
		assertFalse(exists);
	}

	@Test(expected = FileNotFoundInSourceException.class)
	public void cleanUpFileChildSourceFailureCheck() throws IOException {

		String fileName = "1002.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.LANDING_ZONE);

		fileManager.cleanUpFile(DirectoryPathDto.ARCHIVE_LOCATION, DirectoryPathDto.LANDING_ZONE, fileNameWithoutExtn,
				"child");
	}

	@Test
	public void deleteSuccess() throws FileNotFoundException, IOException {
		String fileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.ARCHIVE_LOCATION);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.LANDING_ZONE);

		fileManager.deletePacket(DirectoryPathDto.ARCHIVE_LOCATION, fileNameWithoutExtn);
		fileManager.put("child" + File.separator + fileNameWithoutExtn, new FileInputStream(file),
				DirectoryPathDto.ARCHIVE_LOCATION);
		fileManager.deleteFolder(DirectoryPathDto.ARCHIVE_LOCATION, "child");

		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.ARCHIVE_LOCATION, fileNameWithoutExtn);
		assertEquals("Deleted file", false, exists);
	}

	@Test(expected = FileNotFoundInSourceException.class)
	public void deleteFailureTest() throws FileNotFoundException, IOException {
		String fileName = "1002.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);

		fileManager.deletePacket(DirectoryPathDto.ARCHIVE_LOCATION, fileNameWithoutExtn);

	}

	@Test
	public void copyTest() throws FileNotFoundException, IOException {
		String fileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.ARCHIVE_LOCATION);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.LANDING_ZONE);
		fileManager.copy(fileNameWithoutExtn, DirectoryPathDto.ARCHIVE_LOCATION, DirectoryPathDto.LANDING_ZONE);
	}

	@Test
	public void getFileTest() throws FileNotFoundException, IOException {
		String fileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		File file = FileUtils.getFile(DirectoryPathDto.ARCHIVE_LOCATION.toString(), fileName);
		File getFile = fileManager.getFile(DirectoryPathDto.ARCHIVE_LOCATION, fileNameWithoutExtn);
		assertEquals(file.getName().trim(), getFile.getName().trim());
	}

}
