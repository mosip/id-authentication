package org.mosip.registration.processor.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Registration entity details
 * @author M1047595
 *
 */
@Data
@Entity
@Table(schema="reg", name = "REGISTRATION")
public class Registration implements Serializable {
	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = -8969928961711339471L;
	@Column
	private String pkt_name;
	@Id
	@Column
	private Integer enrl_id;
	@Column
	private String pkt_type_code;
	@Column
	private String filespath;
	@Column
	private String ack_filename;
	@Column
	private String client_status_code;
	@Column
	private String server_status_code;
	@Column
	private String status_comments;
	@Column
	private String file_sync_status;
	@Column
	private short sync_count;
	@Column
	private Date sync_dtime;
	@Column
	private String scan_status;
	@Column
	private Date scan_datetimes;
	@Column
	private String lang_code;
	@Column
	private String is_active;
	@Column
	private String cr_by;
	@Column
	private Date cr_dtimes;
	@Column
	private String upd_by;
	@Column
	private Date upd_dtimes;
	@Column
	private String json_store;
	
	
}
