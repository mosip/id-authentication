package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author M1046368 Arun Bose
 * Instantiates a new demo entity.
 */
@Data
@Entity
@Table(name = "indv_demographic", schema = "ida")
public class DemoEntity {

	//FIX ME annotate columns for all fields.

	/** The uin ref id. */
	@Id
	@Column(name="uin_ref_id", nullable=false)
	private String uinRefId;
	
	/** The firstname. */
	@Column(name="firstname")
	private String firstName;

	/** The forename. */
	@Column(name="forename")
	private String foreName;

	/** The given name. */
	@Column(name="givenname")
    private String givenName;

	/** The middle name. */
	@Column(name="middlename")
	private String middleName;
 
	/** The middle initial. */
	@Column(name="middleinitial")
	private String middleInitial;

	/** The last name. */
	@Column(name="lastname")
	private String lastName;

	/** The sur name. */
	@Column(name="surname")
	private String surName;

	/** The family name. */
	@Column(name="familyname")
	private String familyName;

	/** The full name. */
	@Column(name="fullname")
	private String fullName;

	/** The gender code. */
	@Column(name="gender_code",nullable=false)
	private String genderCode;

	/** The parent full name. */
	@Column(name="parent_fullname")
	private String parentFullName;
	
	/** The parent ref id type. */
	@Column(name="parent_ref_id_type")
	private String parentRefIdType;
	
	/** The parent ref id. */
	@Column(name="parent_ref_id")
	private String parentRefId;
	
	/** The dob. */
	@Column(name="dob")
	private Date dob;
	
	/** The age. */
	@Column(name="age")
	private Integer age;
	
	/** The addr line 1. */
	@Column(name="addr_line1")
	private String 	addrLine1;
	
	/** The addr line 2. */
	@Column(name="addr_line2")
	private String addrLine2;
	
	/** The addr line 3. */
	@Column(name="addr_line3")
	private String addrLine3;
	
	/** The location code. */
	@Column(name="location_code",nullable=false)
	private String locationCode;
	
	/** The mobile. */
	@Column(name="mobile")
	private String mobile;
	
	/** The email. */
	@Column(name="email")
	private String email;
	
	/** The applicant type. */
	@Column(name="applicant_type",nullable=false)
	private String applicantType;
	
	/** The national id. */
	@Column(name="nationalid")
	private String nationalId;
	
	/** The status code. */
	@Column(name="status_code",nullable=false)
	private String statusCode;
	
	/** The lang code. */
	@Column(name="lang_code")
	private String langCode;
	
	/** The is active. */
	@Column(name = "is_active")
	boolean isActive;

	/** The created by. */
	@Column(name = "cr_by")
	String createdBy;

	/** The created on. */
	@Column(name = "cr_dtimes")
	Date createdOn;

	/** The updated by. */
	@Column(name = "upd_by")
	String updatedBy;

	/** The updated on. */
	@Column(name = "upd_dtimes")
	Date updatedOn;

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

		
}
