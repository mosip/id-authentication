package io.mosip.registration.dto.json.demo;

public class Demographic {
	
	private DemographicInfo demoInLocalLang;
	private DemographicInfo demoInUserLang;

	/**
	 * @return the demoInLocalLang
	 */
	public DemographicInfo getDemoInLocalLang() {
		return demoInLocalLang;
	}

	/**
	 * @param demoInLocalLang
	 *            the demoInLocalLang to set
	 */
	public void setDemoInLocalLang(DemographicInfo demoInLocalLang) {
		this.demoInLocalLang = demoInLocalLang;
	}

	/**
	 * @return the demoInUserLang
	 */
	public DemographicInfo getDemoInUserLang() {
		return demoInUserLang;
	}

	/**
	 * @param demoInUserLang
	 *            the demoInUserLang to set
	 */
	public void setDemoInUserLang(DemographicInfo demoInUserLang) {
		this.demoInUserLang = demoInUserLang;
	}
}
