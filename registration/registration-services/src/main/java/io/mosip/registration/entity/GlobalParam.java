package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * Global parame entity details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "global_param")
@Getter
@Setter
public class GlobalParam extends RegistrationCommonFields {

	@Id
	@Column(name = "code")
	private String code;
	@Column(name = "lang_code")
	private String langCode;	

	@Column(name = "name")
	private String name;
	@Column(name = "val")
	private String val;
	@Column(name = "typ")
	private String typ;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;

}
