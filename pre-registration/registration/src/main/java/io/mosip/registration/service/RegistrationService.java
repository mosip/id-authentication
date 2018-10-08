package io.mosip.registration.service;

import org.springframework.stereotype.Service;


@Service
public interface RegistrationService<T, U> {

	public U getRegistration(T userId);

	public void addRegistration(U registrationDto);

	public void updateRegistration(U registrationDto);

}
