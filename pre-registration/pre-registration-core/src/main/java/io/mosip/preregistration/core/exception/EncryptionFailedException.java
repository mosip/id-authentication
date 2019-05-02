package io.mosip.preregistration.core.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import lombok.Getter;

/**
 * @author Jagadishwari S
 * @since 1.0.0
 *
 */
@Getter
public class EncryptionFailedException extends BaseUncheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5135952690225019228L;
	 private MainResponseDTO<?> mainresponseDTO;

	public EncryptionFailedException(String msg) {
		super("", msg);
	}

	public EncryptionFailedException(String msg, Throwable cause) {
		super("", msg, cause);
	}

	public EncryptionFailedException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage, null);
	}
	public EncryptionFailedException(String errorCode, String errorMessage,MainResponseDTO<?> response) {
		super(errorCode, errorMessage, null);
		this.mainresponseDTO=response;
	}

	public EncryptionFailedException(String errorCode, String errorMessage, Throwable rootCause) {
		super(errorCode, errorMessage, rootCause);
	}

	public EncryptionFailedException() {
		super();
	}
}
