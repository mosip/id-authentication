package io.mosip.authentication.service.impl.indauth.service.demo;

import java.util.List;
import java.util.function.Function;

import io.mosip.authentication.service.config.IDAMappingConfig;

public enum IdMapping {

	NAME("name", IDAMappingConfig::getName),;

	private String idname;

	private Function<IDAMappingConfig, List<String>> mappingFunction;

	private IdMapping(String idname, Function<IDAMappingConfig, List<String>> mappingFunction) {
		this.idname = idname;
		this.mappingFunction = mappingFunction;
	}

	public String getIdname() {
		return idname;
	}

	public Function<IDAMappingConfig, List<String>> getMappingFunction() {
		return mappingFunction;
	}

}
