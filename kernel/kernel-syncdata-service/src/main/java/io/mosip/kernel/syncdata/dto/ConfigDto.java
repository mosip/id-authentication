package io.mosip.kernel.syncdata.dto;



import lombok.Data;
import net.minidev.json.JSONObject;

@Data
public class ConfigDto {
	private JSONObject globalConfig;
	private JSONObject registrationCenterConfiguration;
}
