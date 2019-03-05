package io.mosip.registration.processor.packet.manager.service.test;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.config.PacketManagerConfigTest;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;

/**
 * @author M1022006
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@RefreshScope
@ContextConfiguration(classes = PacketManagerConfigTest.class)
public class FileManagerTest {

	@Autowired
	private FileManager<DirectoryPathDto, InputStream> fileManager;

	private File file;

	@MockBean
	private Environment env;

	@Value("${VIRUS_SCAN_ENC}")
	private String virusScanEnc;

	@Value("${VIRUS_SCAN_DEC}")
	private String virusScanDec;

	@Before
	public void setUp() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		file = new File(classLoader.getResource("1001.zip").getFile());
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_ENC.toString())).thenReturn(virusScanEnc);
		when(env.getProperty(DirectoryPathDto.VIRUS_SCAN_DEC.toString())).thenReturn(virusScanDec);
	}

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

}
