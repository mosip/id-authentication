package io.mosip.authentication.service.impl.idauth.demo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "indv_demographic", schema = "ida")
public class DemoEntity {

	//FIX ME annotate columns for all fields.
	private String uinRefId;

	private String firstName;

	private String forename;

	private String givenname;

	private String middlename;

	private String middleinitial;

	private String lastname;

	private String surname;

	private String familyname;

	private String fullname;

	private String genderCode;

	private String parentFullName;

	private String parentRefIdType;

	private String parentRefId;

	private Date dob;

	private int age;

	private String addressLine1;

	private String addressLine2;

	private String addressLine3;

	private String locationCode;

	private String mobile;

	private String email;

	private String applicantType;

	private String nationalId;

	private String statusCode;

	private String langCode;

	private boolean isActive;

	private String correctedBy;

	private Date correctedOn;

	private String updatedBy;

	private Date updatedOn;
	
}
