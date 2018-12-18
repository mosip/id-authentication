package io.mosip.preregistration.batchjobservices.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author M1043008
 *
 * The persistent class Processed PreReg List database table.
 */
@Component
@Entity
@Table(name = "processed_prereg_list")
@Getter
@Setter
@NoArgsConstructor
public class Processed_Prereg_List {
	
	/**
	 * The PreRegistration Id.
	 */
	@Id
	private String prereg_id;
	
	/**
	 * The first received time.
	 */
	@Column(name="first_received_dtimes")
	private Timestamp firstReceivedDtimes;
	
	/**
	 * The status code.
	 */
	@Column(name="status_code")
	private String statusCode;
	
	/**
	 * The status comment.
	 */
	private String status_comments;
	
	/**
	 * The PreRegistration transaction Id. 
	 */
	private String prereg_trn_id;
	
	/**
	 * The Language code.
	 */
	private String lang_code;
	
	/**
	 * The created by
	 */
	private String cr_by;
	
	/**
	 * The created time.
	 */
	private Timestamp cr_dtimes;
	
	/**
	 * The updated by.
	 */
	private String upd_by;
	
	/**
	 * The updated time.
	 */
	private Timestamp upd_times;
	
	/**
	 * The is deleted.
	 */
	private boolean is_deleted;
	
	/**
	 * The deleted time.
	 */
	private Timestamp del_dtimes;
	
	/**
	 * The is new.
	 */
	@Column(name="is_new")
	private boolean isNew;

	/**
	 * Gets the is deleted
	 * @return is_deleted.
	 */
	public boolean isIs_deleted() {
		return is_deleted;
	}

	/**
	 * Sets the is_deleted
	 * @param is_deleted
	 */
	public void setIs_deleted(boolean is_deleted) {
		this.is_deleted = is_deleted;
	}

}
