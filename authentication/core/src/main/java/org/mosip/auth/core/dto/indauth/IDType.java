package org.mosip.auth.core.dto.indauth;

import java.util.Optional;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * General-purpose annotation used for configuring details of user
 * identification.
 * 
 * @author Rakesh Roshan
 */
public enum IDType {

	UIN("D"), VID("V");
	


	/**
	 * Value that indicates that default id.
	 */
	public static final IDType DEFAULT_ID_TYPE = IDType.UIN;

	private String type;

	/**
	 * 
	 * @param type
	 */
	private IDType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @return type
	 */
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
	 * Look for id type either "D" or "V". default id is "D"
	 * 
	 * @param type
	 * @return IDType
	 */
	public static Optional<IDType> getIDType(String type) {
		return Stream.of(values()).filter(t -> t.getType().equals(type))
				                                .findAny();

	}
}
