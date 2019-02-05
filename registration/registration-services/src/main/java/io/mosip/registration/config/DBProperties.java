package io.mosip.registration.config;

import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.service.config.GlobalParamService;

/**
 * Custom properties from DB
 * 
 * @author Omsai Eswar M.
 *
 */
@Component
public class DBProperties extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private GlobalParamService globalParamService;

	@PostConstruct
	public void loadProperiesFromDB() {
		Map<String, Object> configs = globalParamService.getGlobalParams();
		for(String key : configs.keySet()) {
			super.setProperty(key, configs.get(key).toString());
		}
	}
}
