package io.mosip.registration.test.update;

import java.io.File;
import java.io.FileInputStream;
import java.util.jar.Manifest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.update.SoftwareUpdateHandler;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Manifest.class})
public class SoftwareUpdateHandlerTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	@InjectMocks
	private SoftwareUpdateHandler softwareUpdateHandler;

	@Mock
	private File mockFile;

	@Mock
	private FileInputStream mockFileStream;

	@Mock
	private GlobalParamService globalParamService;

	@Mock
	private JdbcTemplate jdbcTemplate;
	
	@Mock
	private Manifest manifest;

	@Before
	public void initialize() throws Exception {
		// initMocks(this);
		//
		// mockFile = PowerMockito.mock(File.class);
		//
		// PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(mockFile);
		// PowerMockito.whenNew(FileInputStream.class).withAnyArguments().thenReturn(mockFileStream);

		// softwareUpdateHandler = new SoftwareUpdateHandler();

	}

	@Test
	public void executeSQLTest() throws Exception {

		// SoftwareUpdateHandler softwareUpdateHandler =new SoftwareUpdateHandler();

		Mockito.doNothing().when(globalParamService).update(Mockito.anyString(), Mockito.anyString());

		Mockito.doNothing().when(jdbcTemplate).execute(Mockito.anyString());
		Assert.assertSame(RegistrationConstants.SQL_EXECUTION_SUCCESS,
				softwareUpdateHandler.executeSqlFile("0.11.0", "0.12.5").getSuccessResponseDTO().getMessage());

	}

	@Ignore
	@Test
	public void executeSQLTestRollBack() throws Exception {

		// SoftwareUpdateHandler softwareUpdateHandler =new SoftwareUpdateHandler();

		// Mockito.doNothing().when(globalParamService).update(Mockito.anyString(),
		// Mockito.anyString());

		System.setProperty("user.dir", "src/test/resources/");
		Mockito.doThrow(RuntimeException.class).when(jdbcTemplate).execute(Mockito.anyString());

		SoftwareUpdateHandler softwareUpdateHandle = new SoftwareUpdateHandler();
		Assert.assertNotNull(softwareUpdateHandle.executeSqlFile("0.11.0", "0.12.5").getErrorResponseDTOs());

	}

	@Test
	public void setTimestapTest() {
		softwareUpdateHandler.setLatestVersionReleaseTimestamp("20190520091122");
		Assert.assertNotNull(softwareUpdateHandler.getLatestVersionReleaseTimestamp());
	}
	
	@Test
	public void hasUpdateTest() {
		Assert.assertFalse(softwareUpdateHandler.hasUpdate());
	}

	
/*	@Test
	public void hasUpdateGetCurrentVersionTest() {
		
		Attributes attributes=new Attributes();
		
		Mockito.when(manifest.getMainAttributes()).thenReturn(attributes);
		
		Assert.assertFalse(softwareUpdateHandler.hasUpdate());
	}*/
}
