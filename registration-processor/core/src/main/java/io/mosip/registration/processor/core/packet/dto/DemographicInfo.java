package io.mosip.registration.processor.core.packet.dto;

public class DemographicInfo {

	private DemoInLocalLang demoInLocalLang;
	private DemoInUserLang demoInUserLang;


	public DemoInLocalLang getDemoInLocalLang() {
		return demoInLocalLang;
	}

	public void setDemoInLocalLang(DemoInLocalLang demoInLocalLang) {
		this.demoInLocalLang = demoInLocalLang;
	}

	public DemoInUserLang getDemoInUserLang() {
		return demoInUserLang;
	}

	public void setDemoInUserLang(DemoInUserLang demoInUserLang) {
		this.demoInUserLang = demoInUserLang;
	}



}