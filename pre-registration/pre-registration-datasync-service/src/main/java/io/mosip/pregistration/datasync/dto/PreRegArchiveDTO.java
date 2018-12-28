package io.mosip.pregistration.datasync.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreRegArchiveDTO {
	private String pre_registration_id;
	private String registration_center_id;
	private String appointment_date;
	private String time_slot_from;
	private String time_slot_to;
	private byte[] zipBytes;
	private String fileName;
}
