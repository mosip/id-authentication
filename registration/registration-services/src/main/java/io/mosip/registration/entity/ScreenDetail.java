package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.registration.entity.id.ScreenDetailId;
import lombok.Getter;
import lombok.Setter;

/**
 * The Entity Class for Screen Detail
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */

@Entity
@Table(schema = "reg", name = "screen_detail")
@Getter
@Setter
public class ScreenDetail extends RegistrationCommonFields{	
	
	@EmbeddedId
	private ScreenDetailId screenDetailId;
	
	@Column(name = "app_id")
	private String appId;
	@Column(name = "name")
	private String name;
	@Column(name = "descr")
	private String descr;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;
}
