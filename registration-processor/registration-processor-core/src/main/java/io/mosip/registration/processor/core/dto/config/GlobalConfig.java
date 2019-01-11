package io.mosip.registration.processor.core.dto.config;


import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class GlobalConfig {
	
private String archivalPolicy;
private String otpTimeOutInMinutes;
private String numberOfWrongAttemptsForOtp;
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
