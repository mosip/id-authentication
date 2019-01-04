package io.mosip.authentication.core.dto.indauth;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * 
 * @author Dinesh Karuppiah.T
 */

public enum InternalAuthType {

	DEMO("demo"), OTP("otp"), BIO("bio");

	/** The type. */
	String type;

	/**
	 * Instantiates a new internal auth type.
	 *
	 * @param type the type
	 */
	private InternalAuthType(String type) {
		this.type = type;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Get Internal Auth type
	 * 
	 * @param type
	 * @return
	 */
	public static Optional<InternalAuthType> getInternalAuthType(String type) {
		return Stream.of(values()).filter(t -> t.getType().equals(type)).findAny();
	}

}
