package io.mosip.authentication.core.dto.indauth;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class KycInfo {
	
	private Map<String, List<IdentityInfoDTO>> identity;
	private String ePrint;
	private String idvId;
}
