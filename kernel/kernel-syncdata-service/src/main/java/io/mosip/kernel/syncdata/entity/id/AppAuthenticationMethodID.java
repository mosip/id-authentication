package io.mosip.kernel.syncdata.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class AppAuthenticationMethodID.
 * 
 * @author Srinivasan
 * @since 1.0.0
 */

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AppAuthenticationMethodID implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4980191138685997576L;

	@Column(name = "app_id", length = 36, nullable = false)
	private String appId;

	@Column(name = "process_Id", length = 36, nullable = false)
	private String processId;

	@Column(name = "role_code", length = 36, nullable = false)
	private String roleCode;

	@Column(name = "auth_method_code", length = 36, nullable = false)
	private String authMethodCode;

}
