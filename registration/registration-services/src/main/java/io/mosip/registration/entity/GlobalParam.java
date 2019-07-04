package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.registration.entity.id.GlobalParamId;
import lombok.Getter;
import lombok.Setter;

/**
 * The Entity Class for Global param
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "global_param")
@Getter
@Setter
public class GlobalParam extends RegistrationCommonFields {
	
	@EmbeddedId
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
	
	/**
	 * @param updDtimes the updDtimes to set
	 */
	@Override
	public void setUpdDtimes(Timestamp updDtimes) {
		this.updDtimes = updDtimes;
	}
}
