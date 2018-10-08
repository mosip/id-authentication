package org.mosip.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Person data transfer class
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto {
	/**
	 * The String first name of the person
	 */
	private String firstName;
	
	/**
	 * The string last name of the person
	 */
	private String lastName;
	
	/**
	 * The string age of the person
	 */
	private String age;
	
	/**
	 * The string address of the person
	 */
	private String address;
	
	/**
	 * The integer enrollment id of the enrollment center
	 */
	private int enrollmentId;

}
