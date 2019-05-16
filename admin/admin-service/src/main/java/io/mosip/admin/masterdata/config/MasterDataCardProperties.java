package io.mosip.admin.masterdata.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "mosip.admin.masterdata")
@Data
public class MasterDataCardProperties {

	private String langCode;
	private Map<String, String> card;

}
