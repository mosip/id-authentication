package io.mosip.registration.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * AppAuthenticationMethod entity details
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
	private int methodSeq;
	
	@Column(name = "lang_code")
	private String langCode;

}
