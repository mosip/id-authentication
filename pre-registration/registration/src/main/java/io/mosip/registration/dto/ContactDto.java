package io.mosip.registration.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContactDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;
	
	private String mobile;

	private String email;

	@Override
	public String toString() {
		return "ContactDto [mobile=" + mobile + ", email=" + email + "]";
	}
	
	

}
