package io.mosip.registration.mdmservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import io.mosip.registration.mdmservice.dto.DeviceInfo;
import io.mosip.registration.mdmservice.dto.DeviceInfoResponseData;

@Service
public class DeviceInfoService {
	
	public List<DeviceInfoResponseData> getDeviceInfo(){
		
		List<DeviceInfoResponseData> deviceDetailsList = new ArrayList<>();
		prepareDeviceDetailsInfo(deviceDetailsList);
		return deviceDetailsList;
		
	}
	
	private void prepareDeviceDetailsInfo(List<DeviceInfoResponseData> deviceDetailsList) {
		DeviceInfoResponseData deviceInfoResponseData = new DeviceInfoResponseData();
	}
	
	

}
