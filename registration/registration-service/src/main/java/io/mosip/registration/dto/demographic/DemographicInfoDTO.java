package io.mosip.registration.dto.demographic;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.mosip.registration.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class used to capture the Demographic details of the Individual
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DemographicInfoDTO extends BaseDTO {

	private String firstName;
	private String middleName;
	private String lastName;
	@JsonIgnore
	private String fullName;
	protected LocalDate dateOfBirth;
	protected String gender;
	protected AddressDTO addressDTO;
	protected String emailId;
	protected String mobile;
	protected String landLine;
	protected String languageCode;
	protected boolean isChild;
	protected String age;
}
