package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.exception.util.PreIssuanceExceptionCodes;
import lombok.Getter;
import lombok.Setter;

/**
 * TablenotAccessibleException occurs when system is not able to access registration table.
 *
 */
@Setter
@Getter
public class TableNotAccessibleException extends BaseUncheckedException {

	private static final long serialVersionUID = 1L;
	private MainResponseDTO<?> mainResposneDTO;

	public TableNotAccessibleException() {
		super();
	}

	public TableNotAccessibleException(String message) {
		super(PreIssuanceExceptionCodes.TABLE_NOT_FOUND_EXCEPTION, message);
	}

	public TableNotAccessibleException(String message, Throwable cause) {
		super(PreIssuanceExceptionCodes.TABLE_NOT_FOUND_EXCEPTION+ EMPTY_SPACE, message, cause);
	}
	
	public TableNotAccessibleException(String errorCode,String message) {
		super(errorCode, message);
	}
	public TableNotAccessibleException(String errorCode,String message, MainResponseDTO<?> response) {
		super(errorCode, message);
		this.mainResposneDTO=response;
	}
	
	public TableNotAccessibleException(String errorCode,String message, Throwable cause) {
		super(errorCode, message, cause);
	}
}