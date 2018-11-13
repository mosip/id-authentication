package io.mosip.registration.processor.stages.osivalidator;

import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.packet.dto.OsiData;

@Service
public class OSIValidator {

	public boolean isValidOSI(String registrationId) {
		// To do call packet info to get osi data then call each validation

		return false;
	}

	private boolean isValidOperator(OsiData osiData) {

		return false;
	}

	private boolean isValidSupervisor(OsiData osiData) {

		return false;
	}

	private boolean isValidIntroducer(OsiData osiData) {

		return false;
	}
}
