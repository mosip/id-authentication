package org.mosip.kernel.pridgenerator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "prids" ,schema="pid")
public class Prid {
	@Id
	@Column(name = "prid", unique = true, nullable = false, updatable = false, length = 14)
	private String id;
	@Column(name = "generatedTime")
	private long generatedTime;
	

}
