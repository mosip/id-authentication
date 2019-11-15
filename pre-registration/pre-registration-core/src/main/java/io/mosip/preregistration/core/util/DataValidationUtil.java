package io.mosip.preregistration.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.validation.Errors;

import io.mosip.preregistration.core.common.dto.ExceptionJSONInfoDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;

public final class DataValidationUtil {

	/**
	 * Instantiates a new data validation util.
	 */
	private DataValidationUtil() {
	}
	

	/**
	 * Get list of errors from error object and build and throw {@code InvalidRequestParameterException}.
	 *
	 * @param errors the errors
	 * @throws InvalidRequestParameterException the InvalidRequestParameterException
	 */
	public static void validate(Errors errors, String operation) throws InvalidRequestParameterException {
		MainResponseDTO<?> response= new MainResponseDTO<>();
		List<ExceptionJSONInfoDTO> errorList = new ArrayList<>();
		
		if (errors.hasErrors()) {
			errors.getAllErrors().stream()
					.forEach(error ->{
						ExceptionJSONInfoDTO ex= new ExceptionJSONInfoDTO();
						ex.setErrorCode(error.getCode().toString());
						ex.setMessage(error.getDefaultMessage());
						errorList.add(ex);
					} );
			throw new InvalidRequestParameterException(errorList, response);
		}
	}

}
