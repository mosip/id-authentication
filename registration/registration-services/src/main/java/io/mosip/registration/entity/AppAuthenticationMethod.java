package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import io.mosip.registration.entity.id.AppAuthenticationMethodId;
import lombok.Getter;
import lombok.Setter;

/**
 * This entity class(table) will hold the user's different mode of Authentication such as 
 * Fingerprint/Password/Iris/Face Authentication in different scenarios like
 * Login Authentication, Packet Authentication, EOD Authentication.
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Entity
@Table(schema = "reg", name = "app_authentication_method")
@Getter
@Setter
public class AppAuthenticationMethod extends RegistrationCommonFields {

	@EmbeddedId
	private AppAuthenticationMethodId appAuthenticationMethodId;

	@Column(name = "method_seq")
	private Integer methodSequence;
	
	@Column(name = "lang_code")
	private String langCode;

}
