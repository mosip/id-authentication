
package org.mosip.registration.processor.file.system.connector.service.test;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import org.mosip.registration.processor.packet.manager.PacketManagerApp;
import org.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author M1022006
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PacketManagerApp.class)
@TestPropertySource({ "classpath:application.properties" })
public class CleanUpServiceTest {

	@Autowired
	private FileManager<DirectoryPathDto, InputStream> fileManager;

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
		fileManager.put(fileName, new FileInputStream(file), DirectoryPathDto.LANDING_ZONE);
		fileManager.put(fileName, new FileInputStream(file), DirectoryPathDto.VIRUS_SCAN);
		fileManager.cleanUpFile(DirectoryPathDto.LANDING_ZONE, DirectoryPathDto.VIRUS_SCAN, fileNameWithoutExtn);
		boolean exists = fileManager.checkIfFileExists(DirectoryPathDto.LANDING_ZONE, fileNameWithoutExtn);
		assertFalse(exists);

	}

}
