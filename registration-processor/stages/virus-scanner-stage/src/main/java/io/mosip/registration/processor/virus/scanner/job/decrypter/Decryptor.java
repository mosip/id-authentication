package io.mosip.registration.processor.virus.scanner.job.decrypter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.virus.scanner.job.decrypter.constant.PacketDecryptionFailureExceptionConstant;
import io.mosip.registration.processor.virus.scanner.job.decrypter.exception.PacketDecryptionFailureException;
import io.mosip.registration.processor.virus.scanner.job.dto.CryptomanagerRequestDto;
import io.mosip.registration.processor.virus.scanner.job.dto.CryptomanagerResponseDto;

/**
 * Decryptor class for packet decryption.
 *
 * @author Girish Yarru
 */
@Component
public class Decryptor {
	private static io.mosip.kernel.core.logger.spi.Logger regProcLogger = RegProcessorLogger.getLogger(Decryptor.class);

	@Value("${registration.processor.application.id}")
	private String applicationId;

	// @Value("${registration.processor.reference.id}")
	// private String referenceId;

	@Value("${registration.processor.rid.centerid.length}")
	private int centerIdLength;

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	private static final String DECRYPTION_SUCCESS = "Decryption success for RegistrationId : {}";

	/**
	 * This method consumes inputStream of encrypted packet and registrationId as
	 * arguments. Hits the kernel's crypto-manager api passing 'application
	 * id,center id and encrypted inputStream in form of string. gets the
	 * response(Success or Failure) as string if success convert string to
	 * cryptomanager response dto and then get decrypted data and then return
	 * inputStream of decrypted data. if failure convert string to cryptomanager
	 * response dto and then get error code and error response and throw
	 * PacketDecryptionFailureException.
	 * 
	 * @param encryptedPacket
	 * @param registrationId
	 * @return
	 * @throws PacketDecryptionFailureException
	 * @throws ParseException
	 */

	public InputStream decrypt(InputStream encryptedPacket, String registrationId)
			throws PacketDecryptionFailureException {
		InputStream outstream = null;
		try {
			// String centerId = registrationId.substring(0, centerIdLength);
			String encryptedPacketString = IOUtils.toString(encryptedPacket, "UTF-8");
			CryptomanagerRequestDto cryptomanagerRequestDto = new CryptomanagerRequestDto();
			cryptomanagerRequestDto.setApplicationId(applicationId);
			cryptomanagerRequestDto.setData(encryptedPacketString);
			// cryptomanagerRequestDto.setReferenceId(centerId);
			cryptomanagerRequestDto.setReferenceId("1001");
			// setLocal Date Time
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
			if (registrationId.length() > 14) {
				String packetCreatedDateTime = registrationId.substring(registrationId.length() - 14);
				Date date = formatter.parse(packetCreatedDateTime.substring(0, 8) + "T"
						+ packetCreatedDateTime.substring(packetCreatedDateTime.length() - 6));
				LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"));
				cryptomanagerRequestDto.setTimeStamp(ldt);
			} else {
				regProcLogger.error(LoggerFileConstant.SESSIONID.toString(),
						LoggerFileConstant.REGISTRATIONID.toString(), registrationId,
						"Packet DecryptionFailed-Invalid Packet format");

				throw new PacketDecryptionFailureException(
						PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE
								.getErrorCode(),
						"Packet DecryptionFailed-Invalid Packet format");
			}

			CryptomanagerResponseDto cryptomanagerResponseDto = (CryptomanagerResponseDto) restClientService.postApi(
					ApiName.CRYPTOMANAGERDECRYPT, "", "", cryptomanagerRequestDto, CryptomanagerResponseDto.class);
			byte[] decryptedPacket = CryptoUtil.decodeBase64(cryptomanagerResponseDto.getData());
			outstream = new ByteArrayInputStream(decryptedPacket);

		} catch (IOException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "Error Converting encrypted packet inputStream to string");
			throw new PacketDecryptionFailureException(
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorCode(),
					"Error Converting encrypted packet inputStream to string");
		} catch (ApisResourceAccessException e) {
			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "Internal Error Occured ");
			if (e.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException httpClientException = (HttpClientErrorException) e.getCause();

				throw new PacketDecryptionFailureException(
						PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE
								.getErrorCode(),
						httpClientException.getResponseBodyAsString());
			} else if (e.getCause() instanceof HttpServerErrorException) {
				HttpServerErrorException httpServerException = (HttpServerErrorException) e.getCause();

				throw new PacketDecryptionFailureException(
						PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE
								.getErrorCode(),
						httpServerException.getResponseBodyAsString());
			} else {
				throw new PacketDecryptionFailureException(
						PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE
								.getErrorCode(),
						e.getMessage());
			}

		} catch (ParseException e) {

			regProcLogger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId,
					"Packet DecryptionFailed-Invalid PacketFormat : Unable to parse packet date and time");
			throw new PacketDecryptionFailureException(
					PacketDecryptionFailureExceptionConstant.MOSIP_PACKET_DECRYPTION_FAILURE_ERROR_CODE.getErrorCode(),
					"Packet DecryptionFailed-Invalid PacketFormat : Unable to parse packet date and time");
		}
		regProcLogger.info(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
				registrationId, DECRYPTION_SUCCESS);
		return outstream;
	}

}
