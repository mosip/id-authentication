package io.mosip.registration.service.external.impl;

import static io.mosip.kernel.core.util.DateUtils.formatDate;
import static io.mosip.registration.constants.LoggerConstants.LOG_PKT_STORAGE;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;
import static io.mosip.registration.constants.RegistrationConstants.ZIP_FILE_EXTENSION;
import static io.mosip.registration.exception.RegistrationExceptionConstants.REG_IO_EXCEPTION;
import static java.io.File.separator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.SecretKey;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.decryption.MosipDecryptor;
import io.mosip.kernel.core.security.encryption.MosipEncryptor;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.dto.OSIDataDTO;
import io.mosip.registration.dto.PreRegistrationDTO;
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

	@Value("${PRE_REG_PACKET_LOCATION}")
	private String preRegPacketLocation;

	@Value("${packet.location.dateFormat}")
	private String preRegLocationDateFormat;

	@Autowired
	private KeyGenerator keyGenerator;

	private static final Logger LOGGER = AppConfig.getLogger(PreRegZipHandlingServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.service.external.impl.PreRegZipHandlingService#
	 * extractPreRegZipFile(byte[])
	 */
	@Override
	public RegistrationDTO extractPreRegZipFile(byte[] preRegZipFile) throws RegBaseCheckedException {
		RegistrationDTO registrationDTO = new RegistrationDTO();
		DemographicDTO demographicDTO = new DemographicDTO();
		registrationDTO.setDemographicDTO(demographicDTO);
		ApplicantDocumentDTO applicantDocumentDTO = new ApplicantDocumentDTO();
		List<DocumentDetailsDTO> documentDetailsDTOs = new ArrayList<>();
		DocumentDetailsDTO documentDetailsDTO;
		try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(preRegZipFile))) {

			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				if (zipEntry.getName().endsWith(".json")) {
					registrationDTO = parseDemographicJson(zipInputStream, zipEntry, registrationDTO);
				} else {

					documentDetailsDTO = new DocumentDetailsDTO();
					documentDetailsDTO.setDocumentName(zipEntry.getName());
					documentDetailsDTO.setDocument(IOUtils.toByteArray(zipInputStream));

					documentDetailsDTOs.add(documentDetailsDTO);
					//System.out.println(zipEntry.getName());
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

			}
		} catch (JSONException | IOException exception) {
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), exception.getCause().getMessage());
		}
		return registrationDTO;
	}

	/**
	 * This method is used to encrypt the pre registration packet and save it into
	 * the disk
	 * 
	 * @param PreRegistrationId
	 * @param preRegPacket
	 * @return PreRegistrationDTO
	 * @throws RegBaseCheckedException
	 */
	@Override
	public PreRegistrationDTO encryptAndSavePreRegPacket(String PreRegistrationId, byte[] preRegPacket)
			throws RegBaseCheckedException {

		SecretKey symmetricKey = keyGenerator.getSymmetricKey();

		// Encrypt the Pre reg packet data using AES
		final byte[] encryptedData = MosipEncryptor.symmetricEncrypt(symmetricKey.getEncoded(), preRegPacket,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);

		LOGGER.debug(LOG_PKT_STORAGE, APPLICATION_NAME, APPLICATION_ID, "Pre Registration packet Encrypted");

		String filePath = storePreRegPacketToDisk(PreRegistrationId, encryptedData);

		PreRegistrationDTO preRegistrationDTO = new PreRegistrationDTO();
		preRegistrationDTO.setPacketPath(filePath);
		preRegistrationDTO.setSymmetricKey(Base64.getEncoder().encodeToString(symmetricKey.getEncoded()));
		preRegistrationDTO.setEncryptedPacket(encryptedData);
		preRegistrationDTO.setPreRegId(PreRegistrationId);
		return preRegistrationDTO;

	}

	/**
	 * This method is used to store the encrypted packet into to the configured disk
	 * location
	 * 
	 * @param PreRegistrationId
	 * @param encryptedPacket
	 * @return
	 * @throws RegBaseCheckedException
	 */
	@Override
	public String storePreRegPacketToDisk(String PreRegistrationId, byte[] encryptedPacket)
			throws RegBaseCheckedException {
		try {
			// Generate the file path for storing the Encrypted Packet
			String filePath = preRegPacketLocation + separator + formatDate(new Date(), preRegLocationDateFormat)
					.concat(separator).concat(PreRegistrationId).concat(ZIP_FILE_EXTENSION);
			// Storing the Encrypted Registration Packet as zip
			FileUtils.copyToFile(new ByteArrayInputStream(encryptedPacket), new File(filePath));

			LOGGER.debug(LOG_PKT_STORAGE, APPLICATION_NAME, APPLICATION_ID, "Pre Registration Encrypted packet saved");

			return filePath;
		} catch (io.mosip.kernel.core.exception.IOException e) {
			
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), REG_IO_EXCEPTION.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			throw new RegBaseUncheckedException(RegistrationConstants.ENCRYPTED_PACKET_STORAGE,
					runtimeException.toString());
		}
	}

	@Override
	public byte[] decryptPreRegPacket(String symmetricKey, byte[] encryptedPacket) {

		MosipDecryptor.symmetricDecrypt(Base64.getDecoder().decode(symmetricKey), encryptedPacket,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
		return encryptedPacket;

	}

}
