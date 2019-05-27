package io.mosip.registration.test.update;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.update.SoftwareUpdateHandler;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SoftwareUpdateHandler.class })
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

	@Before
	public void initialize() throws Exception {
		//initMocks(this);
		
		mockFile = PowerMockito.mock(File.class);

		PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(mockFile);
		PowerMockito.whenNew(FileInputStream.class).withAnyArguments().thenReturn(mockFileStream);
		
		//softwareUpdateHandler = new SoftwareUpdateHandler();

	}

	@Test
	public void executeSQLTest() throws Exception {
		initialize();

		//Mockito.when(this.getClass().getResource(Mockito.anyString())).thenReturn(null);
		
		
		Mockito.doNothing().when(globalParamService).update(Mockito.anyString(), Mockito.anyString());
		Assert.assertSame(RegistrationConstants.SQL_EXECUTION_SUCCESS,
				softwareUpdateHandler.executeSqlFile("0.11.0", "0.12.5").getSuccessResponseDTO().getMessage());
	}

}
