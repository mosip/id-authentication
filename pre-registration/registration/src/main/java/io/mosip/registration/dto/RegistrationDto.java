package io.mosip.registration.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * The Class RegistrationStatusDto.
 */

@Getter
@Setter@NoArgsConstructor
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

	/** The created by. */
	private String createdBy;

	/** The create date time. */
	private Timestamp createDateTime;

	/** The updated by. */
	private String updatedBy;

	/** The update date time. */
	private Timestamp updateDateTime;

	/** The is deleted. */
	private Boolean isDeleted;

	/** The deleted date time. */
	private LocalDateTime deletedDateTime;

	
}
