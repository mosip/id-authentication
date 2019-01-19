package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Manoj SP
 *
 */
public enum DOBType {
	
	VERIFIED("V"),
	DECLARED("D"),
	APPROXIMATE("A");
	
	private String type;

	private DOBType(String dobType) {
		this.type = dobType;
	}

	public String getDobType() {
		return type;
	}
	
	public static Boolean isTypePresent(String dobType) {
		return Stream.of(values()).anyMatch(type -> type.getDobType().equalsIgnoreCase(dobType));
	}
	
	public static Optional<DOBType> getType(String dobType) {
		return Stream.of(values()).filter(type -> type.getDobType().equalsIgnoreCase(dobType)).findAny();
	}

}
