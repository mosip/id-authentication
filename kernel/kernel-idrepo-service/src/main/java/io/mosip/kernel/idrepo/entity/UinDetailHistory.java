package io.mosip.kernel.idrepo.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	private Date effectiveDateTime;
	
	/** The uin data. */
	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Basic(fetch=FetchType.LAZY)
	private byte[] uinData;
	
	/** The created by. */
	@Column(name = "cr_by")
	private String createdBy;
	
	/** The created date time. */
	@Column(name = "cr_dtimes")
	private Date createdDateTime;
	
	/** The updated by. */
	@Column(name = "upd_by")
	private String updatedBy;
	
	/** The updated date time. */
	@Column(name = "upd_dtimes")
	private Date updatedDateTime;
	
	/** The is deleted. */
	private Boolean isDeleted;
	
	/** The deleted date time. */
	@Column(name = "del_dtimes")
	private Date deletedDateTime;
}
