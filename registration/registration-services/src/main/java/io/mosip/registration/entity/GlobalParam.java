package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(schema = "master", name = "global_param")
@Getter
@Setter
public class GlobalParam extends RegistrationCommonFields {

	@EmbeddedId
	@Column(name = "pk_glbp_code")
	private GlobalParamId globalParamId;

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
