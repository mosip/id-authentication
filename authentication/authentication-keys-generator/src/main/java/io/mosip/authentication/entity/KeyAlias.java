package io.mosip.authentication.entity;

import java.beans.ConstructorProperties;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Generated;

@Entity
@Table(name = "key_alias")
public class KeyAlias extends BaseEntity {
	@Id
	@Column(name = "id", nullable = false, length = 36)
	private String alias;
	@Column(name = "app_id", nullable = false, length = 36)
	private String applicationId;
	@Column(name = "ref_id", length = 36)
	private String referenceId;
	@Column(name = "key_gen_dtimes")
	private LocalDateTime keyGenerationTime;
	@Column(name = "key_expire_dtimes")
	private LocalDateTime keyExpiryTime;
	@Column(name = "status_code", length = 36)
	private String status;

	@Generated
	public String getAlias() {
		return this.alias;
	}

	@Generated
	public String getApplicationId() {
		return this.applicationId;
	}

	@Generated
	public String getReferenceId() {
		return this.referenceId;
	}

	@Generated
	public LocalDateTime getKeyGenerationTime() {
		return this.keyGenerationTime;
	}

	@Generated
	public LocalDateTime getKeyExpiryTime() {
		return this.keyExpiryTime;
	}

	@Generated
	public String getStatus() {
		return this.status;
	}

	@Generated
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Generated
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	@Generated
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	@Generated
	public void setKeyGenerationTime(LocalDateTime keyGenerationTime) {
		this.keyGenerationTime = keyGenerationTime;
	}

	@Generated
	public void setKeyExpiryTime(LocalDateTime keyExpiryTime) {
		this.keyExpiryTime = keyExpiryTime;
	}

	@Generated
	public void setStatus(String status) {
		this.status = status;
	}

	@Generated
	public String toString() {
		return "KeyAlias(alias=" + this.getAlias() + ", applicationId=" + this.getApplicationId() + ", referenceId="
				+ this.getReferenceId() + ", keyGenerationTime=" + this.getKeyGenerationTime() + ", keyExpiryTime="
				+ this.getKeyExpiryTime() + ", status=" + this.getStatus() + ")";
	}

	@Generated
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof KeyAlias)) {
			return false;
		} else {
			KeyAlias other = (KeyAlias) o;
			if (!other.canEqual(this)) {
				return false;
			} else if (!super.equals(o)) {
				return false;
			} else {
				Object this$alias = this.getAlias();
				Object other$alias = other.getAlias();
				if (this$alias == null) {
					if (other$alias != null) {
						return false;
					}
				} else if (!this$alias.equals(other$alias)) {
					return false;
				}

				Object this$applicationId = this.getApplicationId();
				Object other$applicationId = other.getApplicationId();
				if (this$applicationId == null) {
					if (other$applicationId != null) {
						return false;
					}
				} else if (!this$applicationId.equals(other$applicationId)) {
					return false;
				}

				label71 : {
					Object this$referenceId = this.getReferenceId();
					Object other$referenceId = other.getReferenceId();
					if (this$referenceId == null) {
						if (other$referenceId == null) {
							break label71;
						}
					} else if (this$referenceId.equals(other$referenceId)) {
						break label71;
					}

					return false;
				}

				label64 : {
					Object this$keyGenerationTime = this.getKeyGenerationTime();
					Object other$keyGenerationTime = other.getKeyGenerationTime();
					if (this$keyGenerationTime == null) {
						if (other$keyGenerationTime == null) {
							break label64;
						}
					} else if (this$keyGenerationTime.equals(other$keyGenerationTime)) {
						break label64;
					}

					return false;
				}

				Object this$keyExpiryTime = this.getKeyExpiryTime();
				Object other$keyExpiryTime = other.getKeyExpiryTime();
				if (this$keyExpiryTime == null) {
					if (other$keyExpiryTime != null) {
						return false;
					}
				} else if (!this$keyExpiryTime.equals(other$keyExpiryTime)) {
					return false;
				}

				Object this$status = this.getStatus();
				Object other$status = other.getStatus();
				if (this$status == null) {
					if (other$status != null) {
						return false;
					}
				} else if (!this$status.equals(other$status)) {
					return false;
				}

				return true;
			}
		}
	}

	@Generated
	protected boolean canEqual(Object other) {
		return other instanceof KeyAlias;
	}

	@Generated
	public KeyAlias() {
	}

	@ConstructorProperties({"alias", "applicationId", "referenceId", "keyGenerationTime", "keyExpiryTime", "status"})
	@Generated
	public KeyAlias(String alias, String applicationId, String referenceId, LocalDateTime keyGenerationTime,
			LocalDateTime keyExpiryTime, String status) {
		this.alias = alias;
		this.applicationId = applicationId;
		this.referenceId = referenceId;
		this.keyGenerationTime = keyGenerationTime;
		this.keyExpiryTime = keyExpiryTime;
		this.status = status;
	}
}