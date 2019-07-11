package io.mosip.idrepository.core.test.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.exception.RestServiceException;

/**
 * 
 * @author Prem Kumar
 *
 */
public class RestServiceExceptionTest {
	/**
	 * Test id repo data validation exception.
	 *
	 * @throws IdRepoAppException the id repo app exception
	 */
	@Test(expected = RestServiceException.class)
	public void testRestServiceException() throws IdRepoAppException {
		throw new RestServiceException();
	}

	@Test(expected = RestServiceException.class)
	public void testRestServiceExceptionWithException() throws IdRepoAppException {
		throw new RestServiceException(IdRepoErrorConstants.INVALID_REQUEST);
	}

	@Test(expected = RestServiceException.class)
	public void testRestServiceExceptionWithExceptionAndConstant() throws IdRepoAppException {
		throw new RestServiceException(IdRepoErrorConstants.INVALID_REQUEST, new RestServiceException());
	}

	@Test(expected = RestServiceException.class)
	public void testRestServiceExceptionStringAndConstant() throws IdRepoAppException {
		throw new RestServiceException(IdRepoErrorConstants.INVALID_REQUEST, "", Object.class);
	}

	@Test
	public void testRestServiceExceptionStringAndConstantAndBody() throws IdRepoAppException {
		ObjectNode object = new ObjectMapper().createObjectNode().put("a", "b");
		RestServiceException exception = new RestServiceException(IdRepoErrorConstants.INVALID_REQUEST, object.toString(), object);
		assertEquals(object.toString(), exception.getResponseBodyAsString().get());
		assertEquals(exception.getResponseBody().get(), object);
	}

}
