package io.mosip.resident.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;

import io.mosip.resident.constant.IdType;
import io.mosip.resident.exception.ApisResourceAccessException;
@RunWith(MockitoJUnitRunner.class)
public class UINCardDownloadTest {
	@Mock
    private Environment env;

	@Mock
    private ResidentServiceRestClient residentServiceRestClient;

	@Mock
    private TokenGenerator tokenGenerator;

	@InjectMocks
	UINCardDownloadService uinCardDownloadService;
	byte[] arr= new byte[10];
	@Test
	public void testgetUINCard() throws ApisResourceAccessException {
		Mockito.when(residentServiceRestClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(arr);
		assertEquals(arr,uinCardDownloadService.getUINCard("123456789", "UIN", IdType.UIN));
	}
	@Test(expected=ApisResourceAccessException.class)
	public void testgetUINCardFailure() throws ApisResourceAccessException {
		Mockito.when(residentServiceRestClient.postApi(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(new ApisResourceAccessException());
		uinCardDownloadService.getUINCard("123456789", "UIN", IdType.UIN);
	}

}
