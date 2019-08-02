package io.mosip.registration.processor.stages.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

/**
 * 
 * @author M1048399 Horteppa
 *
 */
public class MandatoryValidation {

	private static Logger regProcLogger = RegProcessorLogger.getLogger(MandatoryValidation.class);

	/** The adapter. */
	private PacketManager adapter;

	private Utilities utility;

	/** The registration status dto. */
	private InternalRegistrationStatusDto registrationStatusDto;

	public static final String FILE_SEPARATOR = "\\";

	public MandatoryValidation(PacketManager adapter, InternalRegistrationStatusDto registrationStatusDto,
			Utilities utility) {
		this.adapter = adapter;
		this.registrationStatusDto = registrationStatusDto;
		this.utility = utility;
	}

	public boolean mandatoryFieldValidation(String regId) throws IOException, JSONException, PacketDecryptionFailureException, ApisResourceAccessException, io.mosip.kernel.core.exception.IOException {
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"MandatoryValidation::mandatoryFieldValidation()::entry");
		io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.Identity identiy = getMappeedJSONIdentity()
				.getIdentity();
		JSONObject idJsonObj = getDemoIdentity(regId);
		Map<String, Boolean> fieldValidationMap = new HashMap<>();
		fieldValidationMap.put(identiy.getName().getValue(), identiy.getName().getIsMandatory());
		fieldValidationMap.put(identiy.getDob().getValue(), identiy.getDob().getIsMandatory());
		fieldValidationMap.put(identiy.getGender().getValue(), identiy.getGender().getIsMandatory());
		fieldValidationMap.put(identiy.getParentOrGuardianRID().getValue(),
				identiy.getParentOrGuardianRID().getIsMandatory());
		fieldValidationMap.put(identiy.getParentOrGuardianUIN().getValue(),
				identiy.getParentOrGuardianUIN().getIsMandatory());
		fieldValidationMap.put(identiy.getParentOrGuardianName().getValue(),
				identiy.getParentOrGuardianName().getIsMandatory());
		fieldValidationMap.put(identiy.getPoa().getValue(), identiy.getPoa().getIsMandatory());
		fieldValidationMap.put(identiy.getPoi().getValue(), identiy.getPoi().getIsMandatory());
		fieldValidationMap.put(identiy.getPor().getValue(), identiy.getPor().getIsMandatory());
		fieldValidationMap.put(identiy.getPob().getValue(), identiy.getPob().getIsMandatory());
		fieldValidationMap.put(identiy.getIndividualBiometrics().getValue(),
				identiy.getIndividualBiometrics().getIsMandatory());
		fieldValidationMap.put(identiy.getAge().getValue(), identiy.getAge().getIsMandatory());
		fieldValidationMap.put(identiy.getAddressLine1().getValue(), identiy.getAddressLine1().getIsMandatory());
		fieldValidationMap.put(identiy.getAddressLine2().getValue(), identiy.getAddressLine2().getIsMandatory());
		fieldValidationMap.put(identiy.getAddressLine3().getValue(), identiy.getAddressLine3().getIsMandatory());
		fieldValidationMap.put(identiy.getRegion().getValue(), identiy.getRegion().getIsMandatory());
		fieldValidationMap.put(identiy.getProvince().getValue(), identiy.getProvince().getIsMandatory());
		fieldValidationMap.put(identiy.getPostalCode().getValue(), identiy.getPostalCode().getIsMandatory());
		fieldValidationMap.put(identiy.getPhone().getValue(), identiy.getPhone().getIsMandatory());
		fieldValidationMap.put(identiy.getEmail().getValue(), identiy.getEmail().getIsMandatory());
		fieldValidationMap.put(identiy.getLocalAdministrativeAuthority().getValue(),
				identiy.getLocalAdministrativeAuthority().getIsMandatory());
		fieldValidationMap.put(identiy.getIdschemaversion().getValue(), identiy.getIdschemaversion().getIsMandatory());
		fieldValidationMap.put(identiy.getCnienumber().getValue(), identiy.getCnienumber().getIsMandatory());
		fieldValidationMap.put(identiy.getCity().getValue(), identiy.getCity().getIsMandatory());

		List<String> list = fieldValidationMap.entrySet().stream().filter(map -> map.getValue() == Boolean.TRUE)
				.map(map -> map.getKey()).collect(Collectors.toList());

		for (String keyLabel : list) {
			if (JsonUtil.getJSONValue(idJsonObj, keyLabel) == null
					|| checkEmptyString(JsonUtil.getJSONValue(idJsonObj, keyLabel))) {
				registrationStatusDto.setStatusComment(StatusMessage.MANDATORY_FIELD_MISSING);
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), regId,
						PlatformErrorMessages.RPR_PVM_MANDATORY_FIELD_MISSING.getCode(),
						PlatformErrorMessages.RPR_PVM_MANDATORY_FIELD_MISSING.getMessage() + keyLabel);
				return false;
			}
		}
		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(), "",
				"MandatoryValidation::mandatoryFieldValidation()::exit");
		return true;
	}

	private boolean checkEmptyString(Object obj) throws JSONException {
		ArrayList<HashMap> objArray;
		if (obj instanceof String)
			return ((String) obj).trim().isEmpty() ? true : false;
		if (obj instanceof ArrayList) {
			objArray = (ArrayList<HashMap>) obj;
			for (int i = 0; i < objArray.size(); i++) {
				Map jObj = objArray.get(i);
				return jObj.get("value") == null || jObj.get("language") == null;
			}
		}

		return false;
	}

	private RegistrationProcessorIdentity getMappeedJSONIdentity() throws IOException {
		String getIdentityJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),
				utility.getGetRegProcessorIdentityJson());
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
		return mapIdentityJsonStringToObject.readValue(getIdentityJsonString, RegistrationProcessorIdentity.class);
	}

	private JSONObject getDemoIdentity(String registrationId) throws IOException, PacketDecryptionFailureException, ApisResourceAccessException, io.mosip.kernel.core.exception.IOException {
		InputStream documentInfoStream = adapter.getFile(registrationId,
				PacketFiles.DEMOGRAPHIC.name() + FILE_SEPARATOR + PacketFiles.ID.name());

		byte[] bytes = IOUtils.toByteArray(documentInfoStream);
		String demographicJsonString = new String(bytes);
		JSONObject demographicJson = (JSONObject) JsonUtil.objectMapperReadValue(demographicJsonString,
				JSONObject.class);
		JSONObject idJsonObj = JsonUtil.getJSONObject(demographicJson, "identity");
		if (idJsonObj == null)
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());
		return idJsonObj;
	}

}
