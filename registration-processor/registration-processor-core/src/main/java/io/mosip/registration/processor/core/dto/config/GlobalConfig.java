package io.mosip.registration.processor.core.dto.config;


import java.util.Arrays;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component

public class GlobalConfig {
	
public String[] getRestrictedNumbers() {
		return Arrays.copyOf(restrictedNumbers,restrictedNumbers.length);
	}
	public void setRestrictedNumbers(String[] restrictedNumbers) {
		this.restrictedNumbers = restrictedNumbers!=null?restrictedNumbers:null;
	}
	public String[] getSupportedLanguages() {
		return Arrays.copyOf(supportedLanguages,supportedLanguages.length);
	}
	public void setSupportedLanguages(String[] supportedLanguages) {
		this.supportedLanguages = supportedLanguages!=null?supportedLanguages:null;
	}
private String archivalPolicy;
private String otpTimeOutInMinutes;
private String numberOfWrongAttemptsForOtp;
public String getArchivalPolicy() {
	return archivalPolicy;
}
public void setArchivalPolicy(String archivalPolicy) {
	this.archivalPolicy = archivalPolicy;
}
public String getOtpTimeOutInMinutes() {
	return otpTimeOutInMinutes;
}
public void setOtpTimeOutInMinutes(String otpTimeOutInMinutes) {
	this.otpTimeOutInMinutes = otpTimeOutInMinutes;
}
public String getNumberOfWrongAttemptsForOtp() {
	return numberOfWrongAttemptsForOtp;
}
public void setNumberOfWrongAttemptsForOtp(String numberOfWrongAttemptsForOtp) {
	this.numberOfWrongAttemptsForOtp = numberOfWrongAttemptsForOtp;
}
public String getAccountFreezeTimeoutInHours() {
	return accountFreezeTimeoutInHours;
}
public void setAccountFreezeTimeoutInHours(String accountFreezeTimeoutInHours) {
	this.accountFreezeTimeoutInHours = accountFreezeTimeoutInHours;
}
public String getUinLength() {
	return uinLength;
}
public void setUinLength(String uinLength) {
	this.uinLength = uinLength;
}
public String getVidLength() {
	return vidLength;
}
public void setVidLength(String vidLength) {
	this.vidLength = vidLength;
}
public String getPridLength() {
	return pridLength;
}
public void setPridLength(String pridLength) {
	this.pridLength = pridLength;
}
public String getTokenIdLength() {
	return tokenIdLength;
}
public void setTokenIdLength(String tokenIdLength) {
	this.tokenIdLength = tokenIdLength;
}
public String getTspIdLength() {
	return tspIdLength;
}
public void setTspIdLength(String tspIdLength) {
	this.tspIdLength = tspIdLength;
}
public String getRegistrationCenterId() {
	return registrationCenterId;
}
public void setRegistrationCenterId(String registrationCenterId) {
	this.registrationCenterId = registrationCenterId;
}
public String getMachineId() {
	return machineId;
}
public void setMachineId(String machineId) {
	this.machineId = machineId;
}
public String getMobilenumberlength() {
	return mobilenumberlength;
}
public void setMobilenumberlength(String mobilenumberlength) {
	this.mobilenumberlength = mobilenumberlength;
}
public String getNotificationtype() {
	return notificationtype;
}
public void setNotificationtype(String notificationtype) {
	this.notificationtype = notificationtype;
}
private String accountFreezeTimeoutInHours;
private String uinLength;
private String vidLength;
private String pridLength;
private String tokenIdLength;
private String tspIdLength;
private String registrationCenterId;
private String machineId;
private String mobilenumberlength;
private String[] restrictedNumbers;
private String[] supportedLanguages;
private String notificationtype;

}
