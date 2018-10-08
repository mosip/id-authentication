package io.mosip.registration.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.mosip.registration.dto.ApplicationDto;
import io.mosip.registration.dto.RegistrationDto;
import io.mosip.registration.service.RegistrationService;

@Component
public class ApplicationHelper {
	
	@Autowired
	RegistrationService<String, RegistrationDto> registrationService;
	
	public void  Helper(ApplicationDto applications) {
		int noOfApplications=applications.getApplications().size();
		if(noOfApplications==1) {
			//indivudual
			registrationService.addRegistration(applications.getApplications().get(0));
		}
		else if(applications.getApplications().get(0).getIsPrimary()) {
			//group with family
			if(applications.getApplications().get(0).getAge()>=18) {
				for (RegistrationDto registartion : applications.getApplications()) {
					
					registrationService.addRegistration(registartion);
				}
			}
			else {
				System.out.println("Age criteria not met");
			}
			
		}
		else {
			//group with friends
			
			for (RegistrationDto registartion : applications.getApplications()) {
				registrationService.addRegistration(registartion);
			}
		}
	}

}
