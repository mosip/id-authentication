package io.mosip.registration.processor.core.packet.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RegAbisRefDto {

	private String reg_id;
	private String abis_ref_id;
	private Boolean is_active;
	private String cr_by;
	private LocalDateTime cr_dtimes;
	private String upd_by;
	private LocalDateTime upd_dtimes;
	private Boolean is_deleted;
	private LocalDateTime del_dtimes;

}
