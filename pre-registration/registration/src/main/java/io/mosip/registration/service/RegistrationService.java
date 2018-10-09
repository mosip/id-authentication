package io.mosip.registration.service;

import org.springframework.stereotype.Service;

import io.mosip.registration.dto.ResponseDto;


@Service
public interface RegistrationService<T, U> {

	public U getRegistration(T userId);

	public ResponseDto addRegistration(U registrationDto, String Type);

	public void updateRegistration(U registrationDto);

}
