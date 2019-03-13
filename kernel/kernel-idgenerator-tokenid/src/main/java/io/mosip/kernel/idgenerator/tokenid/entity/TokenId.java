package io.mosip.kernel.idgenerator.tokenid.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Entity class for tokenid.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Data
@Entity
@Table(name = "tokenid", schema = "ida")
public class TokenId {

	/**
	 * Random value.
	 */
	@Id
	private String randomValue;

	/**
	 * Random number Counter for encryption.
	 */
	@Column(name = "seq_counter")
	private String sequenceCounter;
}
