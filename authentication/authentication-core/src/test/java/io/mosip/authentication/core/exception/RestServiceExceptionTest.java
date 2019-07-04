package io.mosip.authentication.core.exception;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;

/**
 * The Class RestServiceExceptionTest.
 *
 * @author Manoj SP
 */
public class RestServiceExceptionTest {

    /**
     * Test rest service exception default cons.
     *
     * @throws RestServiceException the rest service exception
     */
    @Test(expected = RestServiceException.class)
    public void testRestServiceExceptionDefaultCons() throws RestServiceException {
	throw new RestServiceException();
    }

    /**
     * Test rest service exception.
     *
     * @throws RestServiceException the rest service exception
     */
    @Test(expected = RestServiceException.class)
    public void testRestServiceException() throws RestServiceException {
	throw new RestServiceException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
    }

    /**
     * Test rest service exception throwable.
     *
     * @throws RestServiceException the rest service exception
     */
    @Test(expected = RestServiceException.class)
    public void testRestServiceExceptionThrowable() throws RestServiceException {
	throw new RestServiceException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, new RestServiceException());
    }

    /**
     * Test rest service exception object.
     *
     * @throws RestServiceException the rest service exception
     */
    @Test(expected = RestServiceException.class)
    public void testRestServiceExceptionObject() throws RestServiceException {
	AuthRequestDTO authRequestDTO = new AuthRequestDTO();
	throw new RestServiceException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, authRequestDTO.toString(),
		Optional.of(authRequestDTO));
    }

    /**
     * Test get response body.
     */
    @Test
    public void testGetResponseBody() {
	AuthRequestDTO authRequestDTO = new AuthRequestDTO();
	RestServiceException ex = new RestServiceException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED,
		authRequestDTO.toString(), authRequestDTO);
	assertEquals(authRequestDTO, (AuthRequestDTO) ex.getResponseBody().get());
	assertEquals(authRequestDTO.toString(), ex.getResponseBodyAsString().get());
    }

}
