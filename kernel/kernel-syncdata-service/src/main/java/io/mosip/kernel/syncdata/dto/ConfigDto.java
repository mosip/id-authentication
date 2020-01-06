package io.mosip.kernel.syncdata.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import net.minidev.json.JSONObject;

@Data
public class ConfigDto {
	@JsonIgnore
	private JSONObject globalConfig;
	@JsonIgnore
	private JSONObject registrationCenterConfiguration;

	private String lastSyncTime;

	private JSONObject configDetail;
}
