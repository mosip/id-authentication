package io.mosip.kernel.core.saltgenerator.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Enum SaltGeneratorErrorConstants - contains error constants
 * for kernel salt generator.
 *
 * @author Manoj SP
 */
public enum SaltGeneratorErrorConstants {
	
	/** The record exists. */
	RECORD_EXISTS("KER-SGR-001", "Record(s) already exists in DB");

	/** The error code. */
	private final String errorCode;

	/** The error message. */
	private final String errorMessage;

	/**
	 * Constructor for {@link SaltGeneratorErrorConstants}.
	 *
	 * @param errorCode    - id-usage error codes which follows
	 *                     "<product>-<component>-<number>" pattern
	 * @param errorMessage - short error message
	 */
	private SaltGeneratorErrorConstants(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	/**
	 * Getter for errorCode.
	 *
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Getter for errorMessage.
	 *
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	
	/**
	 * Gets the all error codes.
	 *
	 * @return the all error codes
	 */
	public static List<String> getAllErrorCodes() {
		return Collections.unmodifiableList(Arrays.asList(SaltGeneratorErrorConstants.values()).parallelStream()
				.map(SaltGeneratorErrorConstants::getErrorCode).collect(Collectors.toList()));
	}
}
