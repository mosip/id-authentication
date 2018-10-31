package io.mosip.preregistration.application.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Registration DTO
 * 
 * @author M1037717
 *
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
public class RegistrationDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 6705845720255847210L;

	/** The preRegistration-Id. */
	private String preRegistrationId;

	/** The user-Id. */
	private String userId;

	/** The group-Id. */
	private String groupId;

	/** The isPrimary. */
	private Boolean isPrimary;

	/** The name dto. */
	private NameDto name;

	/** The genderCode. */
	private String genderCode;

	/** The parentFullName. */
	private String parentFullName;

	/** The parentRefIdType. */
	private String parentRefIdType;

	/** The parentRefId. */
	private String parentRefId;

	/** The dob. */
	private Date dob;

	/** The age. */
	private int age;

	/** The address dto. */
	private AddressDto address;

	/** The contact dto. */
	private ContactDto contact;

	/** The applicantType. */
	private String applicantType;

	/** The nationalid. */
	private String nationalid;

	/** The statusCode. */
	private String statusCode;

	/** The language code. */
	private String langCode;

	/** The is active. */
	private Boolean isActive;
	
	/** The app user id. */
	private String cr_appuser_id;
}
