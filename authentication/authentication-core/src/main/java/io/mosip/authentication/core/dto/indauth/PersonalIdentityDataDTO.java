package io.mosip.authentication.core.dto.indauth;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonalIdentityDataDTO {
	
	private Date requestTimeStamp;
	
	private BioDTO bioDTO;
	
	private PinDTO pinDTO;
	
	private DemoDTO demoDTO;
	

}
