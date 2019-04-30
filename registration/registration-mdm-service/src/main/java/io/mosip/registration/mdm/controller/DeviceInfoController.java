package io.mosip.registration.mdm.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
public class DeviceInfoController {
	
	@PostMapping(path = "/deviceInfo")
	public String getDeviceInfo() {
		return "Success";
		
	}

}
