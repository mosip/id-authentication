package io.mosip.registration.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import io.mosip.registration.entity.AppRolePriority;
import lombok.Data;

/**
 * Composite key for {@link AppRolePriority}
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
@Data
public class AppRolePriorityId implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Column(name = "app_id")
	private String appId;
	@Column(name = "process_id")
	private String processId;
	@Column(name = "role_code")
	private String roleCode;

}
