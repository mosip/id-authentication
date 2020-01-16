package io.mosip.authentication.core.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.dto.DataValidationUtil;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;

/**
 * The Class DataValidationUtilTest.
 *
 * @author Manoj SP
 */
@RunWith(MockitoJUnitRunner.class)
public class DataValidationUtilTest {
	
	/** The request. */
	AuthRequestDTO request;
	
	/**
	 * Before.
	 */
	@Before
	public void before() {
		request = new AuthRequestDTO();
	}
	
	/**
	 * Test data validation util.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 */
	@Test
	public void testDataValidationUtil() throws IDDataValidationException {
		Errors errors = new BindException(DataValidationUtil.class, "DataValidationUtil");
		DataValidationUtil.validate(errors);
	}
	
	/**
	 * Test data validation util exception.
	 *
	 * @throws IDDataValidationException the ID data validation exception
	 * @throws NoSuchFieldException the no such field exception
	 * @throws SecurityException the security exception
	 */
	@Test(expected=IDDataValidationException.class)
	public void testDataValidationUtilException() throws IDDataValidationException, NoSuchFieldException, SecurityException {
		request.setId("uniqueID");
		Errors errors = new BindException(request, "AuthRequestDTO");
		errors.rejectValue("id", "errorCode", "defaultMessage");
		DataValidationUtil.validate(errors);
	}

}
