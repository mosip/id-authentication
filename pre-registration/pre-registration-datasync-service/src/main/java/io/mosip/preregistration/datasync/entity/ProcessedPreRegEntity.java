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

	/**
	 * The PreRegistration Id.
	 */
	@Id
	@Column(name = "prereg_id")
	private String preRegistrationId;
	
	/**
	 * The first received time.
	 */
	@Column(name = "first_received_dtimes")
	private LocalDateTime receivedDTime;
	
	/**
	 * The status code.
	 */
	@Column(name = "status_code")
	private String statusCode;
	
	/**
	 * The status comment.
	 */
	@Column(name = "status_comments")
	private String statusComments;
	
	/**
	 * The PreRegistration transaction Id. 
	 */
	@Column(name = "prereg_trn_id")
	private String preregTrnId;
	
	/**
	 * The Language code.
	 */
	@Column(name = "lang_code")
	private String langCode;
	
	/**
	 * The created by
	 */
	@Column(name = "cr_by")
	private String crBy;
	
	/**
	 * The created time.
	 */
	@Column(name = "cr_dtimes")
	private LocalDateTime crDate;
	
	/**
	 * The updated by.
	 */
	@Column(name = "upd_by")
	private String upBy;
	
	/**
	 * The updated time.
	 */
	@Column(name = "upd_dtimes")
	private LocalDateTime updDate;
	
	/**
	 * The is deleted.
	 */
	@Column(name = "is_deleted")
	private boolean isDeleted;
	
	/**
	 * The deleted time.
	 */
	@Column(name = "del_dtimes")
	private LocalDateTime delTime;

}
