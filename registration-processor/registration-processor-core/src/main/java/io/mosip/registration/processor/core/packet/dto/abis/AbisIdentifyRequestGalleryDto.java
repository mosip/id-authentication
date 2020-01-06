package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class AbisIdentifyRequestGalleryDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<ReferenceIdDto> referenceIds;

}
