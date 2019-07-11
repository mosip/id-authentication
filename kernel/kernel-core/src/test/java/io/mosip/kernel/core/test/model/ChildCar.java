package io.mosip.kernel.core.test.model;

//@JsonAutoDetect(fieldVisibility = Visibility.ANY)
/**
 * @author Sidhant Agarwal
 *
 */
public class ChildCar {
	public String companyName;
	public String modelName;

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public ChildCar(String companyName, String modelName) {
		super();
		this.companyName = companyName;
		this.modelName = modelName;
	}

	public ChildCar() {
		this.companyName = " ";
		this.modelName = " ";
	}

}
