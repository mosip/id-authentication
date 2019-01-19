package io.mosip.authentication.core.exception;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;

public class RestServiceExceptionTest {

    @Test(expected = RestServiceException.class)
    public void testRestServiceExceptionDefaultCons() throws RestServiceException {
	throw new RestServiceException();
    }

    @Test(expected = RestServiceException.class)
    public void testRestServiceException() throws RestServiceException {
	throw new RestServiceException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT);
    }

    @Test(expected = RestServiceException.class)
    public void testRestServiceExceptionThrowable() throws RestServiceException {
	throw new RestServiceException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT, new RestServiceException());
    }

    @Test(expected = RestServiceException.class)
    public void testRestServiceExceptionObject() throws RestServiceException {
	AuthRequestDTO authRequestDTO = new AuthRequestDTO();
	throw new RestServiceException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT, authRequestDTO.toString(),
		Optional.of(authRequestDTO));
    }

    @Test
    public void testGetResponseBody() {
	AuthRequestDTO authRequestDTO = new AuthRequestDTO();
	RestServiceException ex = new RestServiceException(IdAuthenticationErrorConstants.OTP_NOT_PRESENT,
		authRequestDTO.toString(), authRequestDTO);
	assertEquals(authRequestDTO, (AuthRequestDTO) ex.getResponseBody().get());
	assertEquals(authRequestDTO.toString(), ex.getResponseBodyAsString().get());
    }

}
