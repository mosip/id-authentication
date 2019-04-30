package io.mosip.registration.processor.status.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class RegistrationStatusSubRequestDto implements Serializable {

	
	private static final long serialVersionUID = 8796818679130367545L;
	

	private String registrationId;


}
