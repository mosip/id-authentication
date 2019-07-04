package io.mosip.registration.tpm.initialize;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.logger.logback.impl.LoggerImpl;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.exception.RegBaseCheckedException;

import tss.Tpm;
import tss.TpmFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TpmFactory.class, AppConfig.class})
public class TPMInitializationTest {

	@BeforeClass
	public static void mockTPMLogger() throws Exception {
		PowerMockito.mockStatic(AppConfig.class);

		Logger mockedLogger = PowerMockito.mock(LoggerImpl.class);

		PowerMockito.doReturn(mockedLogger).when(AppConfig.class, "getLogger", Mockito.any(Class.class));

		PowerMockito.doNothing().when(mockedLogger, "error", Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
		PowerMockito.doNothing().when(mockedLogger, "info", Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void testTPMGetInstance() throws Exception {
		Tpm tpm = PowerMockito.mock(Tpm.class);

		PowerMockito.mockStatic(TpmFactory.class);

		ReflectionTestUtils.setField(TPMInitialization.class, "tpm", null);
		PowerMockito.doReturn(tpm).when(TpmFactory.class, "platformTpm");

		Assert.assertSame(tpm, TPMInitialization.getTPMInstance());
	}

	@Test
	public void testTPMGetExistingInstance() throws Exception {
		Tpm tpm = PowerMockito.mock(Tpm.class);

		ReflectionTestUtils.setField(TPMInitialization.class, "tpm", tpm);

		Assert.assertSame(tpm, TPMInitialization.getTPMInstance());
	}

	@Test
	public void testCloseTPMInstance() throws Exception {
		Tpm tpm = PowerMockito.mock(Tpm.class);

		ReflectionTestUtils.setField(TPMInitialization.class, "tpm", tpm);
		PowerMockito.doNothing().when(tpm, "close");

		TPMInitialization.closeTPMInstance();
	}

	@Test
	public void testCloseNullTPMInstance() throws Exception {
		ReflectionTestUtils.setField(TPMInitialization.class, "tpm", null);

		TPMInitialization.closeTPMInstance();
	}

	@Test(expected = RegBaseCheckedException.class)
	public void testCloseTPMInstanceException() throws Exception {
		Tpm tpm = PowerMockito.mock(Tpm.class);

		ReflectionTestUtils.setField(TPMInitialization.class, "tpm", tpm);
		PowerMockito.doThrow(new IOException("Unable to close")).when(tpm, "close");

		TPMInitialization.closeTPMInstance();
	}

}
