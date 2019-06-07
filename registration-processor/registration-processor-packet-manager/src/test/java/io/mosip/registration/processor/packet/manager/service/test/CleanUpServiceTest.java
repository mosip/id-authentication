
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
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

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
@RunWith(PowerMockRunner.class)
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

	private String virusScanEnc = "src/test/resources/encrypted";

	private String virusScanDec = "src/test/resources/decrypted";

	@Value("${registration.processor.packet.ext}")
	private String extention;

	@Mock
	private Environment env;

	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		file = new File(classLoader.getResource("1001.zip").getFile());
		// Mockito.when(env.getProperty(any()).thenReturn("");
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_ENC.toString())).thenReturn(virusScanEnc);
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString())).thenReturn(virusScanDec);
		when(env.getProperty("registration.processor.packet.ext")).thenReturn(extention);

	}

	@Test
	// @Ignore
	public void cleanUpFileSuccessCheck() throws IOException {
		String fileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_ENC);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_DEC);
		fileManager.cleanUpFile(DirectoryPathDto.VIRUS_SCAN_ENC, DirectoryPathDto.VIRUS_SCAN_DEC, fileNameWithoutExtn);
		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.VIRUS_SCAN_ENC, fileNameWithoutExtn);
		assertFalse(exists);

	}

	@Test
	// @Ignore
	public void cleanUpFileIOExceptionTest() throws IOException {
		String fileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_ENC);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_DEC);
		fileManager.cleanUpFile(DirectoryPathDto.VIRUS_SCAN_ENC, DirectoryPathDto.VIRUS_SCAN_DEC, fileNameWithoutExtn);
		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.VIRUS_SCAN_ENC, fileNameWithoutExtn);
		assertFalse(exists);

	}

	@Test(expected = FileNotFoundInDestinationException.class)
	// @Ignore
	public void cleanUpFileDestinationFailureCheck() throws IOException {

		String fileName = "Destination.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.cleanUpFile(DirectoryPathDto.VIRUS_SCAN_ENC, DirectoryPathDto.VIRUS_SCAN_DEC, fileNameWithoutExtn);
		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.VIRUS_SCAN_DEC, fileNameWithoutExtn);
		assertFalse(exists);//
	}

	@Test(expected = FileNotFoundInSourceException.class)
	// @Ignore
	public void cleanUpFileSourceFailureCheck() throws IOException {

		String fileName = "1002.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_DEC);

		fileManager.cleanUpFile(DirectoryPathDto.VIRUS_SCAN_ENC, DirectoryPathDto.VIRUS_SCAN_DEC, fileNameWithoutExtn);
		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.VIRUS_SCAN_ENC, fileNameWithoutExtn);
		assertFalse(exists);

	}

	@Test
	// @Ignore
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
	// @Ignore
	public void cleanUpFileChildDestinationFailureCheck() throws IOException {

		String fileName = "Destination.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.cleanUpFile(DirectoryPathDto.VIRUS_SCAN_ENC, DirectoryPathDto.VIRUS_SCAN_DEC, fileNameWithoutExtn,
				"child");
		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.VIRUS_SCAN_ENC, fileNameWithoutExtn);
		assertFalse(exists);
	}

	@Test(expected = FileNotFoundInSourceException.class)
	// @Ignore
	public void cleanUpFileChildSourceFailureCheck() throws IOException {

		String fileName = "1002.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_DEC);

		fileManager.cleanUpFile(DirectoryPathDto.VIRUS_SCAN_ENC, DirectoryPathDto.VIRUS_SCAN_DEC, fileNameWithoutExtn,
				"child");
	}

	@Test
	// @Ignore
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

	// @Ignore
	@Test(expected = FileNotFoundInSourceException.class)
	public void deleteFailureTest() throws FileNotFoundException, IOException {
		String fileName = "1002.zip";
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);

		fileManager.deletePacket(DirectoryPathDto.VIRUS_SCAN_ENC, fileNameWithoutExtn);

	}

	@Test
	// @Ignore
	public void copyTest() throws FileNotFoundException, IOException {
		String fileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_ENC);
		fileManager.put(fileNameWithoutExtn, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN_DEC);
		fileManager.copy(fileNameWithoutExtn, DirectoryPathDto.VIRUS_SCAN_ENC, DirectoryPathDto.VIRUS_SCAN_DEC);
	}

	@Test
	// @Ignore
	public void getFileTest() throws FileNotFoundException, IOException {
		String fileName = file.getName();
		String fileNameWithoutExtn = FilenameUtils.removeExtension(fileName);
		File file = FileUtils.getFile(DirectoryPathDto.VIRUS_SCAN_ENC.toString(), fileName);
		File getFile = fileManager.getFile(DirectoryPathDto.VIRUS_SCAN_ENC, fileNameWithoutExtn);
		assertEquals(file.getName().trim(), getFile.getName().trim());
	}

}
