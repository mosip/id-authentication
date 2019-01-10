package io.mosip.preregistration.datasync.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author M1046129
 *
 */
@Component
@Entity
@Table(name = "i_processed_prereg_list", schema = "prereg")
@Getter
@Setter
@NoArgsConstructor
public class InterfaceDataSyncEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2624621243252003059L;

	@Id
	@EmbeddedId
	private InterfaceDataSyncTablePK ipprlst_PK;

	@Column(name = "lang_code")
	private String langCode;

	@Column(name = "cr_by")
	private String createdBy;

	@Column(name = "cr_dtimes")
	private LocalDateTime createdDate;

	@Column(name = "upd_by")
	private String updatedBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDate;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime delTime;

}
