package io.mosip.registration.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

/**
 * Composite key for AppAuthenticationMethod entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
@Getter
@Setter
public class AppAuthenticationMethodId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "app_id")
	private String appId;
	@Column(name = "auth_method_code")
	private String authMethodCode;
	@Column(name = "process_name")
	private String processName;
	@Column(name = "lang_code")
	private String langCode;

}
