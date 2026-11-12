package io.mosip.authentication.core.util;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;

/**
 * The Class DataValidationUtilTest.
 *
 * @author Manoj SP
 */
@ExtendWith(MockitoExtension.class)
public class DataValidationUtilTest {
	
	/** The request. */
	AuthRequestDTO request;
	
	/**
	 * Before.
	 */
	@BeforeEach
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
	@Test
	public void testDataValidationUtilException() throws IDDataValidationException, NoSuchFieldException, SecurityException {
		assertThrows(IDDataValidationException.class, () -> {
		request.setId("uniqueID");
		Errors errors = new BindException(request, "AuthRequestDTO");
		errors.rejectValue("id", "errorCode", "defaultMessage");
		DataValidationUtil.validate(errors);
			});
	}

}
