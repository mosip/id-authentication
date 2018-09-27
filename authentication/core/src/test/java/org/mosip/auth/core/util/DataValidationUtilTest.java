package org.mosip.auth.core.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mosip.auth.core.dto.indauth.AuthRequestDTO;
import org.mosip.auth.core.exception.IDDataValidationException;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

@RunWith(MockitoJUnitRunner.class)
public class DataValidationUtilTest {
	
	AuthRequestDTO request;
	
	@Before
	public void before() {
		request = new AuthRequestDTO();
	}
	
	@Test
	public void testDataValidationUtil() throws IDDataValidationException {
		Errors errors = new BindException(DataValidationUtil.class, "DataValidationUtil");
		DataValidationUtil.validate(errors);
	}
	
	@Test(expected=IDDataValidationException.class)
	public void testDataValidationUtilException() throws IDDataValidationException, NoSuchFieldException, SecurityException {
		request.setId("uniqueID");
		Errors errors = new BindException(request, "AuthRequestDTO");
		errors.rejectValue("id", "errorCode", "defaultMessage");
		DataValidationUtil.validate(errors);
	}

}
