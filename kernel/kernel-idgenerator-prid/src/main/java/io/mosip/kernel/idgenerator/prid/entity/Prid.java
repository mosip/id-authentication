package io.mosip.kernel.idgenerator.prid.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "prid", schema = "master")
public class Prid {

	@Id
	private String randomValue;

	@Column(name = "seq_counter")
	private String sequenceCounter;
}
