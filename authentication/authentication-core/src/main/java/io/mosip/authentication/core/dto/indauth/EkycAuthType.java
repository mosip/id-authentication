package io.mosip.authentication.core.dto.indauth;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * 
 * @author Prem Kumar
 *
 */

public enum EkycAuthType {
		
	/** For Fingerprints  */
	FINGERPRINT("F", AuthTypeDTO::isBio),
	
	/** For Iris */
	IRIS("I", AuthTypeDTO::isBio),
	
	/** For Face */
	FACE("A", AuthTypeDTO::isBio),
	
	/** For Pin */
	PIN("P", AuthTypeDTO::isPin),
	/** For OTP */
	OTP("O", AuthTypeDTO::isOtp);
	
	
	String type;
	private Predicate<AuthTypeDTO> authTypePredicate;
	
	/**
	 * Instantiates a new EkycAuthType.
	 *
	 * @param type the type
	 * @param type the authTypePredicate
	 */
	private EkycAuthType(String type, Predicate<AuthTypeDTO> authTypePredicate) {
		this.type = type;
		this.authTypePredicate = authTypePredicate;
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
	 * This method returns the EkycAuthType based on the type.
	 *
	 * @param type the type
	 * @return EkycAuthType
	 */
	public static Optional<EkycAuthType> getEkycAuthType(String type) {
		return Stream.of(values()).filter(t -> t.getType().equals(type)).findAny();

	}
	
	/**
	 * Get the predicate to check the auth type
	 * @return the predicate
	 */
	public Predicate<AuthTypeDTO> getAuthTypePredicate() {
		return authTypePredicate;
	}
	
}
