package io.mosip.kernel.syncdata.entity.id;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.Data;

/**
 * Class ScreenAuthorizationID.
 * 
 * @author Srinivasan
 * @since 1.0.0
 */
@Data
public class ScreenAuthorizationID implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4782612793281742441L;

	@Column(name = "screen_id", length = 36, nullable = false)
	private String screenId;

	@Column(name = "role_code", length = 36, nullable = false)
	private String roleCode;

}
