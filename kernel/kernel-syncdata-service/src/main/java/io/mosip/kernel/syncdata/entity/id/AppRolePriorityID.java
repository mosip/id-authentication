package io.mosip.kernel.syncdata.entity.id;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Data

/**
 * Instantiates a new app role priority ID.
 */
@NoArgsConstructor

/**
 * Instantiates a new app role priority ID.
 *
 * @param appId     the app id
 * @param processId the process id
 * @param roleCode  the role code
 */
@AllArgsConstructor
/**
 * 
 * @author Srinivasan
 * @since 1.0.0
 *
 */
public class AppRolePriorityID implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -807295672060713346L;

	/** The app id. */
	@Column(name = "app_id", length = 36, nullable = false)
	private String appId;

	/** The process id. */
	@Column(name = "process_Id", length = 36, nullable = false)
	private String processId;

	/** The role code. */
	@Column(name = "role_code", length = 36, nullable = false)
	private String roleCode;

}
