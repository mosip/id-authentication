package org.mosip.registration.entity;

import java.io.Serializable;
import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
@Table(schema="reg", name = "center")
public class RegistrationCenter implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3700579398127105264L;
	@Id
	@Column(name="id", length=64, nullable=false, updatable=false)
	private String id;
	@Column(name="name", length=64, nullable=true, updatable=false)
	private String name;
	@Column(name="addr_line1", length=256, nullable=true, updatable=false)
	private String addrLine1;
	@Column(name="addr_line2", length=256, nullable=true, updatable=false)
	private String addrLine2;
	@Column(name="addr_line3", length=256, nullable=true, updatable=false)
	private String addrLine3;
	@Column(name="loc_line1", length=128, nullable=true, updatable=false)
	private String locLine1;
	@Column(name="loc_line2", length=128, nullable=true, updatable=false)
	private String locLine2;
	@Column(name="loc_line3", length=128, nullable=true, updatable=false)
	private String locLine3;
	@Column(name="loc_line4", length=128, nullable=true, updatable=false)
	private String locLine4;
	@Column(name="latitude", nullable=true, updatable = false)
	private float latitude;
	@Column(name="longitude", nullable=true, updatable = false)
	private float longitude;
	@Column(name="country", length=64, nullable=true, updatable=false)
	private String country;
	@Column(name="pincode", length=16, nullable=true, updatable=false)
	private String pincode;
	@Column(name="lang_code", length=3, nullable=false, updatable=false)
	private String langCode;
	@Column(name="is_active", nullable=false, updatable=false)
	@Type(type= "true_false")
	private boolean isActive;
	@Column(name="cr_by", length=24, nullable=false, updatable=false)
	private String crBy;
	@Column(name="cr_dtimes", nullable=false, updatable=false)
	private OffsetDateTime crDtimes;
	@Column(name="upd_by", length=24, nullable=true, updatable=false)
	private String updBy;
	@Column(name="upd_dtimes", nullable=true, updatable=false)
	private OffsetDateTime updDtimes;
	@Column(name="is_deleted", nullable=true, updatable=false)
	@Type(type= "true_false")
	private boolean isDeleted;
	@Column(name="del_dtimes", nullable=true, updatable=false)
	private OffsetDateTime delDtimes;
}
