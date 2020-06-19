package io.mosip.authentication.entity;

import java.beans.ConstructorProperties;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Generated;

@MappedSuperclass
public class BaseEntity {
	@Column(name = "cr_by", length = 256)
	private String createdBy;
	@Column(name = "cr_dtimes")
	private LocalDateTime createdtimes;
	@Column(name = "upd_by", length = 256)
	private String updatedBy;
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedtimes;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private LocalDateTime deletedtimes;

	@Generated
	public String getCreatedBy() {
		return this.createdBy;
	}

	@Generated
	public LocalDateTime getCreatedtimes() {
		return this.createdtimes;
	}

	@Generated
	public String getUpdatedBy() {
		return this.updatedBy;
	}

	@Generated
	public LocalDateTime getUpdatedtimes() {
		return this.updatedtimes;
	}

	@Generated
	public Boolean getIsDeleted() {
		return this.isDeleted;
	}

	@Generated
	public LocalDateTime getDeletedtimes() {
		return this.deletedtimes;
	}

	@Generated
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	@Generated
	public void setCreatedtimes(LocalDateTime createdtimes) {
		this.createdtimes = createdtimes;
	}

	@Generated
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	@Generated
	public void setUpdatedtimes(LocalDateTime updatedtimes) {
		this.updatedtimes = updatedtimes;
	}

	@Generated
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Generated
	public void setDeletedtimes(LocalDateTime deletedtimes) {
		this.deletedtimes = deletedtimes;
	}

	@Generated
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof BaseEntity)) {
			return false;
		} else {
			BaseEntity other = (BaseEntity) o;
			if (!other.canEqual(this)) {
				return false;
			} else {
				Object this$createdBy = this.getCreatedBy();
				Object other$createdBy = other.getCreatedBy();
				if (this$createdBy == null) {
					if (other$createdBy != null) {
						return false;
					}
				} else if (!this$createdBy.equals(other$createdBy)) {
					return false;
				}

				Object this$createdtimes = this.getCreatedtimes();
				Object other$createdtimes = other.getCreatedtimes();
				if (this$createdtimes == null) {
					if (other$createdtimes != null) {
						return false;
					}
				} else if (!this$createdtimes.equals(other$createdtimes)) {
					return false;
				}

				Object this$updatedBy = this.getUpdatedBy();
				Object other$updatedBy = other.getUpdatedBy();
				if (this$updatedBy == null) {
					if (other$updatedBy != null) {
						return false;
					}
				} else if (!this$updatedBy.equals(other$updatedBy)) {
					return false;
				}

				label62 : {
					Object this$updatedtimes = this.getUpdatedtimes();
					Object other$updatedtimes = other.getUpdatedtimes();
					if (this$updatedtimes == null) {
						if (other$updatedtimes == null) {
							break label62;
						}
					} else if (this$updatedtimes.equals(other$updatedtimes)) {
						break label62;
					}

					return false;
				}

				label55 : {
					Object this$isDeleted = this.getIsDeleted();
					Object other$isDeleted = other.getIsDeleted();
					if (this$isDeleted == null) {
						if (other$isDeleted == null) {
							break label55;
						}
					} else if (this$isDeleted.equals(other$isDeleted)) {
						break label55;
					}

					return false;
				}

				Object this$deletedtimes = this.getDeletedtimes();
				Object other$deletedtimes = other.getDeletedtimes();
				if (this$deletedtimes == null) {
					if (other$deletedtimes != null) {
						return false;
					}
				} else if (!this$deletedtimes.equals(other$deletedtimes)) {
					return false;
				}

				return true;
			}
		}
	}

	@Generated
	protected boolean canEqual(Object other) {
		return other instanceof BaseEntity;
	}

	@Generated
	public String toString() {
		return "BaseEntity(createdBy=" + this.getCreatedBy() + ", createdtimes=" + this.getCreatedtimes()
				+ ", updatedBy=" + this.getUpdatedBy() + ", updatedtimes=" + this.getUpdatedtimes() + ", isDeleted="
				+ this.getIsDeleted() + ", deletedtimes=" + this.getDeletedtimes() + ")";
	}

	@ConstructorProperties({"createdBy", "createdtimes", "updatedBy", "updatedtimes", "isDeleted", "deletedtimes"})
	@Generated
	public BaseEntity(String createdBy, LocalDateTime createdtimes, String updatedBy, LocalDateTime updatedtimes,
			Boolean isDeleted, LocalDateTime deletedtimes) {
		this.createdBy = createdBy;
		this.createdtimes = createdtimes;
		this.updatedBy = updatedBy;
		this.updatedtimes = updatedtimes;
		this.isDeleted = isDeleted;
		this.deletedtimes = deletedtimes;
	}

	@Generated
	public BaseEntity() {
	}
}