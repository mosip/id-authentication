package org.mosip.registration.processor.dto.json.metadata;

import java.util.List;

import lombok.Data;

@Data
public class Document
{
	// TODO - Where Enrollment Ack Scanned Copy location in zip?    
    private List<DocumentDetails> documentDetails;
    private String enrollmentAckCopy;
}
		
