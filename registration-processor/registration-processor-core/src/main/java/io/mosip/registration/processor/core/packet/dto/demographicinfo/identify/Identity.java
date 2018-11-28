package io.mosip.registration.processor.core.packet.dto.demographicinfo.identify;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class Identity {
	private IdentityJsonValues name;
	
	private IdentityJsonValues gender;

	private IdentityJsonValues dob;
	
	private IdentityJsonValues pheoniticName;


}
