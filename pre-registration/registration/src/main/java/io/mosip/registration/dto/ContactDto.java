package io.mosip.registration.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Contact DTO
 * 
 * @author M1037717
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class ContactDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	/** The mobile number. */
	private String mobile;

	/** The email. */
	private String email;

	@Override
	public String toString() {
		return "ContactDto [mobile=" + mobile + ", email=" + email + "]";
	}

}
