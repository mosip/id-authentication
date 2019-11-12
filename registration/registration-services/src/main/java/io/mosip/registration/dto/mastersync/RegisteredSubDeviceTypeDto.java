package io.mosip.registration.dto.mastersync;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RegisteredSubDeviceTypeDto extends MasterSyncBaseDto {

	private String code;
	private String dtypCode;
	private String name;
	private String descr;
	private Boolean isActive;
	private String crBy;
	private Timestamp crDtime;
	private String updBy;
	private Timestamp updDtimes;
	private Timestamp delDtimes;

}
