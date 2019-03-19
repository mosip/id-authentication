package io.mosip.authentication.core.exception;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.kernel.core.exception.BaseCheckedException;

public class IDDataValidationExceptionTest {

	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationExceptionDefaultCons() throws IDDataValidationException {
		throw new IDDataValidationException();
	}

	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationException() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
	}

	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationExceptionWithArgs() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED, "OTP");
	}

	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationExceptionWithCause() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED,
				new IDDataValidationException());
	}

	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationExceptionWithArgsAndCause() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED,
				new IDDataValidationException(), "OTP");
	}

	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationExceptionThrowable() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage());
	}

	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationExceptionWithArg() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage(), "a");
	}

	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationExceptionWithThrowable() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage(),
				new IDDataValidationException());
	}

	@Test(expected = IDDataValidationException.class)
	public void testIDDataValidationExceptionWithArgAndThrowable() throws IDDataValidationException {
		throw new IDDataValidationException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage(), new IDDataValidationException(),
				"a");
	}

	@Test
	public void testIDDataValidationExceptionAddIfo() throws IDDataValidationException {
		IDDataValidationException ex = new IDDataValidationException();
		ex.addInfo(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(), null, "a");
		assertEquals(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
				((BaseCheckedException) ex).getErrorCode());
		assertEquals("", ((BaseCheckedException) ex).getErrorText());

	}

	@Test
	public void testGetArgs() {
		IDDataValidationException ex = new IDDataValidationException();
		List<Object[]> args = ex.getArgs();// .get(0);
		// assertEquals("OTP", objects[0]);
	}

}
