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

	/** The firstname. */
	private String firstname;

	/** The forename. */
	private String forename;

	/** The givenname. */
	private String givenname;

	/** The middlename. */
	private String middlename;

	/** The middleinitial. */
	private String middleinitial;

	/** The lastname. */
	private String lastname;

	/** The surname. */
	private String surname;

	/** The familyname. */
	private String familyname;

	/** The fullname. */
	private String fullname;

	@Override
	public String toString() {
		return "NameDto [firstname=" + firstname + ", forename=" + forename + ", givenname=" + givenname
				+ ", middlename=" + middlename + ", middleinitial=" + middleinitial + ", lastname=" + lastname
				+ ", surname=" + surname + ", familyname=" + familyname + ", fullname=" + fullname + "]";
	}
	
	

}
