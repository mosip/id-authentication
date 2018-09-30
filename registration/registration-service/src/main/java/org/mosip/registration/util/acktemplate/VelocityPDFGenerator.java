package org.mosip.registration.util.acktemplate;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.mosip.registration.dto.RegistrationDTO;
import org.mosip.registration.dto.biometric.ExceptionFingerprintDetailsDTO;
import org.mosip.registration.dto.biometric.ExceptionIrisDetailsDTO;
import org.mosip.registration.dto.biometric.FingerprintDetailsDTO;
import org.mosip.registration.dto.demographic.DocumentDetailsDTO;
import org.springframework.stereotype.Component;

/**
 * Generates Velocity Template for the creation of acknowledgement
 * 
 * @author M1044292
 *
 */
@Component
public class VelocityPDFGenerator {

	/**
	 * @param templateFile
	 *            - vm file which is used to generate acknowledgement
	 * @param enrolment
	 *            - EnrolmentDTO to display required fields on the template
	 * @return writer - After mapping all the fields into the template, it is
	 *         written into a StringWriter and returned
	 */
	public Writer generateTemplate(File templateFile, RegistrationDTO registration) {
		// to get the velocity template
		VelocityEngine vel = new VelocityEngine();
		/*
		 * setting the properties such that the template gets loaded in the form of a
		 * file from the specified location
		 */
		vel.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
		vel.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
		vel.setProperty("file.resource.loader.path", templateFile.getParentFile().getAbsolutePath());
		vel.init();
		// getting template using VelocityEngine
		Template template = vel.getTemplate(templateFile.getName());

		VelocityContext velocityContext = new VelocityContext();

		velocityContext.put("RegId", registration.getRegistrationId());

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String currentDate = sdf.format(new Date());

		// map the respective fields with the values in the enrolmentDTO
		velocityContext.put("Date", currentDate);
		velocityContext.put("FullName", registration.getDemographicDTO().getDemoInLocalLang().getFullName());
		velocityContext.put("DOB", registration.getDemographicDTO().getDemoInLocalLang().getDateOfBirth());
		velocityContext.put("Gender", registration.getDemographicDTO().getDemoInLocalLang().getGender());
		velocityContext.put("AddressLine1",
				registration.getDemographicDTO().getDemoInLocalLang().getAddressDTO().getLine1());
		velocityContext.put("AddressLine2",
				registration.getDemographicDTO().getDemoInLocalLang().getAddressDTO().getLine2());
		velocityContext.put("City", registration.getDemographicDTO().getDemoInLocalLang().getAddressDTO().getCity());
		velocityContext.put("State", registration.getDemographicDTO().getDemoInLocalLang().getAddressDTO().getState());
		velocityContext.put("Country",
				registration.getDemographicDTO().getDemoInLocalLang().getAddressDTO().getCountry());
		velocityContext.put("Mobile", registration.getDemographicDTO().getDemoInLocalLang().getMobile());
		velocityContext.put("Email", registration.getDemographicDTO().getDemoInLocalLang().getEmailId());

		String documentsList = "";
		List<DocumentDetailsDTO> documents = registration.getDemographicDTO().getApplicantDocumentDTO()
				.getDocumentDetailsDTO();
		boolean isFirst = true;
		for (DocumentDetailsDTO document : documents) {
			if (isFirst) {
				documentsList = document.getDocumentName();
				isFirst = false;
			} else {
				documentsList += ", " + document.getDocumentName();
			}
		}

		velocityContext.put("Documents", documentsList);
		velocityContext.put("OperatorName", registration.getOsiDataDTO().getOperatorName());

		byte[] imageBytes = registration.getDemographicDTO().getApplicantDocumentDTO().getPhoto();

		String encodedBytes = StringUtils.newStringUtf8(Base64.encodeBase64(imageBytes, false));

		velocityContext.put("imagesource", "data:image/jpg;base64," + encodedBytes);

		// get the quality ranking for fingerprints of the applicant
		HashMap<String, Integer> fingersQuality = getFingerPrintQualityRanking(registration);
		for (Map.Entry<String, Integer> entry : fingersQuality.entrySet()) {
			if (entry.getValue() != 0) {
				// display rank of quality for the captured fingerprints
				velocityContext.put(entry.getKey(), entry.getValue());
			} else {
				// display cross mark for missing fingerprints
				velocityContext.put(entry.getKey(), "&#10008;");
			}
		}

		// get the total count of fingerprints captured and irises captured
		int[] fingersAndIrises = getFingersAndIrisCount(registration);
		velocityContext.put("BiometricsCaptured",
				fingersAndIrises[0] + " fingers and " + fingersAndIrises[1] + " Iris(es)");

		Writer writer = new StringWriter();
		template.merge(velocityContext, writer);
		return writer;
	}

	/**
	 * @param enrolment
	 *            - EnrolmentDTO to get the biometric details
	 * @return int array which gives the count of fingerprints and irises captured
	 */
	private static int[] getFingersAndIrisCount(RegistrationDTO registration) {

		List<ExceptionFingerprintDetailsDTO> exceptionFingers = registration.getBiometricDTO()
				.getApplicantBiometricDTO().getExceptionFingerprintDetailsDTO();

		List<ExceptionIrisDetailsDTO> exceptionIris = registration.getBiometricDTO().getApplicantBiometricDTO()
				.getExceptionIrisDetailsDTO();

		return new int[] { 10 - exceptionFingers.size(), 2 - exceptionIris.size() };
	}

	/**
	 * @param enrolment
	 *            - EnrolmentDTO to get the biometric details
	 * @return hash map which gives the set of fingerprints captured and their
	 *         respective rankings based on quality score
	 */
	@SuppressWarnings("unchecked")
	private static HashMap<String, Integer> getFingerPrintQualityRanking(RegistrationDTO registration) {
		// for storing the fingerprints captured and their respective quality scores
		HashMap<String, Double> fingersQuality = new HashMap<>();

		// list of missing fingers
		List<ExceptionFingerprintDetailsDTO> exceptionFingers = registration.getBiometricDTO()
				.getApplicantBiometricDTO().getExceptionFingerprintDetailsDTO();
		//
		if (exceptionFingers != null) {
			for (ExceptionFingerprintDetailsDTO exceptionFinger : exceptionFingers) {
				fingersQuality.put(exceptionFinger.getMissingFinger(), (double) 0);
			}
		}
		List<FingerprintDetailsDTO> availableFingers = registration.getBiometricDTO().getApplicantBiometricDTO()
				.getFingerprintDetailsDTO();
		for (FingerprintDetailsDTO availableFinger : availableFingers) {
			fingersQuality.put(availableFinger.getFingerType(), (double) availableFinger.getQualityScore());
		}

		Object[] fingerQualitykeys = fingersQuality.entrySet().toArray();
		Arrays.sort(fingerQualitykeys, new Comparator<Object>() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			public int compare(Object fingetPrintQuality1, Object fingetPrintQuality2) {
				return ((Map.Entry<String, Double>) fingetPrintQuality2).getValue()
						.compareTo(((Map.Entry<String, Double>) fingetPrintQuality1).getValue());
			}
		});
		
		
		LinkedHashMap<String, Double> fingersQualitySorted = new LinkedHashMap<>();
		for (Object fingerPrintQualityKey : fingerQualitykeys) {
			String finger = ((Map.Entry<String, Double>) fingerPrintQualityKey).getKey();
			double quality = ((Map.Entry<String, Double>) fingerPrintQualityKey).getValue();
			fingersQualitySorted.put(finger, quality);
		}
		
		
		HashMap<String, Integer> fingersQualityRanking = new HashMap<>();
		int rank = 1;
		double prev = 1.0;
		for (Map.Entry<String, Double> entry : fingersQualitySorted.entrySet()) {
			if (entry.getValue() != 0) {
				if (Double.compare(entry.getValue(), prev) ==  0 || Double.compare(prev, 1.0) == 0) {
					fingersQualityRanking.put(entry.getKey(), rank);
				} else {
					fingersQualityRanking.put(entry.getKey(), ++rank);
				}
				prev = entry.getValue();
			} else {
				fingersQualityRanking.put(entry.getKey(), entry.getValue().intValue());
			}
		}

		return fingersQualityRanking;
	}
}
