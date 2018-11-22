package io.mosip.pregistration.datasync.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;

import lombok.Data;
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
public class PreRegistrationProcessedEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -239434336226779852L;

	@Id
	@Column(name = "prereg_id")
	private String preRegistrationId;

	@Column(name = "first_received_dtimes")
	private Timestamp receivedDTime;

	@Column(name = "status_code")
	private String statusCode;

	@Column(name = "status_comments")
	private String statusComments;

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
