package io.mosip.preregistration.login.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * This class provides custom Exception for calidation fail scenario
 * 
 * @author Akshay Jain
 * @since 1.0.0
 *
 */

@Getter
@Setter
public class UserIdOtpFaliedException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;
	
	private MainResponseDTO<?> mainResponseDto;
	
	public UserIdOtpFaliedException(String errorCode,String errorMessage,MainResponseDTO<?> response) {
		super(errorCode,errorMessage);
		this.mainResponseDto=response;
	}

}
