package io.mosip.authentication.core.indauth.dto;

import java.util.Optional;
import java.util.stream.Stream;

/*
 * Auth type for Internal Authentication
 * 
 * @author Dinesh Karuppiah.T
 */

public enum InternalAuthType {

	/** The demo. */
	DEMO("demo"),
	/** The otp. */
	OTP("otp"),
	/** The bio. */
	BIO("bio"),
	/** The spin. */
	SPIN("pin");

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
	 * Get Internal Auth type.
	 *
	 * @param type the type
	 * @return the internal auth type
	 */
	public static Optional<InternalAuthType> getInternalAuthType(String type) {
		return Stream.of(values()).filter(t -> t.getType().equals(type)).findAny();
	}

}
