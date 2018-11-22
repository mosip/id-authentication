package io.mosip.authentication.core.dto.indauth;

import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * @author Prem Kumar
 *
 */
public enum KycType {
	/** For Full Kyc */
	NONE("none"),
	
	/** For Limited Kyc */
	LIMITED("limited"),
	
	/** For Full Kyc */
	FULL("full");
	
	private String type;
	
	private static final KycType DEFAULT_KYC_TYPE = FULL;
	
	private KycType(String type)
	{
		this.type=type;
	}
	
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	@JsonValue
	@XmlValue
	public String getType() {
		return type;
	}

	/**
	 * This method returns the KycType based on the type.
	 *
	 * @param type the type
	 * @return KycType
	 */
	public static KycType getEkycAuthType(String type) {
		return Stream.of(values()).filter(t -> t.getType().equalsIgnoreCase(type)).findAny().orElse(DEFAULT_KYC_TYPE);

	}
}
