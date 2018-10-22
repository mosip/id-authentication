package org.mosip.demo.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The person entity class
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "Person")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5214115990348844220L;

	/**
	 * The integer person id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer personId;

	/**
	 * The string first name of person
	 */
	@Column
	private String firstName;

	/**
	 * The string last name of the person
	 */
	@Column
	private String lastName;

	/**
	 * The string age of the person
	 */
	@Column
	private String age;

	/**
	 * The string address of the person
	 */
	@Column
	private String address;

	/**
	 * The enrollment center related to this person
	 */
	@OneToOne 
	private Enrollment enrollmentCenter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Person [personId=");
		builder.append(personId);
		builder.append(", firstName=");
		builder.append(firstName);
		builder.append(", lastName=");
		builder.append(lastName);
		builder.append(", age=");
		builder.append(age);
		builder.append(", address=");
		builder.append(address);
		builder.append("]");
		return builder.toString();
	}

}
