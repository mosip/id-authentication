/**
 * 
 */
package io.mosip.kernel.dataaccess.hibernate.test.model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "Person", schema = "master")
public class Person {

	@javax.persistence.Id
	private int Id;

	private String name;

	
	/**
	 * No args constructor
	 */
	public Person() {
		super();
	}
	/**
	 * @param string
	 */
	public Person(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return Id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		Id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Person [Id=");
		builder.append(Id);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}
}
