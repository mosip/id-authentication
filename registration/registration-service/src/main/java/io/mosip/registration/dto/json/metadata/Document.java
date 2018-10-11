package io.mosip.registration.dto.json.metadata;

import java.util.List;

import lombok.Data;

@Data
public class Document {
	private List<DocumentDetails> documentDetails;
	private String registrationAckCopy;
}
		
