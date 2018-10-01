package org.mosip.kernel.pridgenerator.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "prid", schema = "ids")
public class Prid {
	@Id
	@Column(name = "id", unique = true, nullable = false, updatable = false, length = 14)
	private String id;
	@Column(name = "createdAt")
	private long createdAt;
}
