package io.mosip.registration.entity.id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import io.mosip.registration.entity.ScreenAuthorization;
import lombok.Data;

/**
 * Composite key for {@link ScreenAuthorization}
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Embeddable
@Data
public class ScreenAuthorizationId implements Serializable {

	private static final long serialVersionUID = -8699602385381298607L;

	@Column(name = "screen_id")
	private String screenId;
	@Column(name = "role_code")
	private String roleCode;

}
