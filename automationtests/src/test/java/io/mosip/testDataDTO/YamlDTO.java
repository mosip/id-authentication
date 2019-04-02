package io.mosip.testDataDTO;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YamlDTO {
	
	private Map<String,List<Object>> yamlObject;

	public Map<String, List<Object>> getYamlObject() {
		return yamlObject;
	}

	public void setYamlObject(Map<String, List<Object>> yamlObject) {
		this.yamlObject = yamlObject;
	}
	

}
