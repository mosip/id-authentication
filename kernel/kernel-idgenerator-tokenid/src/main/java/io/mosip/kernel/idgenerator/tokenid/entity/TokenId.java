package io.mosip.kernel.idgenerator.tokenid.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * TokenId generator entity with lombok dependency
 * 
 * @author Srinivasan
 *
 */
@Data
@Entity
@Table(name="tokenid", schema ="ids")
@AllArgsConstructor
@NoArgsConstructor
public class TokenId {
	
	
	
	@Id
	@Column(name = "id", unique = true, nullable = false, updatable = false)
	@NonNull
	private String id;
	@Column(name = "created_at")
	private long createdAt;
	
	
}
