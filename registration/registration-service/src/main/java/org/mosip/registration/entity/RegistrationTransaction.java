package org.mosip.registration.entity;

import java.io.Serializable;
import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import lombok.Data;

/**
 * The enity class for Registration Transaction
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
@Entity
@Table(schema="REG", name="REGISTRATION_TRANSACTION")
@Data
public class RegistrationTransaction implements Serializable {

	/**
	 * Auto-Generated Serial Version Id
	 */
	private static final long serialVersionUID = 3504279571852674128L;
	@Id
	@Column(name="ID", length=32, nullable=false, updatable=false)
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String id;
	@Column(name="REG_ID", length=28, nullable=true, updatable=true)
	private String regId;
	@Column(name="PARENT_REGTRN_ID", length=32, nullable=true, updatable=true)
	private String parentRegTrnId;
	@Column(name="TRN_TYPE_CODE", length=64, nullable=false, updatable=true)
	private String trnTypeCode;
	@Column(name="REMARKS", length=1024, nullable=true, updatable=true)
	private String remarks;
	@Column(name="STATUS_CODE", length=64, nullable=false, updatable=true)
	private String statusCode;
	@Column(name="LANG_CODE", length=3, nullable=true, updatable=true)
	private String langCode;
	@Column(name="STATUS_COMMENT", length=1024, nullable=true, updatable=true)
	private String statusComment;
	@Column(name="IS_ACTIVE", nullable=true, updatable=true)
	@Type(type= "true_false")
	private Boolean isActive;
	@Column(name="CR_BY", length=32, nullable=false, updatable=true)
	private String crBy;
	@Column(name="CR_DTIMES", nullable=false, updatable=true)
	private OffsetDateTime crDtime;
	@Column(name="UPD_BY", length=32, nullable=true, updatable=true)
	private String updBy;
	@Column(name="UPD_DTIMES", nullable=true, updatable=false)
	private OffsetDateTime updDtimes;
	
}
