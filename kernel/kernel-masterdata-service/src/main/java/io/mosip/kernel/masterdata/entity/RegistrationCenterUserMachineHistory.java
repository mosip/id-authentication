package io.mosip.kernel.masterdata.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entity class for User and Registration mappings
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "reg_center_user_machine_h", schema = "master")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationCenterUserMachineHistory implements Serializable {

	/**
	 * Generated Serial Id
	 */
	private static final long serialVersionUID = -4167453471874926985L;

	/**
	 * Composite key for this table
	 */
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "cntrId", column = @Column(name = "cntr_id", nullable = false, length = 36)),
			@AttributeOverride(name = "usrId", column = @Column(name = "usr_id", nullable = false, length = 36)),
			@AttributeOverride(name = "machineId", column = @Column(name = "machine_id", nullable = false, length = 36)) })
	private RegistrationCenterUserMachineHistoryId id;

	/**
	 * Mapping is active or not
	 */
	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	/**
	 * Created By
	 */
	@Column(name = "cr_by", nullable = false, length = 24)
	private String createdBy;

	/**
	 * Created Times
	 */
	@Column(name = "cr_dtimes", nullable = false)
	private LocalDateTime createdtime;

	/**
	 * Updated By
	 */
	@Column(name = "upd_by", length = 24)
	private String updatedBy;

	/**
	 * Updated Times
	 */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedtime;

	/**
	 *  Is Deleted or not
	 */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/**
	 * Deletion Times
	 */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedtime;

	/**
	 * Effective TimeStamp
	 */
	@Column(name = "eff_dtimes", nullable = false)
	private LocalDateTime effectivetimes;

}
