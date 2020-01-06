package io.mosip.preregistration.core.common.dto.identity;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 * @author Jagadishwari S
 * @since 1.0.0
 *
 */

@Data
public class DemographicIdentityRequestDTO implements Serializable {
	/**
	 * constant serialVersion UID
	 */
	private static final long serialVersionUID = -912216321976514052L;

	private Identity identity;
}
