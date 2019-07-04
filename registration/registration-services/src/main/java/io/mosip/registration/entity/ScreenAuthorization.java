package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.registration.entity.id.ScreenAuthorizationId;
import lombok.Getter;
import lombok.Setter;

/**
 * The Entity Class for Screen Authorization details
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "screen_authorization")
@Getter
@Setter
public class ScreenAuthorization extends RegistrationCommonFields {

	@EmbeddedId
	private ScreenAuthorizationId screenAuthorizationId;

	@Column(name = "lang_code")
	private String langCode;
	@Column(name = "is_permitted")
	private Boolean isPermitted;
	@Column(name = "is_deleted")
	private Boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp delDtimes;

}
