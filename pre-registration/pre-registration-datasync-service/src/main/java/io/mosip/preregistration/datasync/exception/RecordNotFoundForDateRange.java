package io.mosip.preregistration.datasync.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * RecordNotFoundForDateRange Exception
 * 
 * @author M1043226
 *
 */
@Getter
@Setter
public class RecordNotFoundForDateRange extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;

	private MainResponseDTO<?> mainResponseDto;

	/**
	 * @param errorCode
	 *            pass the error code
	 * @param errorMessage
	 *            pass the error message
	 * @param response
	 *            pass the cause
	 */
	public RecordNotFoundForDateRange(String errorCode, String errorMessage, MainResponseDTO<?> response) {
		super(errorCode, errorMessage);
		this.mainResponseDto = response;
	}

}