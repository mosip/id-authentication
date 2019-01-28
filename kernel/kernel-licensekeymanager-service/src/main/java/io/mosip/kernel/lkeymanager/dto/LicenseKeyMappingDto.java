package io.mosip.kernel.lkeymanager.dto;

import java.util.List;

import lombok.Data;

@Data
public class LicenseKeyMappingDto {
	private String tspId;
	private String lKey;
	private List<String> permissions;
}
