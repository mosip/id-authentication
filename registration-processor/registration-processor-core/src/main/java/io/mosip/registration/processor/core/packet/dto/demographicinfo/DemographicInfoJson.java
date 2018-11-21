package io.mosip.registration.processor.core.packet.dto.demographicinfo;

import lombok.Data;

@Data
public class DemographicInfoJson {
private String regId;
private String preRegId;
private String statusCode;
private String langCode;
private byte[] demographicDetails;
}
