package io.mosip.kernel.core.test.model;

/**
 * @author Sidhant Agarwal
 *
 */
public class ChildCar2 {
	public String companyName;
	public String modelName;
	ParentCar2 parentCar = new ParentCar2();

	public ParentCar2 getParentCar() {
		return parentCar;
	}

	public void setParentCar(ParentCar2 parentCar) {
		this.parentCar = parentCar;
	}

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

}
