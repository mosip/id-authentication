package org.mosip.demo.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Enrollment entity class
 * 
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "Enrollment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@NamedQuery(name = "findAllCenterWithName", query = "SELECT center FROM Enrollment center WHERE center.enrollmentCenterName LIKE :name")
public class Enrollment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7366618827172718974L;

	/**
	 * The integer enrollment center id
	 */
	@Id
	private Integer enrollmentId;

	/**
	 * List of persons for this enrollment center
	 */
	@OneToMany(mappedBy = "enrollmentCenter", fetch = FetchType.LAZY)
	@ElementCollection
	@JsonIgnore
	private List<Person> personId;

	/**
	 * The string enrollment center name
	 */
	private String enrollmentCenterName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Enrollment [enrollmentId=");
		builder.append(enrollmentId);
		builder.append(", enrollmentCenterName=");
		builder.append(enrollmentCenterName);
		builder.append("]");
		return builder.toString();
	}

}
