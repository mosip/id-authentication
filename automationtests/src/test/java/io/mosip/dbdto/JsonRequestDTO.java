package io.mosip.dbdto;

import java.io.Serializable;
import lombok.Data;

@Data
public class JsonRequestDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private String nameLang1;
	
	private String addressLine1Lang1;
	
	private String addressLine2Lang1;
	
	private String addressLine3Lang1;
	
	private String regionLang1;
	
	private String provinceLang1;
	
	private String cityLang2;

	private String nameLang2;

	private String addressLine1Lang2;

	private String addressLine2Lang2;

	private String addressLine3Lang2;

	private String regionLang2;

	private String provinceLang2;

	private String cityLang1;
	
	private String postalCode;
	
	private String phoneNumber;

	public String getNameLang1() {
		return nameLang1;
	}

	public void setNameLang1(String nameLang1) {
		this.nameLang1 = nameLang1;
	}

	public String getAddressLine1Lang1() {
		return addressLine1Lang1;
	}

	public void setAddressLine1Lang1(String addressLine1Lang1) {
		this.addressLine1Lang1 = addressLine1Lang1;
	}

	public String getAddressLine2Lang1() {
		return addressLine2Lang1;
	}

	public void setAddressLine2Lang1(String addressLine2Lang1) {
		this.addressLine2Lang1 = addressLine2Lang1;
	}

	public String getAddressLine3Lang1() {
		return addressLine3Lang1;
	}

	public void setAddressLine3Lang1(String addressLine3Lang1) {
		this.addressLine3Lang1 = addressLine3Lang1;
	}

	public String getRegionLang1() {
		return regionLang1;
	}

	public void setRegionLang1(String regionLang1) {
		this.regionLang1 = regionLang1;
	}

	public String getProvinceLang1() {
		return provinceLang1;
	}

	public void setProvinceLang1(String provinceLang1) {
		this.provinceLang1 = provinceLang1;
	}

	public String getCityLang2() {
		return cityLang2;
	}

	public void setCityLang2(String cityLang2) {
		this.cityLang2 = cityLang2;
	}

	public String getNameLang2() {
		return nameLang2;
	}

	public void setNameLang2(String nameLang2) {
		this.nameLang2 = nameLang2;
	}

	public String getAddressLine1Lang2() {
		return addressLine1Lang2;
	}

	public void setAddressLine1Lang2(String addressLine1Lang2) {
		this.addressLine1Lang2 = addressLine1Lang2;
	}

	public String getAddressLine2Lang2() {
		return addressLine2Lang2;
	}

	public void setAddressLine2Lang2(String addressLine2Lang2) {
		this.addressLine2Lang2 = addressLine2Lang2;
	}

	public String getAddressLine3Lang2() {
		return addressLine3Lang2;
	}

	public void setAddressLine3Lang2(String addressLine3Lang2) {
		this.addressLine3Lang2 = addressLine3Lang2;
	}

	public String getRegionLang2() {
		return regionLang2;
	}

	public void setRegionLang2(String regionLang2) {
		this.regionLang2 = regionLang2;
	}

	public String getProvinceLang2() {
		return provinceLang2;
	}

	public void setProvinceLang2(String provinceLang2) {
		this.provinceLang2 = provinceLang2;
	}

	public String getCityLang1() {
		return cityLang1;
	}

	public void setCityLang1(String cityLang1) {
		this.cityLang1 = cityLang1;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	

}
