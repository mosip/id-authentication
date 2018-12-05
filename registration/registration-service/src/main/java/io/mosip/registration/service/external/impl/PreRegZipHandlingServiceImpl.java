package io.mosip.registration.service.external.impl;

import static io.mosip.registration.exception.RegistrationExceptionConstants.REG_IO_EXCEPTION;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.AddressDTO;
import io.mosip.registration.dto.demographic.ApplicantDocumentDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.LocationDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.service.external.PreRegZipHandlingService;

/**
 * This class is used to handle the pre-registration packet zip files
 * 
 * @author balamurugan ramamoorthy
 * @since 1.0.0
 *
 */
@Service
public class PreRegZipHandlingServiceImpl implements PreRegZipHandlingService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.external.impl.PreRegZipHandlingService#
	 * extractPreRegZipFile(byte[])
	 */
	@Override
	public RegistrationDTO extractPreRegZipFile(byte[] preREgZipFile) throws RegBaseCheckedException {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		DemographicDTO demographicDTO = new DemographicDTO();
		registrationDTO.setDemographicDTO(demographicDTO);
		ApplicantDocumentDTO applicantDocumentDTO = new ApplicantDocumentDTO();
		List<DocumentDetailsDTO> documentDetailsDTOs = new ArrayList<>();
		DocumentDetailsDTO documentDetailsDTO;
		try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(preREgZipFile))) {

			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				if (zipEntry.getName().endsWith(".json")) {
					registrationDTO = parseDemographicJson(zipInputStream, zipEntry, registrationDTO);
				} else {

					documentDetailsDTO = new DocumentDetailsDTO();
					documentDetailsDTO.setDocumentName(zipEntry.getName());
					documentDetailsDTO.setDocument(IOUtils.toByteArray(zipInputStream));

					documentDetailsDTOs.add(documentDetailsDTO);
					System.out.println(zipEntry.getName());
				}
			}

			if (!documentDetailsDTOs.isEmpty()) {
				applicantDocumentDTO.setDocumentDetailsDTO(documentDetailsDTOs);
				if (registrationDTO.getDemographicDTO() != null) {
					registrationDTO.getDemographicDTO().setApplicantDocumentDTO(applicantDocumentDTO);
				}
			}
		} catch (IOException exception) {
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), exception.getCause().getMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_ZIP_CREATION, runtimeException.toString());
		}
		return registrationDTO;
	}

	/**
	 * This method is used to parse the demographic json and converts it into
	 * RegistrationDto
	 * 
	 * @param zipInputStream
	 * @param zipEntry
	 * @return RegistrationDTO
	 * @throws IOException
	 * @throws RegBaseCheckedException
	 */
	private static RegistrationDTO parseDemographicJson(ZipInputStream zipInputStream, ZipEntry zipEntry,
			RegistrationDTO registrationDTO) throws RegBaseCheckedException {
		DemographicInfoDTO demographicInfoDTO = new DemographicInfoDTO();
		LocationDTO locationDTO = new LocationDTO();
		AddressDTO addressDTO = new AddressDTO();
		OSIDataDTO osiDataDTO = new OSIDataDTO();

		addressDTO.setLocationDTO(locationDTO);
		demographicInfoDTO.setAddressDTO(addressDTO);
		registrationDTO.getDemographicDTO().setDemoInUserLang(demographicInfoDTO);
		registrationDTO.setOsiDataDTO(osiDataDTO);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipInputStream));
		try {
			String value;
			while ((value = bufferedReader.readLine()) != null) {

				JSONObject jsonObject = new JSONObject(value);

				JSONObject demographicContentJson = jsonObject.getJSONObject("demographic-details")
						.getJSONObject("identity");
				System.out.println(demographicContentJson);
				Iterator<String> demographicFields = demographicContentJson.keys();
				JSONObject fieldContentObject;
				String fieldValue = null;
				while (demographicFields.hasNext()) {
					String fieldNameKey = (String) demographicFields.next();
					JSONArray demographicValues = demographicContentJson.getJSONArray(fieldNameKey);

					if (demographicValues.length() > 0) {
						fieldContentObject = demographicValues.getJSONObject(0);
						fieldValue = (String) fieldContentObject.get("value");
					}
					switch (fieldNameKey) {
					case "gender":
						demographicInfoDTO.setGender(fieldValue);
						break;
					case "city":
						locationDTO.setCity(fieldValue);
						break;
					case "mobileNumber":
						demographicInfoDTO.setMobile(fieldValue);
						break;
					case "localAdministrativeAuthority":
						demographicInfoDTO.setLocalAdministrativeAuthority(fieldValue);
						break;
					case "dateOfBirth":
						try {
							demographicInfoDTO.setDateOfBirth(new SimpleDateFormat("dd/MM/yyyy").parse(fieldValue));
						} catch (ParseException e) {
						}
						break;
					case "emailId":
						demographicInfoDTO.setEmailId(fieldValue);
						break;
					case "province":
						locationDTO.setProvince(fieldValue);
						break;
					case "postalcode":
						locationDTO.setPostalCode(fieldValue);
						break;
					case "FullName":
						demographicInfoDTO.setFullName(fieldValue);
						break;
					case "addressLine1":
						addressDTO.setAddressLine1(fieldValue);
						break;
					case "addressLine2":
						addressDTO.setAddressLine2(fieldValue);
						break;
					case "addressLine3":
						addressDTO.setLine3(fieldValue);
						break;
					case "region":
						locationDTO.setRegion(fieldValue);
						break;
					case "CNEOrPINNumber":
						demographicInfoDTO.setCneOrPINNumber(fieldValue);
						break;
					case "age":
						demographicInfoDTO.setAge(fieldValue);
						break;

					default:
						break;
					}
				}

			}
		} catch (JSONException | IOException exception) {
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), exception.getCause().getMessage());
		}
		return registrationDTO;
	}
}
