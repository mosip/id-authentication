package io.mosip.registration.processor.stages.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.packet.dto.applicantcategory.ApplicantCategory;
import io.mosip.registration.processor.core.packet.dto.applicantcategory.ApplicantTypeDocument;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.exception.ParsingException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;

/**
 * The Class ApplicantDocumentValidation.
 * 
 * M1039285
 */
public class ApplicantDocumentValidation {

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(ApplicantDocumentValidation.class);

	/** The identity iterator. */
	IdentityIteratorUtil identityIterator = new IdentityIteratorUtil();

	/** The utility. */
	Utilities utility;

	/** The applicant type document. */
	ApplicantTypeDocument applicantTypeDocument;

	/** The env. */
	Environment env;

	/** The reg id. */
	String regId;

	/** The Constant AGE_THRESHOLD. */
	private static final String AGE_THRESHOLD = "mosip.kernel.applicant.type.age.limit";

	private static final String DOCUMENT_CATEGORY = "documentCategory";

	/** The Constant TYPE. */
	private static final String TYPE = "type";

	/** The Constant APPLICANTTYPECHILD. */
	private static final String APPLICANTTYPECHILD = "Child";

	/** The Constant APPLICANTTYPEADULT. */
	private static final String APPLICANTTYPEADULT = "Adult";

	/** The demographic identity. */
	JSONObject demographicIdentity = null;

	/**
	 * Instantiates a new applicant document validation.
	 *
	 * @param utilities
	 *            the utilities
	 * @param env
	 *            the env
	 * @param applicantTypeDocument
	 *            the applicant type document
	 */
	public ApplicantDocumentValidation(Utilities utilities, Environment env,
			ApplicantTypeDocument applicantTypeDocument) {
		this.env = env;
		this.utility = utilities;
		this.applicantTypeDocument = applicantTypeDocument;
	}

	/**
	 * Validate document.
	 *
	 * @param registrationId
	 *            the registration id
	 * @param jsonString
	 *            the json string
	 * @return true, if successful
	 * @throws ApisResourceAccessException
	 *             the apis resource access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws io.mosip.kernel.core.exception.IOException 
	 * @throws PacketDecryptionFailureException 
	 * @throws ParseException
	 *             the parse exception
	 * @throws ParseException
	 *             the parse exception
	 * @throws SecurityException
	 *             the security exception
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 */
	public boolean validateDocument(String registrationId, String jsonString)
			throws ApisResourceAccessException, IOException, PacketDecryptionFailureException, io.mosip.kernel.core.exception.IOException {

		boolean isApplicantDocumentVerified = false;
		String applicantType = null;
		regId = registrationId;
		int age = utility.getApplicantAge(regId);
		int ageThreshold = Integer.parseInt(env.getProperty(AGE_THRESHOLD));

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, "ApplicantDocumentValidation::validateApplicantData::entry");
		demographicIdentity = getDemographicJson(jsonString);

		List<String> mapperJsonKeys = new ArrayList<>(demographicIdentity.keySet());

		if (age < ageThreshold)
			applicantType = APPLICANTTYPECHILD;
		else
			applicantType = APPLICANTTYPEADULT;

		isApplicantDocumentVerified = applicantValidation(applicantType, mapperJsonKeys);

		regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, "ApplicantDocumentValidation::validateApplicantData::exit");
		return isApplicantDocumentVerified;
	}

	/**
	 * Applicant validation.
	 *
	 * @param applicantType
	 *            the applicant type
	 * @param list
	 *            the list
	 * @return the boolean
	 * @throws ParseException
	 *             the parse exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Boolean applicantValidation(String applicantType, List<String> list) throws IOException {

		Boolean isApplicantValidated = false;
		String getIdentityJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),
				utility.getGetRegProcessorDocumentCategory());

		JSONParser parser = new JSONParser();

		JSONObject json;
		try {
			json = (JSONObject) parser.parse(getIdentityJsonString);
		} catch (org.json.simple.parser.ParseException e) {
			throw new ParsingException(PlatformErrorMessages.RPR_SYS_JSON_PARSING_EXCEPTION.getCode(), e);
		}
		JSONObject identityJsonObject = (JSONObject) json.get("identity");

		Set<String> documentCategoryList = new HashSet<>();

		Iterator<String> it = list.iterator();

		while (it.hasNext()) {
			String key = it.next().trim();

			JSONObject attributesJsonObject = (JSONObject) identityJsonObject.get(key);
			if (attributesJsonObject != null) {
				String documentCategory = attributesJsonObject.get(DOCUMENT_CATEGORY).toString();

				if (documentCategory != null && !documentCategory.isEmpty())
					documentCategoryList.add(documentCategory);
			}

		}

		Iterator<String> iterator = documentCategoryList.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next().trim();
			if (!applicantTypeDocumentValidation(key, applicantType))
				return false;
			else
				isApplicantValidated = true;

		}

		return isApplicantValidated;
	}

	/**
	 * Type code.
	 *
	 * @param key
	 *            the key
	 * @return the string
	 */
	private String typeCode(String key) {
		JSONObject json = JsonUtil.getJSONObject(demographicIdentity, key);
		return (String) json.get(TYPE);
	}

	/**
	 * Applicant type document validation.
	 *
	 * @param documentCategory
	 *            the document category
	 * @param applicantType
	 *            the applicant type
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private boolean applicantTypeDocumentValidation(String documentCategory, String applicantType) throws IOException {

		String getIdentityJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),
				utility.getGetRegProcessorApplicantType());
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
		ApplicantTypeDocument applicantTypeDocument = mapIdentityJsonStringToObject.readValue(getIdentityJsonString,
				ApplicantTypeDocument.class);
		List<ApplicantCategory> applicanttype = applicantTypeDocument.getApplicantCategory();
		Iterator<ApplicantCategory> it = applicanttype.iterator();

		while (it.hasNext()) {
			ApplicantCategory applicantCategory = it.next();
			if (applicantCategory.getApplicantType().equalsIgnoreCase(applicantType)) {

				return documentTypeValidation(applicantCategory, documentCategory);
			}
		}
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, PlatformErrorMessages.RPR_PVM_APPLICANTDOCUMENT_VALIDATION_FAILED.getMessage() + " "
						+ documentCategory + "and" + applicantType);

		return false;
	}

	/**
	 * Document type validation.
	 *
	 * @param applicantCategory
	 *            the applicant category
	 * @param documentCategory
	 *            the document category
	 * @return true, if successful
	 */
	private boolean documentTypeValidation(ApplicantCategory applicantCategory, String documentCategory) {
		String documentType = typeCode(documentCategory);
		List<io.mosip.registration.processor.core.packet.dto.applicantcategory.DocumentCategory> documentCategoryType = applicantCategory
				.getDocumentCategory();
		Iterator<io.mosip.registration.processor.core.packet.dto.applicantcategory.DocumentCategory> documentIt = documentCategoryType
				.iterator();
		while (documentIt.hasNext()) {
			io.mosip.registration.processor.core.packet.dto.applicantcategory.DocumentCategory documentCategorykey = documentIt
					.next();

			if (documentCategorykey.getKey().equalsIgnoreCase(documentCategory)) {

				List<String> values = documentCategorykey.getValues();
				Iterator<String> valueIt = values.iterator();

				while (valueIt.hasNext()) {
					String value = valueIt.next();
					if (value.equalsIgnoreCase(documentType))
						return true;
				}

			}

		}
		regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				regId, PlatformErrorMessages.RPR_PVM_DOCUMENT_TYPE_INVALID.getMessage() + " " + documentType);

		return false;
	}

	/**
	 * Gets the demographic json.
	 *
	 * @param jsonString
	 *            the json string
	 * @return the demographic json
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private JSONObject getDemographicJson(String jsonString) throws IOException {

		JSONObject demographicJson = JsonUtil.objectMapperReadValue(jsonString, JSONObject.class);
		demographicIdentity = JsonUtil.getJSONObject(demographicJson, utility.getGetRegProcessorDemographicIdentity());

		if (demographicIdentity == null) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					"", PlatformErrorMessages.RPR_PVM_IDJSON_NOT_FOUND.getMessage());
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PVM_IDJSON_NOT_FOUND.getMessage());

		}
		return demographicIdentity;
	}

}
