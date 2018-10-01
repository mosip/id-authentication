package org.mosip.registration.dto.json.enrollmentmeta;

import lombok.Data;

@Data
public class EnrollmentMetaDataInfo {
	private String approverId;
	private String packetStatus;
	private String machineId;
	private String approvedDate;
	private String approverName;
	private String comments;
}
