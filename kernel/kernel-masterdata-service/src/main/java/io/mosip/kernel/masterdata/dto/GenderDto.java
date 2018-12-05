package io.mosip.kernel.synchandler.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenderDto {
	
	private String genderCode;
	private String genderName;
	private String languageCode;
	private Boolean isActive;

}
