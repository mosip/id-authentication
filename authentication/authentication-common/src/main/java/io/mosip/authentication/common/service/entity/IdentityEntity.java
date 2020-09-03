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
 * The entity for the Identity records stored in identity_cache table in IDA DB.
 *
 * @author Loganathan Sekar
 */
@Getter 
@Setter 
@ToString 
@Entity
@NoArgsConstructor
@Table(schema = "ida", name = "identity_cache")
public class IdentityEntity {

	/** The id. */
	@Id
	@NotNull
	@Column(name = "id")
	private String id;
	
	@NotNull
	@Column(name = "token_id")
	private String token;
	
	/** The demographic data. */
	@NotNull
	@Column(name = "demo_data")
	private byte[] demographicData;
	
	/** The biometric data. */
	@NotNull
	@Column(name = "bio_data")
	private byte[] biometricData;
	
	/** The expiry timestamp. */
	@Column(name = "expiry_timestamp")
	private LocalDateTime expiryTimestamp;
	
	/** The transaction limit. */
	@Column(name = "transaction_limit")
	private Integer transactionLimit;
	
	@NotNull
	@Column(name = "cr_by")
	private String crBy;

	@NotNull
	@Column(name = "cr_dtimes")
	private LocalDateTime crDTimes;

	@Column(name = "upd_by")
	private String updBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updDTimes;

	@Column(name = "is_deleted")
	private boolean isDeleted;
	
	@Column(name = "del_dtimes")
	private LocalDateTime delDTimes;

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
