package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Optional;
import java.util.stream.Stream;

/**
* Enum contains Gender Types
*
* @author Sanjay Murali
*/

public enum GenderType {
	
	MALE("M"),
	FEMALE("F"),
	OTHERS("T");
	
	private String type;

	private GenderType(String genderType) {
		this.type = genderType;
	}

	public String getGenderType() {
		return type;
	}
	
	public static Boolean isTypePresent(String genderType) {
		return Stream.of(values()).anyMatch(type -> type.getGenderType().equalsIgnoreCase(genderType));
	}
	
	public static Optional<GenderType> getType(String genderType) {
		return Stream.of(values()).filter(type -> type.getGenderType().equalsIgnoreCase(genderType)).findAny();
	}



}
