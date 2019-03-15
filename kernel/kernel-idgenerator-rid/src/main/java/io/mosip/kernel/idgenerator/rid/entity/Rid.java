package io.mosip.kernel.idgenerator.rid.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * The entity for rid generator.
 * 
 * @author Ritesh Sinha
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "rid_seq", schema = "reg")
@Data
public class Rid {


	/**
	 * the current sequence no.
	 * 
	 */
	@Id
	@Column(name = "curr_seq_no")
	private int currentSequenceNo;
}