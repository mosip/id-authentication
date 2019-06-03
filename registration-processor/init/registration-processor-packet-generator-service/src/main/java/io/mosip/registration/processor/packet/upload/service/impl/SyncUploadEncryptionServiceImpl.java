package io.mosip.registration.processor.packet.upload.service.impl;

import static io.mosip.kernel.core.util.JsonUtils.javaObjectToJsonString;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.filesystem.manager.FileManager;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.manager.dto.DirectoryPathDto;
import io.mosip.registration.processor.packet.service.dto.PacketGeneratorResDto;
import io.mosip.registration.processor.packet.service.dto.PacketReceiverResponseDTO;
import io.mosip.registration.processor.packet.service.exception.RegBaseCheckedException;
import io.mosip.registration.processor.packet.service.util.encryptor.EncryptorUtil;
import io.mosip.registration.processor.packet.upload.service.SyncUploadEncryptionService;
import io.mosip.registration.processor.status.code.RegistrationStatusCode;
import io.mosip.registration.processor.status.code.SupervisorStatus;
import io.mosip.registration.processor.status.dto.RegistrationSyncRequestDTO;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.sync.response.dto.RegSyncResponseDTO;

/**
 * The Class SyncUploadEncryptionServiceImpl.
 * 
 * @author Rishabh Keshari
 */
@Service
public class SyncUploadEncryptionServiceImpl implements SyncUploadEncryptionService {

	private static final String PACKET_RECEIVED = "PACKET_RECEIVED";

	private static final String SUCCESS = "SUCCESS";

	/** The reg proc logger. */
	private static Logger regProcLogger = RegProcessorLogger.getLogger(SyncUploadEncryptionServiceImpl.class);

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	/** The encryptor util. */
	@Autowired
	EncryptorUtil encryptorUtil;

	/** The center id length. */
	@Value("${mosip.kernel.registrationcenterid.length}")
	private int centerIdLength;

	/** The center id length. */
	@Value("${registration.processor.rid.machineidsubstring}")
	private int machineIdLength;
	/** The gson. */
	Gson gson = new GsonBuilder().create();

	@Autowired
	private Environment env;

	/** The filemanager. */
	@Autowired
	protected FileManager<DirectoryPathDto, InputStream> filemanager;

	private static final String REG_SYNC_SERVICE_ID = "mosip.registration.processor.registration.sync.id";
	private static final String REG_SYNC_APPLICATION_VERSION = "mosip.registration.processor.application.version";
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";
	private static final String SYNCSTATUSCOMMENT = "UIN Reactivation and Deactivation By External Resources";
	private static final String UPLOADSTATUSCOMMENT = "RECEIVED";
	private static final String EXTENSION_OF_FILE=".zip";

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.packet.upload.service.
	 * SyncUploadEncryptionService#uploadUinPacket(java.io.File, java.lang.String,
	 * java.lang.String)
	 */
	public PacketGeneratorResDto uploadUinPacket(String registartionId, String creationTime, String regType,byte[] packetZipBytes)
			throws RegBaseCheckedException {
		PacketGeneratorResDto packerGeneratorResDto = new PacketGeneratorResDto();

		String syncStatus = "";

		InputStream decryptedFileStream = null;
		try {
			
			decryptedFileStream = new ByteArrayInputStream(packetZipBytes);

			byte[] encryptedbyte=encryptorUtil.encryptUinUpdatePacket(decryptedFileStream, registartionId, creationTime);
			ByteArrayResource contentsAsResource = new ByteArrayResource(encryptedbyte) {
		        @Override
		        public String getFilename() {
		            return registartionId+EXTENSION_OF_FILE; 
		        }
		    };

			RegSyncResponseDTO regSyncResponseDTO = packetSync(registartionId, regType, encryptedbyte,creationTime);
			
			if (regSyncResponseDTO != null) {
				List<SyncResponseDto> synList = regSyncResponseDTO.getResponse();
				if (synList != null) {
					SyncResponseDto syncResponseDto = synList.get(0);
					syncStatus = syncResponseDto.getStatus();
				}
			}
			if (SUCCESS.equalsIgnoreCase(syncStatus)) {

				PacketReceiverResponseDTO packetReceiverResponseDTO = null;
				LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
				map.add("file", contentsAsResource);
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.MULTIPART_FORM_DATA);
				HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(
						map, headers);
				String result = (String) restClientService.postApi(ApiName.PACKETRECEIVER, "", "", requestEntity,
						String.class);
				if (result != null) {
					packetReceiverResponseDTO = gson.fromJson(result, PacketReceiverResponseDTO.class);
					String uploadStatus = packetReceiverResponseDTO.getResponse().getStatus();
					packerGeneratorResDto.setRegistrationId(registartionId);
					if (uploadStatus.equalsIgnoreCase(RegistrationStatusCode.PROCESSING.toString())) {
						packerGeneratorResDto.setStatus(uploadStatus);
					} else if(uploadStatus.contains(PACKET_RECEIVED)){
						packerGeneratorResDto.setStatus(SUCCESS);
					}else {
						packerGeneratorResDto.setStatus(uploadStatus);
					}
					packerGeneratorResDto.setMessage("Packet created and uploaded");
					return packerGeneratorResDto;
				}
				
			}

		} catch (FileNotFoundException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registartionId,
					PlatformErrorMessages.RPR_PGS_FILE_NOT_PRESENT.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_FILE_NOT_PRESENT, e);

		} catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registartionId, PlatformErrorMessages.RPR_PGS_INVALID_KEY_ILLEGAL_ARGUMENT.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_INVALID_KEY_ILLEGAL_ARGUMENT, e);
		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registartionId,
					PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_API_RESOURCE_NOT_AVAILABLE, e);
		} catch (ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registartionId, PlatformErrorMessages.RPR_PGS_API_RESOURCE_NOT_AVAILABLE.getMessage()
							+ ExceptionUtils.getStackTrace(e));
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_API_RESOURCE_NOT_AVAILABLE, e);
		} catch (RegBaseCheckedException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registartionId,
					PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, e);
		} catch (Exception e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registartionId,
					PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_REG_BASE_EXCEPTION, e);
		}

		return packerGeneratorResDto;

	}

	/**
	 * Packet sync.
	 *
	 * @param regId
	 *            the reg id
	 * @return the reg sync response DTO
	 * @throws ApisResourceAccessException
	 */
	@SuppressWarnings("unchecked")
	private RegSyncResponseDTO packetSync(String regId, String regType, byte[] enryptedUinZipFile, String creationTime)
			throws ApisResourceAccessException, RegBaseCheckedException {
		RegSyncResponseDTO regSyncResponseDTO = null;
		InputStream inputStream;
		try {
			RegistrationSyncRequestDTO registrationSyncRequestDTO = new RegistrationSyncRequestDTO();
			List<SyncRegistrationDto> syncDtoList = new ArrayList<>();
			SyncRegistrationDto syncDto = new SyncRegistrationDto();
			
			// Calculate HashSequense for the enryptedUinZipFile file
			HMACUtils.update(enryptedUinZipFile);
			String hashSequence = HMACUtils.digestAsPlainText(HMACUtils.updatedHash());
			
			//Prepare RegistrationSyncRequestDTO 
			registrationSyncRequestDTO.setId(env.getProperty(REG_SYNC_SERVICE_ID));
			registrationSyncRequestDTO.setVersion(env.getProperty(REG_SYNC_APPLICATION_VERSION));
			registrationSyncRequestDTO.setRequesttime(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)));
			
			syncDto.setLangCode("eng");
			syncDto.setRegistrationId(regId);
			syncDto.setSyncType(regType);
			syncDto.setPacketHashValue(hashSequence);
			syncDto.setPacketSize(BigInteger.valueOf(enryptedUinZipFile.length));
			syncDto.setSupervisorStatus(SupervisorStatus.APPROVED.toString());
			syncDto.setSupervisorComment(SYNCSTATUSCOMMENT);
			
			syncDtoList.add(syncDto);
			registrationSyncRequestDTO.setRequest(syncDtoList);
			
			String requestObject = encryptorUtil.encrypt(JsonUtils.javaObjectToJsonString(registrationSyncRequestDTO).getBytes(), regId, creationTime);
			
			String centerId = regId.substring(0, centerIdLength);
			String machineId = regId.substring(centerIdLength, machineIdLength);
			String refId = centerId + "_" + machineId;
			
			LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
			headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
			headers.add("Center-Machine-RefId", refId);
			headers.add("timestamp", creationTime);
			
			HttpEntity<Object> requestEntity = new HttpEntity<Object>(javaObjectToJsonString(requestObject), headers);
			String response = (String) restClientService.postApi(ApiName.SYNCSERVICE, "", "", requestEntity, String.class,
					MediaType.APPLICATION_JSON);
			regSyncResponseDTO = new Gson().fromJson(response, RegSyncResponseDTO.class);
		} catch (FileNotFoundException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId,
					PlatformErrorMessages.RPR_PGS_FILE_NOT_PRESENT.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_FILE_NOT_PRESENT, e);

		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					regId, PlatformErrorMessages.RPR_SYS_IO_EXCEPTION.getMessage() + ExceptionUtils.getStackTrace(e));
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_API_RESOURCE_NOT_AVAILABLE, e);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | JsonProcessingException e) {
			throw new RegBaseCheckedException(PlatformErrorMessages.RPR_PGS_INVALID_KEY_ILLEGAL_ARGUMENT, e);
		}  
		return regSyncResponseDTO;
	}

}
