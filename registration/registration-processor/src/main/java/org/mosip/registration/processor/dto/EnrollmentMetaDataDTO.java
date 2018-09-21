package org.mosip.registration.processor.dto;

import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import lombok.Data;

@Data
public class EnrollmentMetaDataDTO {
	private String machineId;
	private String packetStatus;
	private String approverName;
	private String approverId;
	private String comments;
	private Date approvedDate;
	private LinkedList<String> hashSequence;
	private Map<String,String> checkSum;
}
