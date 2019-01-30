package io.mosip.registration.processor.core.packet.dto.demographicinfo;
	
import java.util.Arrays;

import lombok.Data;

/**
 * Instantiates a new demographic info json.
 */
@Data
public class DemographicInfoJson {

/** The reg id. */
private String regId;

/** The pre reg id. */
private String preRegId;

/** The status code. */
private String statusCode;

/** The lang code. */
private String langCode;

public byte[] getDemographicDetails() {
	return Arrays.copyOf(demographicDetails, demographicDetails.length);
}

public void setDemographicDetails(byte[] demographicDetails) {
	this.demographicDetails = demographicDetails!=null?demographicDetails:null;
}

/** The demographic details. */
private byte[] demographicDetails;
}
