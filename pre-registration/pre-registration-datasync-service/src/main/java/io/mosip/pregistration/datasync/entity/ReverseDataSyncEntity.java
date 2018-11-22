package io.mosip.pregistration.datasync.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Component
@Entity
@Table(name = "i_processed_prereg_list", schema = "prereg")
@Data
public class ReverseDataSyncEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2624621243252003059L;

	@Id
	/*@SequenceGenerator(name = "i_processed_prereg_list_id_seq", sequenceName = "i_processed_prereg_list_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "i_processed_prereg_list_id_seq")
	@Column(name = "id")
	private int dataSyncId;*/

	//@Column(name = "prereg_id")
	@EmbeddedId
	private Ipprlst_PK ipprlst_PK;

	/*@Id
	@Column(name = "received_dtimes")
	private Timestamp receivedDTime;*/

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
