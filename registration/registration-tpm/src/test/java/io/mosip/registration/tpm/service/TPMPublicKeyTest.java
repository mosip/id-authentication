package io.mosip.registration.tpm.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.tpm.initialize.TPMInitialization;

import tss.Tpm;
import tss.tpm.CreatePrimaryResponse;
import tss.tpm.TPMS_SENSITIVE_CREATE;
import tss.tpm.TPMT_PUBLIC;
import tss.tpm.TPM_HANDLE;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ TPMInitialization.class })
public class TPMPublicKeyTest {

	@Test
	public void testGetPublicKey() throws Exception {
		Tpm tpm = PowerMockito.mock(Tpm.class);
		CreatePrimaryResponse createPrimaryResponse = PowerMockito.mock(CreatePrimaryResponse.class);
		TPMT_PUBLIC tpmtPublic = PowerMockito.mock(TPMT_PUBLIC.class);
		byte[] publicKeyTPMBytes = "PublickKeyTPMBytes".getBytes();
		createPrimaryResponse.outPublic = tpmtPublic;
		PowerMockito.mockStatic(TPMInitialization.class);

		PowerMockito.doReturn(tpm).when(TPMInitialization.class, "getTPMInstance");
		PowerMockito.doReturn(createPrimaryResponse).when(tpm, "CreatePrimary", Mockito.any(TPM_HANDLE.class),
				Mockito.any(TPMS_SENSITIVE_CREATE.class), Mockito.any(TPMT_PUBLIC.class),
				Mockito.anyString().getBytes(), Mockito.any());
		PowerMockito.doReturn(publicKeyTPMBytes).when(tpmtPublic, "toTpm");

		//Assert.assertSame(publicKeyTPMBytes, TPMPublicKey.getPublicKey());
	}

	@Test(expected = RuntimeException.class)
	public void testGetPublicKeyException() throws Exception {
		Tpm tpm = PowerMockito.mock(Tpm.class);
		CreatePrimaryResponse createPrimaryResponse = PowerMockito.mock(CreatePrimaryResponse.class);
		TPMT_PUBLIC tpmtPublic = PowerMockito.mock(TPMT_PUBLIC.class);
		createPrimaryResponse.outPublic = tpmtPublic;
		PowerMockito.mockStatic(TPMInitialization.class);

		PowerMockito.doReturn(tpm).when(TPMInitialization.class, "getTPMInstance");
		PowerMockito.doReturn(createPrimaryResponse).when(tpm, "CreatePrimary", Mockito.any(TPM_HANDLE.class),
				Mockito.any(TPMS_SENSITIVE_CREATE.class), Mockito.any(TPMT_PUBLIC.class),
				Mockito.anyString().getBytes(), Mockito.any());
		PowerMockito.doThrow(new RuntimeException()).when(tpmtPublic, "toTpm");

		//TPMPublicKey.getPublicKey();
	}

}
