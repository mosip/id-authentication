package io.mosip.preregistration.datasync.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author M1046129 - Jagadishwari
 *
 */
@Entity
@Table(name = "processed_prereg_list", schema = "prereg")
@Getter
@Setter
@NoArgsConstructor
public class ProcessedPreRegEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -239434336226779852L;

	@Id
	@Column(name = "prereg_id")
	private String preRegistrationId;

	@Column(name = "first_received_dtimes")
	private LocalDateTime receivedDTime;

	@Column(name = "status_code")
	private String statusCode;

	@Column(name = "status_comments")
	private String statusComments;

	@Column(name = "lang_code")
	private String langCode;

	@Column(name = "cr_by")
	private String crBy;

	@Column(name = "cr_dtimes")
	private LocalDateTime crDate;

	@Column(name = "upd_by")
	private String upBy;

	@Column(name = "upd_dtimes")
	private LocalDateTime updDate;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Column(name = "del_dtimes")
	private LocalDateTime delTime;

}
