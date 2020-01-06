package io.mosip.authentication.core.indauth.dto;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The enum for EkycAuthType
 * 
 * @author Prem Kumar
 *
 *
 *
 */

public enum EkycAuthType {

	/** For demo */
	DEMO("demo", AuthTypeDTO::isDemo),

	/** For bio */
	BIO("bio", AuthTypeDTO::isBio),

	/** For Pin */
	PIN("pin", AuthTypeDTO::isPin),

	/** For OTP */
	OTP("otp", AuthTypeDTO::isOtp);

	/** The EkycAuthType type */
	private String type;

	/** The authTypePredicate */
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
	 * 
	 * @return the predicate
	 */
	public Predicate<AuthTypeDTO> getAuthTypePredicate() {
		return authTypePredicate;
	}

}
