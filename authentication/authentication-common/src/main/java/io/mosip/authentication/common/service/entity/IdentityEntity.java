package io.mosip.authentication.common.service.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
/**
 * The entity for the Identity records stored in identity_cache table in IDA DB
 * 
 * @author Loganathan Sekar
 *
 */
@Getter 
@Setter 
@ToString 
@Entity
@NoArgsConstructor
@Table(schema = "ida", name = "identity_cache")
public class IdentityEntity {

	@Id
	@NotNull
	@Column(name = "id")
	private String id;
	
	@NotNull
	@Column(name = "demographic_data")
	private byte[] demographicData;
	
	@NotNull
	@Column(name = "biometric_data")
	private byte[] biometricData;
	
	@Column(name = "expiry_timestamp")
	private LocalDateTime expiryTimestamp;
	
	@Column(name = "transaction_limit")
	private Integer transactionLimit;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdentityEntity other = (IdentityEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}
