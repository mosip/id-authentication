package io.mosip.registration.processor.stages.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.google.gson.Gson;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.IdJSONConstant;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.packet.dto.masterdata.StatusResponseDto;
import io.mosip.registration.processor.core.packet.dto.regcentermachine.ErrorDTO;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

@Service
public class MasterDataValidation {

	private static Logger regProcLogger = RegProcessorLogger.getLogger(MasterDataValidation.class);

	InternalRegistrationStatusDto registrationStatusDto;
	RegistrationProcessorRestClientService<Object> registrationProcessorRestService;
	Environment env;
	private static final String VALID = "Valid";

	public MasterDataValidation(InternalRegistrationStatusDto registrationStatusDto, Environment env,
			RegistrationProcessorRestClientService<Object> registrationProcessorRestService) {
		this.registrationStatusDto = registrationStatusDto;
		this.env = env;
		this.registrationProcessorRestService = registrationProcessorRestService;
	}

	public Boolean validateMasterData(RegistrationProcessorIdentity regProcessorIdentityJson) {

		String[] elements = env.getProperty("registration.processor.attributes").split(",");
		List<String> list = new ArrayList<>(Arrays.asList(elements));
		Identity identity = regProcessorIdentityJson.getIdentity();
		boolean isValid = false;

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"MasterDataValidation::validateMasterData::entry");

		if (getValue(list, IdJSONConstant.GENDER.toString())
				&& (!validateGenderName(identity.getGender().getValue()))) {
			registrationStatusDto.setStatusComment(StatusMessage.GENDER_NAME_NOT_AVAILABLE);
			return false;
		}

		if (getValue(list, IdJSONConstant.REGION.toString())
				&& (!validateLocationName(identity.getRegion().getValue()))) {
			registrationStatusDto.setStatusComment(StatusMessage.REGION_NOT_AVAILABLE);
			return false;
		}

		if (getValue(list, IdJSONConstant.PROVINCE.toString())
				&& (!validateLocationName(identity.getProvince().getValue()))) {
			registrationStatusDto.setStatusComment(StatusMessage.PROVINCE_NOT_AVAILABLE);
			return false;
		}

		if (getValue(list, IdJSONConstant.CITY.toString()) && (!validateLocationName(identity.getCity().getValue()))) {
			registrationStatusDto.setStatusComment(StatusMessage.CITY_NOT_AVAILABLE);
			return false;
		}

		if (getValue(list, IdJSONConstant.POSTALCODE.toString())
				&& (!validateLocationName(identity.getPostalCode().getValue()))) {
			registrationStatusDto.setStatusComment(StatusMessage.POSTALCODE_NOT_AVAILABLE);
			return false;
		}

		isValid = true;

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"MasterDataValidation::validateMasterData::exit");
		return isValid;

	}

	private Boolean getValue(List<String> list, String value) {
		Iterator<String> it = list.iterator();

		while (it.hasNext()) {
			String dataValue = it.next();
			if (dataValue.equalsIgnoreCase(value))
				return true;
		}
		return false;
	}

	private boolean validateGenderName(String genderName) {
		boolean isValidGender = false;
		StatusResponseDto statusResponseDto;
		if (genderName != null) {
			try {
				List<String> pathsegments = new ArrayList<>();
				pathsegments.add(genderName);

				statusResponseDto = (StatusResponseDto) registrationProcessorRestService.getApi(ApiName.GENDERTYPE,
						pathsegments, "", "", StatusResponseDto.class);

				if (statusResponseDto.getStatus().equalsIgnoreCase(VALID))
					isValidGender = true;
			} catch (ApisResourceAccessException ex) {
				if (ex.getCause() instanceof HttpClientErrorException) {
					HttpClientErrorException httpClientException = (HttpClientErrorException) ex.getCause();
					String result = httpClientException.getResponseBodyAsString();
					Gson gsonObj = new Gson();
					statusResponseDto = gsonObj.fromJson(result, StatusResponseDto.class);
					ErrorDTO error = statusResponseDto.getErrors().get(0);
					isValidGender = false;

					this.registrationStatusDto.setStatusComment(error.getErrorMessage());

				}
			}
		} else {
			isValidGender = true;
		}
		return isValidGender;
	}

	private boolean validateLocationName(String locationName) {
		boolean isValidLocation = false;
		StatusResponseDto statusResponseDto;
		if (locationName != null) {
			try {
				List<String> pathsegments = new ArrayList<>();
				pathsegments.add(locationName);

				statusResponseDto = (StatusResponseDto) registrationProcessorRestService.getApi(ApiName.LOCATION,
						pathsegments, "", "", StatusResponseDto.class);

				if (statusResponseDto.getStatus().equalsIgnoreCase(VALID))
					isValidLocation = true;
			} catch (ApisResourceAccessException ex) {
				if (ex.getCause() instanceof HttpClientErrorException) {
					HttpClientErrorException httpClientException = (HttpClientErrorException) ex.getCause();
					String result = httpClientException.getResponseBodyAsString();
					Gson gsonObj = new Gson();
					statusResponseDto = gsonObj.fromJson(result, StatusResponseDto.class);
					ErrorDTO error = statusResponseDto.getErrors().get(0);
					isValidLocation = false;

					this.registrationStatusDto.setStatusComment(error.getErrorMessage());

				}
			}
		} else {
			isValidLocation = true;
		}
		return isValidLocation;

	}

}