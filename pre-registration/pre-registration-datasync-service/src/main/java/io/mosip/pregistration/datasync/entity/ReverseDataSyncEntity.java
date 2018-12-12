package io.mosip.pregistration.datasync.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import lombok.Data;
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
public class ReverseDataSyncEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2624621243252003059L;

	@Id
	@EmbeddedId
	private InterfaceDataSyncTablePK ipprlst_PK;

	@Column(name = "lang_code")
	private String langCode;

	@Column(name = "cr_by")
	private String crBy;

	@Column(name = "cr_dtimes")
	private Timestamp crDate;

	@Column(name = "upd_by")
	private String upBy;

	@Column(name = "upd_dtimes")
	private Timestamp updDate;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Column(name = "del_dtimes")
	private Timestamp delTime;

}
