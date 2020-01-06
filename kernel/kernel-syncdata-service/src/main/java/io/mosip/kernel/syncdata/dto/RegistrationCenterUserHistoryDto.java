package io.mosip.kernel.syncdata.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegistrationCenterUserHistoryDto extends BaseDto {

	private String regCntrId;

	private String userId;

	private LocalDateTime effectDateTimes;
}
