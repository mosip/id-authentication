package io.mosip.registration.processor.stages.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.packet.dto.applicantcategory.ApplicantCategory;
import io.mosip.registration.processor.core.packet.dto.applicantcategory.ApplicantTypeDocument;
import io.mosip.registration.processor.core.packet.dto.documetycategory.identity.DocumentCategoryIdentity;
import io.mosip.registration.processor.core.packet.dto.documetycategory.identity.DocumentCategoryValues;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;
import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;

/**
 * The Class ApplicantDocumentValidation.
 * 
 * M1039285
 */
public class ApplicantDocumentValidation {

	/** The identity iterator. */
	IdentityIteratorUtil identityIterator = new IdentityIteratorUtil();

	/** The registration status dto. */
	InternalRegistrationStatusDto registrationStatusDto;

	Utilities utility;

	Environment env;

	DocumentCategoryIdentity document;

	DocumentCategoryValues documentCategoryValues;

	/** The reg id. */
	String regId;

	private static final String AGE_THRESHOLD = "registration.processor.age.threshlold";

	private static final String TYPE = "type";

	private static final String APPLICANTTYPECHILD = "Child";

	private static final String APPLICANTTYPEADULT = "Adult";

	JSONObject demographicIdentity = null;

	/**
	 * Instantiates a new applicant document validation.
	 *
	 * @param registrationStatusDto
	 *            the registration status dto
	 */
	public ApplicantDocumentValidation(InternalRegistrationStatusDto registrationStatusDto, Utilities utilities,
			DocumentCategoryIdentity document, Environment env) {
		this.env = env;
		this.registrationStatusDto = registrationStatusDto;
		this.utility = utilities;
		this.document = document;
	}

	/**
	 * Validate document.
	 *
	 * @param identity
	 *            the identity
	 * @param registrationId
	 *            the registration id
	 * @return true, if successful
	 * @throws IOException
	 * @throws ParseException
	 * @throws ApisResourceAccessException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public boolean validateDocument(String registrationId, String jsonString) throws IOException, ParseException,
			ApisResourceAccessException, NoSuchFieldException, IllegalAccessException {

		boolean isApplicantDocumentVerified = false;
		String applicantType = null;
		int age = utility.getApplicantAge(regId);
		int ageThreshold = Integer.parseInt(env.getProperty(AGE_THRESHOLD));
		regId = registrationId;

		demographicIdentity = getDemographicJson(jsonString);
		List<String> list = new ArrayList<>();
		List<String> mapperJsonKeys = new ArrayList<>(demographicIdentity.keySet());
		for (String key : mapperJsonKeys) {
			list.add(key);
		}

		if (age < ageThreshold)
			applicantType = APPLICANTTYPECHILD;
		else
			applicantType = APPLICANTTYPEADULT;

		isApplicantDocumentVerified = applicantValidation(applicantType, list);

		return isApplicantDocumentVerified;
	}

	private Boolean applicantValidation(String applicantType, List<String> list)
			throws IOException, NoSuchFieldException, IllegalAccessException {

		String getIdentityJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),
				utility.getGetRegProcessorDocumentCategory());
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
		document = mapIdentityJsonStringToObject.readValue(getIdentityJsonString, DocumentCategoryIdentity.class);

		Set<String> documentCategoryList = new HashSet<>();

		Iterator<String> it = list.iterator();

		while (it.hasNext()) {
			String key = it.next().trim();

			io.mosip.registration.processor.core.packet.dto.documetycategory.identity.Identity id = document
					.getIdentity();
			Field f1 = id.getClass().getDeclaredField(key);
			f1.setAccessible(true);
			Object value1 = f1.get(id);

			Field f2 = value1.getClass().getDeclaredField("documentCategory");
			f2.setAccessible(true);
			Object value2 = f2.get(value1);
			if (value2 != null)
				documentCategoryList.add(value2.toString());

		}

		Iterator<String> iterator = documentCategoryList.iterator();
		while (iterator.hasNext()) {
			String key = iterator.next().trim();
			if (!applicantTypeDocumentValidation(key, applicantType))
				return false;

		}

		return true;
	}

	private String typeCode(String key) {
		JSONObject json = JsonUtil.getJSONObject(demographicIdentity, key);
		return (String) json.get(TYPE);
	}

	private boolean applicantTypeDocumentValidation(String documentCategory, String applicantType) throws IOException {
		String documentType = typeCode(documentCategory);

		String getIdentityJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),
				utility.getGetRegProcessorApplicantType());
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();
		ApplicantTypeDocument applicantTypeDocument = mapIdentityJsonStringToObject.readValue(getIdentityJsonString,
				ApplicantTypeDocument.class);
		List<ApplicantCategory> applicanttype = applicantTypeDocument.getApplicantCategory();
		Iterator<ApplicantCategory> it = applicanttype.iterator();

		while (it.hasNext()) {
			ApplicantCategory applicantCategory = it.next();
			if (applicantCategory.getApplicanttype().equalsIgnoreCase(applicantType)) {

				return documentTypeValidation(applicantCategory, documentType);
			}
		}
		return false;
	}

	private boolean documentTypeValidation(ApplicantCategory applicantCategory, String documentType) {
		List<io.mosip.registration.processor.core.packet.dto.applicantcategory.DocumentCategory> documentCategoryType = applicantCategory
				.getDocumentcategory();
		Iterator<io.mosip.registration.processor.core.packet.dto.applicantcategory.DocumentCategory> documentIt = documentCategoryType
				.iterator();
		while (documentIt.hasNext()) {
			io.mosip.registration.processor.core.packet.dto.applicantcategory.DocumentCategory key = documentIt.next();

			if (key.toString() == documentType)
				return true;
		}
		return false;
	}

	private JSONObject getDemographicJson(String jsonString) throws IOException {

		JSONObject demographicJson = JsonUtil.objectMapperReadValue(jsonString, JSONObject.class);
		demographicIdentity = JsonUtil.getJSONObject(demographicJson, utility.getGetRegProcessorDemographicIdentity());

		if (demographicIdentity == null)
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PVM_IDENTITY_NOT_FOUND.getMessage());

		return demographicIdentity;
	}

}
