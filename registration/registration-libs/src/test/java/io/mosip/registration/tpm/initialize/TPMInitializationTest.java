package io.mosip.registration.tpm.initialize;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.test.util.ReflectionTestUtils;

import tss.Tpm;
import tss.TpmFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TpmFactory.class })
public class TPMInitializationTest {

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

	@Test(expected = IOException.class)
	public void testCloseTPMInstanceException() throws Exception {
		Tpm tpm = PowerMockito.mock(Tpm.class);

		ReflectionTestUtils.setField(TPMInitialization.class, "tpm", tpm);
		PowerMockito.doThrow(new IOException("Unable to close")).when(tpm, "close");

		TPMInitialization.closeTPMInstance();
	}

}
