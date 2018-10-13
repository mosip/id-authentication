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

	
	private String preRegistrationId;
	
	private String userId;
	
	private String groupId;
	
	private Boolean isPrimary;
	
	private NameDto name;
	
	private String genderCode;
	
	private String parentFullName;

	private String parentRefIdType;
	
	private String parentRefId;
	
	private Date dob;

	private int age;

	private AddressDto address;

	private ContactDto contact;

	private String applicantType;

	private String nationalid;

	private String statusCode;
	
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
