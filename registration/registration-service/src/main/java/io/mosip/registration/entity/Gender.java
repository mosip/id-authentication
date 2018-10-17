package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(schema = "master", name = "gender")
@Data
@EqualsAndHashCode(callSuper = true)
public class Gender extends MasterCommonFields {
	@EmbeddedId
	private GenericId genericId;
	@Column(name = "name", length = 64, nullable = false)
	private String name;

}
