package io.mosip.registration.processor.status.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface RegistrationExternalStatusService<U>{
	public List<U> getStatus(String ids);
}
