package io.mosip.authentication.core.util;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.util.DataValidationUtil;

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
