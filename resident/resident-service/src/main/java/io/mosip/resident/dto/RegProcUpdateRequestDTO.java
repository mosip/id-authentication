package io.mosip.resident.dto;

import lombok.Data;

@Data
public class RegProcUpdateRequestDTO {
	private String idValue;
	private String idType;
	private String centerId;
	private String machineId;
	private String identityJson;
	private String proofOfAddress;
	private String proofOfIdentity;
	private String proofOfRelationship;
	private String proofOfDateOfBirth;
}
