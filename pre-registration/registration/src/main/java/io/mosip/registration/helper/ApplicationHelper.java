package io.mosip.registration.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.mosip.registration.code.FormType;
import io.mosip.registration.core.generator.MosipGroupIdGenerator;
import io.mosip.registration.dao.RegistrationDao;
import io.mosip.registration.dto.ApplicationDto;
import io.mosip.registration.dto.RegistrationDto;
import io.mosip.registration.service.RegistrationService;
import io.mosip.registration.dto.ResponseDto;
import io.mosip.registration.exception.PrimaryValidationFailed;

@Component
public class ApplicationHelper {

	@Autowired
	RegistrationService<String, RegistrationDto> registrationService;

	@Autowired
	RegistrationDao dao;

	@Autowired
	private MosipGroupIdGenerator<String> groupIdGenerator;

	public List<ResponseDto> Helper(ApplicationDto applications) {
		boolean isFamily = false;
		int age = 0;
		String groupId = "";
		List<ResponseDto> response = new ArrayList<>();
		int noOfApplications = applications.getApplications().size();
		if (noOfApplications == 1) {
			
			// indivudual
			if(applications.getApplications().get(0).getAge()>=18) {

			if (applications.getApplications().get(0).getGroupId().isEmpty()) {
				groupId = groupIdGenerator.generateGroupId();
			} else {
				groupId = applications.getApplications().get(0).getGroupId();
			}
			response.add(registrationService.addRegistration(applications.getApplications().get(0), groupId));
			}
			else {
				throw new PrimaryValidationFailed("Age criteria doesnot meet");
			}
		} else if (noOfApplications > 1) {
			for (int i = 0; i < noOfApplications; i++) {
				if (applications.getApplications().get(i).getIsPrimary()) {
					isFamily = true;
					age = applications.getApplications().get(i).getAge();
				}
			}
			if (isFamily) {
				if (age >= 18) {
					if (applications.getApplications().get(0).getGroupId().isEmpty()) {
						groupId = groupIdGenerator.generateGroupId();
					} else {
						groupId = applications.getApplications().get(0).getGroupId();
					}
					for (RegistrationDto registartion : applications.getApplications()) {

						response.add(registrationService.addRegistration(registartion, groupId));
					}
				} else {
					throw new PrimaryValidationFailed("Age criteria doesnot meet");
				}
			} else {
				if (applications.getApplications().get(0).getGroupId().isEmpty()) {
					groupId = groupIdGenerator.generateGroupId();
				} else {
					groupId = applications.getApplications().get(0).getGroupId();
				}
				for (RegistrationDto registartion : applications.getApplications()) {
					response.add(registrationService.addRegistration(registartion, groupId));
				}
			}

		}

		return response;
	}

	public void test(String id) {
		dao.findById(id);
	}
}
