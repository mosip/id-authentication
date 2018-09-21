package org.mosip.auth.core.dto.indauth;

import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PinType {

	OTP("OTP");

	private String type;

	private PinType(String type) {
		this.type = type;
	}

	@JsonValue
	@XmlValue
	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return getType();
	}

	/**
	 * 
	 * @param type
	 * @return IDType
	 */
	public static Optional<PinType> getPINType(String type) {
		return Stream.of(values()).filter(t -> t.getType().equals(type)).findAny();

	}
}
