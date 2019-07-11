package io.mosip.registration.processor.packet.service.dto.demographic;

import java.io.Serializable;

import io.mosip.registration.processor.packet.service.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class used to capture the Demographic details of the Individual
 * 
 * @author Sowmya
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DemographicInfoDTO extends BaseDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -267205388202231900L;
	/** The identity. */
	private Identity identity;

}
