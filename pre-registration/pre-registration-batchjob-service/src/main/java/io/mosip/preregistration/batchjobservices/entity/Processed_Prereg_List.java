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

@Component
@Entity
@Table(name = "processed_prereg_list")
@Getter
@Setter
@NoArgsConstructor
public class Processed_Prereg_List {
	
	@Id
	private String prereg_id;
	
	@Column(name="first_received_dtimes")
	private Timestamp firstReceivedDtimes;
	
	@Column(name="status_code")
	private String statusCode;
	
	private String status_comments;
	
	private String prereg_trn_id;
	
	private String lang_code;
	
	private String cr_by;
	
	private Timestamp cr_dtimes;
	
	private String upd_by;
	
	private Timestamp upd_times;
	
	private boolean is_deleted;
	
	private Timestamp del_dtimes;
	
	@Column(name="is_new")
	private boolean isNew;

	public boolean isIs_deleted() {
		return is_deleted;
	}

	public void setIs_deleted(boolean is_deleted) {
		this.is_deleted = is_deleted;
	}

}
