package io.mosip.registration.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import io.mosip.registration.entity.AppAuthenticationMethod;
import lombok.Data;

/**
 * Composite key for {@link AppAuthenticationMethod}
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
@Data
public class AppAuthenticationMethodId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "app_id")
	private String appId;
	@Column(name = "process_id")
	private String processId;
	@Column(name= "role_code")
	private String roleCode;
	@Column(name = "auth_method_code")
	private String authMethodCode;
}
