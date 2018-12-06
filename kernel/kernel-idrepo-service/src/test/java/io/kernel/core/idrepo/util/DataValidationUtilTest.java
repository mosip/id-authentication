package io.kernel.core.idrepo.util;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import io.kernel.core.idrepo.dto.IdRequestDTO;
import io.kernel.core.idrepo.exception.IdRepoDataValidationException;

/**
 * The Class DataValidationUtilTest.
 *
 * @author Manoj SP
 */
@RunWith(MockitoJUnitRunner.class)
public class DataValidationUtilTest {

	/** The request. */
	IdRequestDTO request;

	/**
	 * Before.
	 */
	@Before
	public void before() {
		request = new IdRequestDTO();
	}

	/**
	 * Test data validation util.
	 *
	 * @throws IdRepoDataValidationException the id repo data validation exception
	 */
	@Test
	public void testDataValidationUtil() throws IdRepoDataValidationException {
		Errors errors = new BindException(DataValidationUtil.class, "DataValidationUtil");
		DataValidationUtil.validate(errors);
	}

	/**
	 * Test data validation util exception.
	 *
	 * @throws IdRepoDataValidationException the id repo data validation exception
	 * @throws NoSuchFieldException the no such field exception
	 * @throws SecurityException the security exception
	 */
	@Test(expected = IdRepoDataValidationException.class)
	public void testDataValidationUtilException()
			throws IdRepoDataValidationException, NoSuchFieldException, SecurityException {
		request.setId("uniqueID");
		Errors errors = new BindException(request, "IdRequestDTO");
		errors.rejectValue("id", "errorCode", "defaultMessage");
		DataValidationUtil.validate(errors);
	}

}
