package io.mosip.authentication.core.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.kernel.core.exception.BaseCheckedException;

/**
 * The Class IDDataValidationExceptionTest.
 *
 * @author Manoj SP
 */
public class IDDataValidationExceptionTest {

	/**
	 * Test ID data validation exception default cons.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationExceptionDefaultCons() throws IDDataValidationException {
		throw new IDDataValidationException();
	}

	/**
	 * Test ID data validation exception.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationException() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
	}

	/**
	 * Test ID data validation exception with args.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationExceptionWithArgs() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, "OTP");
	}

	/**
	 * Test ID data validation exception with cause.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationExceptionWithCause() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED,
				new IDDataValidationException());
	}

	/**
	 * Test ID data validation exception with args and cause.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationExceptionWithArgsAndCause() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED,
				new IDDataValidationException(), "OTP");
	}

	/**
	 * Test ID data validation exception throwable.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationExceptionThrowable() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage());
	}

	/**
	 * Test ID data validation exception with arg.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationExceptionWithArg() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage(), "a");
	}

	/**
	 * Test ID data validation exception with throwable.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationExceptionWithThrowable() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage(),
				new IDDataValidationException());
	}

	/**
	 * Test ID data validation exception with arg and throwable.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationExceptionWithArgAndThrowable() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage(), new IDDataValidationException(),
				"a");
	}

	/**
	 * Test ID data validation exception add ifo.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test
	public void testIDDataValidationExceptionAddIfo() throws IDDataValidationException {
		IDDataValidationException ex = new IDDataValidationException();
		ex.addInfo(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(), null, "a");
		assertEquals(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				((BaseCheckedException) ex).getErrorCode());
		assertEquals("", ((BaseCheckedException) ex).getErrorText());

	}

	/**
	 * Test get args.
	 */
	@Test
	public void testGetArgs() {
		IDDataValidationException ex = new IDDataValidationException();
		ex.getArgs();
	}

}
