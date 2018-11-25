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
 * @author Manoj SP
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DataValidationUtilTest {

	IdRequestDTO request;

	@Before
	public void before() {
		request = new IdRequestDTO();
	}

	@Test
	public void testDataValidationUtil() throws IdRepoDataValidationException {
		Errors errors = new BindException(DataValidationUtil.class, "DataValidationUtil");
		DataValidationUtil.validate(errors);
	}

	@Test(expected = IdRepoDataValidationException.class)
	public void testDataValidationUtilException()
			throws IdRepoDataValidationException, NoSuchFieldException, SecurityException {
		request.setId("uniqueID");
		Errors errors = new BindException(request, "AuthRequestDTO");
		errors.rejectValue("id", "errorCode", "defaultMessage");
		DataValidationUtil.validate(errors);
	}

}
