package io.mosip.registration.service.external.impl;

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
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.SecretKey;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.jsonvalidator.spi.JsonValidator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.security.constants.MosipSecurityMethod;
import io.mosip.kernel.core.security.decryption.MosipDecryptor;
import io.mosip.kernel.core.security.encryption.MosipEncryptor;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.PreRegistrationDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.Identity;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.exception.RegBaseUncheckedException;
import io.mosip.registration.exception.RegistrationExceptionConstants;
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
	private JsonValidator jsonValidator;

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

		RegistrationDTO registrationDTO = getRegistrationDtoContent();
		DocumentDetailsDTO documentDetailsDTO;
		try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(preRegZipFile))) {

			ZipEntry zipEntry;
			BufferedReader bufferedReader = null;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				String fileName = zipEntry.getName();
				if (fileName.endsWith(".json")) {
					bufferedReader = new BufferedReader(new InputStreamReader(zipInputStream));
					parseDemographicJson(bufferedReader, zipEntry);
				} else if (fileName.contains("_")) {
					documentDetailsDTO = new DocumentDetailsDTO();

					switch (fileName.substring(0, fileName.indexOf("_")).toUpperCase()) {
					case RegistrationConstants.POA_DOCUMENT:
						getIdentityDto().setProofOfAddress(documentDetailsDTO);
						attachDocument(documentDetailsDTO, zipInputStream, fileName,
								RegistrationConstants.POA_DOCUMENT);
						break;
					case RegistrationConstants.POI_DOCUMENT:
						getIdentityDto().setProofOfIdentity(documentDetailsDTO);
						attachDocument(documentDetailsDTO, zipInputStream, fileName,
								RegistrationConstants.POI_DOCUMENT);
						break;
					case RegistrationConstants.POR_DOCUMENT:
						getIdentityDto().setProofOfRelationship(documentDetailsDTO);
						attachDocument(documentDetailsDTO, zipInputStream, fileName,
								RegistrationConstants.POR_DOCUMENT);
						break;
					case RegistrationConstants.DOB_DOCUMENT:
						getIdentityDto().setProofOfDateOfBirth(documentDetailsDTO);
						attachDocument(documentDetailsDTO, zipInputStream, fileName,
								RegistrationConstants.DOB_DOCUMENT);
						break;
					}

				}
			}
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		} catch (IOException exception) {
			LOGGER.error("REGISTRATION - PRE_REG_ZIP_HANDLING_SERVICE_IMPL", RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, exception.getMessage());
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), exception.getCause().getMessage());
		} catch (RuntimeException exception) {
			LOGGER.error("REGISTRATION - PRE_REG_ZIP_HANDLING_SERVICE_IMPL - PRE_REGISTRATION_DATA_SYNC_SERVICE_IMPL",
					RegistrationConstants.APPLICATION_NAME, RegistrationConstants.APPLICATION_ID,
					exception.getMessage());
			throw new RegBaseUncheckedException(RegistrationConstants.PACKET_ZIP_CREATION, exception.getMessage());
		}
		return registrationDTO;
	}

	private void attachDocument(DocumentDetailsDTO documentDetailsDTO, ZipInputStream zipInputStream, String fileName,
			String docCatgory) throws IOException {
		documentDetailsDTO.setDocument(IOUtils.toByteArray(zipInputStream));
		documentDetailsDTO.setType(docCatgory);
		documentDetailsDTO.setFormat(fileName.substring(fileName.lastIndexOf(RegistrationConstants.DOT) + 1));
		documentDetailsDTO.setValue(docCatgory.concat("_")
				.concat(fileName.substring(fileName.lastIndexOf("_") + 1, fileName.lastIndexOf("."))));
	}

	/**
	 * This method is used to parse the demographic json and converts it into
	 * RegistrationDto
	 * 
	 * @param zipInputStream
	 * @param zipEntry
	 * @throws IOException
	 * @throws RegBaseCheckedException
	 */
	private void parseDemographicJson(BufferedReader bufferedReader, ZipEntry zipEntry) throws RegBaseCheckedException {

		try {
			String value;
			StringBuilder jsonString = new StringBuilder();
			while ((value = bufferedReader.readLine()) != null) {
				jsonString.append(value);
			}

			if (!StringUtils.isEmpty(jsonString)) {
				/* validate id json schema */
				jsonValidator.validateJson(jsonString.toString(), "mosip-identity-json-schema.json");
				getRegistrationDtoContent().getDemographicDTO().setDemographicInfoDTO(
						new ObjectMapper().readValue(jsonString.toString(), DemographicInfoDTO.class));
			}
		} catch (IOException exception) {
			LOGGER.error("REGISTRATION - PRE_REG_ZIP_HANDLING_SERVICE_IMPL", RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, exception.getMessage());
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), exception.getCause().getMessage());
		} catch (JsonValidationProcessingException | JsonIOException | JsonSchemaIOException
				| FileIOException jsonValidationException) {
			LOGGER.error("REGISTRATION - PRE_REG_ZIP_HANDLING_SERVICE_IMPL", RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID,
					RegistrationExceptionConstants.REG_PACKET_JSON_VALIDATOR_ERROR_CODE.getErrorMessage()
							+ jsonValidationException.getMessage());
			throw new RegBaseCheckedException(
					RegistrationExceptionConstants.REG_PACKET_JSON_VALIDATOR_ERROR_CODE.getErrorCode(),
					RegistrationExceptionConstants.REG_PACKET_JSON_VALIDATOR_ERROR_CODE.getErrorMessage(),
					jsonValidationException);
		}

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

		LOGGER.info(LOG_PKT_STORAGE, APPLICATION_NAME, APPLICATION_ID, "Pre Registration packet Encrypted");

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
			String filePath = preRegPacketLocation.concat(separator).concat(PreRegistrationId)
					.concat(ZIP_FILE_EXTENSION);
			// Storing the Encrypted Registration Packet as zip
			FileUtils.copyToFile(new ByteArrayInputStream(encryptedPacket), new File(filePath));

			LOGGER.info(LOG_PKT_STORAGE, APPLICATION_NAME, APPLICATION_ID, "Pre Registration Encrypted packet saved");

			return filePath;
		} catch (io.mosip.kernel.core.exception.IOException exception) {
			LOGGER.error("REGISTRATION - PRE_REG_ZIP_HANDLING_SERVICE_IMPL", RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, exception.getMessage());
			throw new RegBaseCheckedException(REG_IO_EXCEPTION.getErrorCode(), REG_IO_EXCEPTION.getErrorMessage());
		} catch (RuntimeException runtimeException) {
			LOGGER.error("REGISTRATION - PRE_REG_ZIP_HANDLING_SERVICE_IMPL", RegistrationConstants.APPLICATION_NAME,
					RegistrationConstants.APPLICATION_ID, runtimeException.getMessage());
			throw new RegBaseUncheckedException(RegistrationConstants.ENCRYPTED_PACKET_STORAGE,
					runtimeException.toString());
		}
	}

	@Override
	public byte[] decryptPreRegPacket(String symmetricKey, byte[] encryptedPacket) {

		return MosipDecryptor.symmetricDecrypt(Base64.getDecoder().decode(symmetricKey), encryptedPacket,
				MosipSecurityMethod.AES_WITH_CBC_AND_PKCS7PADDING);
	}

	private RegistrationDTO getRegistrationDtoContent() {
		return (RegistrationDTO) SessionContext.getInstance().getMapObject()
				.get(RegistrationConstants.REGISTRATION_DATA);
	}

	private Identity getIdentityDto() {
		return getRegistrationDtoContent().getDemographicDTO().getDemographicInfoDTO().getIdentity();
	}

}
