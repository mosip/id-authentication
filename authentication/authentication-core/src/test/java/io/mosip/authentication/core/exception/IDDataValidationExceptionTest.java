package io.mosip.authentication.core.exception;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

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
	@Test
	public void testIDDataValidationExceptionDefaultCons() throws IDDataValidationException {
		assertThrows(IDDataValidationException.class, () -> {
		throw new IDDataValidationException();
			});
	}

	/**
	 * Test ID data validation exception.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test
	public void testIDDataValidationException() throws IDDataValidationException {
		assertThrows(IDDataValidationException.class, () -> {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
			});
	}

	/**
	 * Test ID data validation exception with args.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test
	public void testIDDataValidationExceptionWithArgs() throws IDDataValidationException {
		assertThrows(IDDataValidationException.class, () -> {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, "OTP");
			});
	}

	/**
	 * Test ID data validation exception with cause.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test
	public void testIDDataValidationExceptionWithCause() throws IDDataValidationException {
		assertThrows(IDDataValidationException.class, () -> {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED,
				new IDDataValidationException());
			});
	}

	/**
	 * Test ID data validation exception with args and cause.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test
	public void testIDDataValidationExceptionWithArgsAndCause() throws IDDataValidationException {
		assertThrows(IDDataValidationException.class, () -> {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED,
				new IDDataValidationException(), "OTP");
			});
	}

	/**
	 * Test ID data validation exception throwable.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test
	public void testIDDataValidationExceptionThrowable() throws IDDataValidationException {
		assertThrows(IDDataValidationException.class, () -> {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage());
			});
	}

	/**
	 * Test ID data validation exception with arg.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test
	public void testIDDataValidationExceptionWithArg() throws IDDataValidationException {
		assertThrows(IDDataValidationException.class, () -> {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage(), "a");
			});
	}

	/**
	 * Test ID data validation exception with throwable.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test
	public void testIDDataValidationExceptionWithThrowable() throws IDDataValidationException {
		assertThrows(IDDataValidationException.class, () -> {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage(),
				new IDDataValidationException());
			});
	}

	/**
	 * Test ID data validation exception with arg and throwable.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test
	public void testIDDataValidationExceptionWithArgAndThrowable() throws IDDataValidationException {
		assertThrows(IDDataValidationException.class, () -> {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage(), new IDDataValidationException(),
				"a");
			});
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
