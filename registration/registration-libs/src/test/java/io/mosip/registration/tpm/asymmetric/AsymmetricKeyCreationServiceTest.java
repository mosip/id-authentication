package io.mosip.registration.tpm.asymmetric;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;

import io.mosip.kernel.core.exception.BaseUncheckedException;

import tss.Tpm;
import tss.tpm.CreatePrimaryResponse;
import tss.tpm.TPMS_SENSITIVE_CREATE;
import tss.tpm.TPMT_PUBLIC;
import tss.tpm.TPM_HANDLE;

public class AsymmetricKeyCreationServiceTest {

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private AsymmetricKeyCreationService asymmetricKeyCreationService;

	@Test
	public void keyCreationTest() throws Exception {
		Tpm mockedTPM = PowerMockito.mock(Tpm.class);
		CreatePrimaryResponse createPrimaryResponse = PowerMockito.mock(CreatePrimaryResponse.class);
		TPM_HANDLE tpmHandle = PowerMockito.mock(TPM_HANDLE.class);
		createPrimaryResponse.handle = tpmHandle;

		PowerMockito.doReturn(createPrimaryResponse).when(mockedTPM, "CreatePrimary", Mockito.any(TPM_HANDLE.class),
				Mockito.any(TPMS_SENSITIVE_CREATE.class), Mockito.any(TPMT_PUBLIC.class),
				Mockito.anyString().getBytes(), Mockito.any());

		Assert.assertEquals(tpmHandle, asymmetricKeyCreationService.createPersistentKey(mockedTPM));
	}

	@Test(expected = BaseUncheckedException.class)
	public void keyCreationRuntimeExceptionTest() throws Exception {
		Tpm mockedTPM = PowerMockito.mock(Tpm.class);

		PowerMockito.doThrow(new RuntimeException()).when(mockedTPM, "CreatePrimary", Mockito.any(TPM_HANDLE.class),
				Mockito.any(TPMS_SENSITIVE_CREATE.class), Mockito.any(TPMT_PUBLIC.class),
				Mockito.anyString().getBytes(), Mockito.any());

		asymmetricKeyCreationService.createPersistentKey(mockedTPM);
	}

}
