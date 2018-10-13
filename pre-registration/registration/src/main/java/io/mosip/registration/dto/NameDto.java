package io.mosip.registration.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NameDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	private String firstname;

	private String forename;

	private String givenname;

	private String middlename;

	private String middleinitial;

	private String lastname;

	private String surname;

	private String familyname;

	private String fullname;

	@Override
	public String toString() {
		return "NameDto [firstname=" + firstname + ", forename=" + forename + ", givenname=" + givenname
				+ ", middlename=" + middlename + ", middleinitial=" + middleinitial + ", lastname=" + lastname
				+ ", surname=" + surname + ", familyname=" + familyname + ", fullname=" + fullname + "]";
	}
	
	

}
