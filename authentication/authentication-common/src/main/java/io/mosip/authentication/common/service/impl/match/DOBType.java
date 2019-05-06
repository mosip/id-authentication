package io.mosip.authentication.common.service.impl.match;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * The Enum DOBType.
 * 
 * @author Manoj SP
 *
 */
public enum DOBType {
	
	/** The verified. */
	VERIFIED("V"),
	/** The declared. */
	DECLARED("D"),
	/** The approximate. */
	APPROXIMATE("A");
	
	/** The type. */
	private String type;

	/**
	 * Instantiates a new DOB type.
	 *
	 * @param dobType the dob type
	 */
	private DOBType(String dobType) {
		this.type = dobType;
	}

	/**
	 * Gets the dob type.
	 *
	 * @return the dob type
	 */
	public String getDobType() {
		return type;
	}
	
	/**
	 * Checks if is type present.
	 *
	 * @param dobType the dob type
	 * @return the boolean
	 */
	public static Boolean isTypePresent(String dobType) {
		return Stream.of(values()).anyMatch(type -> type.getDobType().equalsIgnoreCase(dobType));
	}
	
	/**
	 * Gets the type.
	 *
	 * @param dobType the dob type
	 * @return the type
	 */
	public static Optional<DOBType> getType(String dobType) {
		return Stream.of(values()).filter(type -> type.getDobType().equalsIgnoreCase(dobType)).findAny();
	}

}
