package io.mosip.kernel.idgenerator.tsp.constant;

public enum TspIdPropertyConstant {
	
	ID_START_VALUE("1000");

	private String property;

	public String getProperty() {
		return property;
	}

	

	TspIdPropertyConstant(String property){
		this.property=property;
	}
	
	
}
