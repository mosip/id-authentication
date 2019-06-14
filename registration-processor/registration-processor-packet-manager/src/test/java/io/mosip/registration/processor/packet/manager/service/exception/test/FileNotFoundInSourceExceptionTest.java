package io.mosip.registration.processor.packet.manager.service.exception.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.manager.exception.FileNotFoundInSourceException;
import io.mosip.registration.processor.packet.manager.service.impl.FileManagerImpl;

@RunWith(SpringRunner.class)
public class FileNotFoundInSourceExceptionTest {

	/** The virus scan enc. */
	@Value("${ARCHIVE_LOCATION}")
	private String ARCHIVE_LOCATION;

	/** The virus scan dec. */
	@Value("${LANDING_ZONE}")
	private String LANDING_ZONE;

	@Value("${registration.processor.packet.ext}")
	private String extention;

	
	@InjectMocks
	private FileManagerImpl fileManager = new FileManagerImpl() {
		@Override
		public String getExtension() {
			return ".zip";
		}
	};
	
	@Mock
	private Environment env;
	
	@Before
	public void setUp() throws Exception {
	Mockito.when(env.getProperty(DirectoryPathDto.ARCHIVE_LOCATION.toString())).thenReturn(ARCHIVE_LOCATION);
	Mockito.when(env.getProperty(DirectoryPathDto.LANDING_ZONE.toString())).thenReturn(LANDING_ZONE);
	Mockito.when(env.getProperty("registration.processor.packet.ext")).thenReturn(extention);

	}
	
	@Test
	@Ignore
	public void TestFileNotFoundInSourceException() {

		String fileName = "sample.zip";
		FileNotFoundInSourceException ex = new FileNotFoundInSourceException(
				PlatformErrorMessages.RPR_PKM_FILE_NOT_FOUND_IN_SOURCE.getMessage());
		
		try {
			fileManager.cleanUpFile(DirectoryPathDto.ARCHIVE_LOCATION, DirectoryPathDto.LANDING_ZONE, fileName);
			fail();
		} catch (FileNotFoundInSourceException e) {
			assertThat("Should throw File Not Found In Source Exception with correct error codes", e.getErrorCode()
					.equalsIgnoreCase(PlatformErrorMessages.RPR_PKM_FILE_NOT_FOUND_IN_SOURCE.getCode()));
			assertThat("Should throw File Not Found In Source Exception with correct messages", e.getErrorText()
					.equalsIgnoreCase(PlatformErrorMessages.RPR_PKM_FILE_NOT_FOUND_IN_SOURCE.getMessage()));

		}

	}

}
