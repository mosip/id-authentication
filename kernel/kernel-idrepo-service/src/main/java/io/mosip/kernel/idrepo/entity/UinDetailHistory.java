package io.mosip.kernel.idrepo.entity;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class UinDetailHistory.
 *
 * @author Manoj SP
 */

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(HistoryPK.class)
@Table(name = "uin_detail_h", schema = "uin")
public class UinDetailHistory {

	/** The uin ref id. */
	@Id
	private String uinRefId;

	/** The effective date time. */
	@Id
	@Column(name = "eff_dtimes")
	private LocalDateTime effectiveDateTime;

	/** The uin data. */
	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Basic(fetch = FetchType.LAZY)
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private byte[] uinData;

	/** The created by. */
	@Column(name = "cr_by")
	private String createdBy;

	/** The created date time. */
	@Column(name = "cr_dtimes")
	private LocalDateTime createdDateTime;

	/** The updated by. */
	@Column(name = "upd_by")
	private String updatedBy;

	/** The updated date time. */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDateTime;

	/** The is deleted. */
	private Boolean isDeleted;

	/** The deleted date time. */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedDateTime;

	/**
	 * Gets the uin data.
	 *
	 * @return the uin data
	 */
	public byte[] getUinData() {
		return uinData;
	}

	/**
	 * Sets the uin data.
	 *
	 * @param uinData
	 *            the new uin data
	 */
	public void setUinData(byte[] uinData) {
		this.uinData = uinData.clone();
	}
}
