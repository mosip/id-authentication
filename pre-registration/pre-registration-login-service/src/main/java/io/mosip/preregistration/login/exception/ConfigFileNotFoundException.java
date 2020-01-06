package io.mosip.preregistration.login.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigFileNotFoundException  extends BaseUncheckedException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1298682891599963309L;
	
	private MainResponseDTO<?> mainResposneDto;
	
	public ConfigFileNotFoundException(String msg,MainResponseDTO<?> response) {
		super("", msg);
		this.mainResposneDto=response;
	}

	public ConfigFileNotFoundException(String msg, Throwable cause,MainResponseDTO<?> response) {
		super("", msg, cause);
		this.mainResposneDto=response;
	}

	public ConfigFileNotFoundException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainResposneDto=response;
	}

	public ConfigFileNotFoundException(String errorCode, String errorMessage, Throwable rootCause,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, rootCause);
		this.mainResposneDto=response;
	}

	public ConfigFileNotFoundException() {
		super();
	}
}