package io.mosip.registration.processor.core.packet.dto.vid;

import java.io.Serializable;
import java.util.List;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestResponseDTO;
import io.mosip.registration.processor.core.idrepo.dto.ErrorDTO;
import lombok.Data;

@Data
public class VidResponseDTO extends BaseRestResponseDTO implements Serializable{
	
	private static final long serialVersionUID = -3604571018699722626L;

	private String str;
	
	private String metadata;
	
	private VidResDTO response;
	
	private List<ErrorDTO> errors;

}
