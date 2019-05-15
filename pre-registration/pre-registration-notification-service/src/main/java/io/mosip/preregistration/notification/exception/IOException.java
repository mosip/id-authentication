package io.mosip.preregistration.notification.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

@Getter
public class IOException extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6723728155340185347L;
	private final MainResponseDTO<?> mainResponseDTO;

	

	public IOException(String arg0, String arg1, Throwable arg2,MainResponseDTO<?> response) {
		super(arg0, arg1, arg2);
		this.mainResponseDTO=response;
	}

	public IOException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage);
		this.mainResponseDTO=response;
	}

	public IOException(String errorMessage,MainResponseDTO<?> response) {
		super(errorMessage);
		this.mainResponseDTO=response;
	}

}
