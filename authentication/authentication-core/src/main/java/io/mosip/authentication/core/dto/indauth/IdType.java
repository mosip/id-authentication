package io.mosip.authentication.core.dto.indauth;

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
public enum IdType {

	UIN("D"), VID("V");

	/**
	 * Value that indicates that default id.
	 */
	public static final IdType DEFAULT_ID_TYPE = IdType.UIN;

	private String type;

	/**
	 * construct enum with id-type.
	 * 
	 * @param type id type
	 */
	private IdType(String type) {
		this.type = type;
	}

	/**
	 * get id-type.
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
	 * @param type String id-type
	 * @return IDType Optional with IdType
	 */
	public static Optional<IdType> getIDType(String type) {
		return Stream.of(values()).filter(t -> t.getType().equalsIgnoreCase(type)).findAny();

	}
}
