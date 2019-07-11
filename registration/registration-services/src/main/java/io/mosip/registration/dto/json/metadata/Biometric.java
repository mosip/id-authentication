package io.mosip.registration.dto.json.metadata;

import lombok.Data;

/**
 * This class contains the attributes to be displayed for Biometric object in
 * PacketMetaInfo JSON.
 * <p>
 * This object contains the biometric details of Applicant
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@Data
public class Biometric {

	private Applicant applicant;

}
