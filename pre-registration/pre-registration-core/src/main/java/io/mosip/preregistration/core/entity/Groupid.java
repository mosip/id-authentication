package io.mosip.preregistration.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "groupid", schema = "prereg")
public class Groupid {
	@Id
	@Column(name = "id", unique = true, nullable = false, updatable = false, length = 12)
	private String id;
	@Column(name = "created_at")
	private long createdAt;
}
