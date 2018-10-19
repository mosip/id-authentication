package io.mosip.registration.processor.core.packet.dto;

import java.util.List;

import lombok.Data;

@Data
public class Document {
	// TODO - Where Enrollment Ack Scanned Copy location in zip?
	private List<DocumentDetail> documentDetails;
	private String registrationAckCopy;
}
