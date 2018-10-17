package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(schema = "master", name = "valid_document")
@Data
public class DocumentFormat extends MasterCommonFields {
	@EmbeddedId
	private GenericId genericId;
	@Column(name = "name", length = 64, nullable = false)
	private String name;
	@Column(name = "descr", length = 128, nullable = true)
	private String description;

}
