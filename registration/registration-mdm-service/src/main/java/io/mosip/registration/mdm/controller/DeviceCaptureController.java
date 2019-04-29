package io.mosip.registration.mdm.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.mdm.constants.MosipBioDeviceConstants;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.mdm.dto.CaptureResponseData;
import io.mosip.registration.mdm.dto.MosipBioCaptureRequestDto;
import io.mosip.registration.mdm.dto.MosipBioCaptureResponse;
import io.mosip.registration.mdm.dto.MosipBioCaptureResponseDto;
import io.mosip.registration.mdm.dto.MosipBioRequest;

/**
 * @author Taleev Aalam 
 * The Class is the controller for device capture rest call
 */
@RestController
public class DeviceCaptureController {

	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(DeviceCaptureController.class);

	@GetMapping("/capture")
	public MosipBioCaptureResponseDto getCaputuredData(@RequestBody MosipBioCaptureRequestDto requestDto) {

		MosipBioRequest mosipBioRequest = requestDto.getMosipBioRequest().get(0);

		String bioType = mosipBioRequest.getType();

		MosipBioCaptureResponseDto mosipBioCaptureResponseDto = new MosipBioCaptureResponseDto();
		List<MosipBioCaptureResponse> mosipBioCaptureResponses = new ArrayList<MosipBioCaptureResponse>();
		MosipBioCaptureResponse mosipBioCaptureResponse = new MosipBioCaptureResponse();
		CaptureResponseData captureResponseData = new CaptureResponseData();
		
		mosipBioCaptureResponse.setCaptureResponseData(captureResponseData);
		mosipBioCaptureResponses.add(mosipBioCaptureResponse);
		mosipBioCaptureResponseDto.setMosipBioDeviceDataResponses(mosipBioCaptureResponses);

		switch (bioType) {
		case MosipBioDeviceConstants.VALUE_FINGERPRINT + "_" + MosipBioDeviceConstants.VALUE_SINGLE:
			stubImage(captureResponseData, mosipBioRequest, bioType);
			break;
		case MosipBioDeviceConstants.VALUE_FINGERPRINT + "_" + MosipBioDeviceConstants.VALUE_SLAP:
			stubImage(captureResponseData, mosipBioRequest, bioType);
			break;
		case MosipBioDeviceConstants.VALUE_FINGERPRINT + "_" + MosipBioDeviceConstants.VALUE_TOUCHLESS:
			stubImage(captureResponseData, mosipBioRequest, bioType);
			break;
		case MosipBioDeviceConstants.VALUE_FACE:
			stubImage(captureResponseData, mosipBioRequest, bioType);
			break;
		case MosipBioDeviceConstants.VALUE_IRIS + "_"+ MosipBioDeviceConstants.VALUE_SINGLE:
			stubImage(captureResponseData, mosipBioRequest, bioType);
			break;
		case MosipBioDeviceConstants.VALUE_IRIS + "_" + MosipBioDeviceConstants.VALUE_DOUBLE:
			stubImage(captureResponseData, mosipBioRequest, bioType);
			break;
		case MosipBioDeviceConstants.VALUE_VEIN:
			stubImage(captureResponseData, mosipBioRequest, bioType);
			break;
		default:
			break;
		}

		return mosipBioCaptureResponseDto;
	}

	private void stubImage(CaptureResponseData captureResponseData, MosipBioRequest mosipBioRequest,String bioType) {
		
			captureResponseData.setBioSubType("");
			captureResponseData.setBioType("");
			captureResponseData.setBioValue(getCapturedByte(bioType));
			captureResponseData.setDeviceCode("");
			captureResponseData.setDeviceProviderID("");
			captureResponseData.setDeviceServiceVersion("");
			captureResponseData.setQualityScore("90");
			captureResponseData.setRequestedScore(mosipBioRequest.getRequestedScore());
			captureResponseData.setTimestamp("");
			captureResponseData.setTransactionID("");

	}
	
	private byte[] getCapturedByte(String imageType) {
		byte[] scannedBytes=null;
		try {
			BufferedImage bufferedImage = ImageIO.read(this.getClass().getResourceAsStream("/images/"+imageType+".png"));
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, RegistrationConstants.IMAGE_FORMAT, byteArrayOutputStream);

			scannedBytes = byteArrayOutputStream.toByteArray();

		} catch (IOException exceptoin) {
			exceptoin.printStackTrace();
		}
		
		return scannedBytes;

	}

}
