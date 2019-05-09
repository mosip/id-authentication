package io.mosip.registration.mdm.dto;

import java.util.Map;

import io.mosio.registration.mdm.constants.MosipBioDeviceConstants;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.integrator.MosipBioDeviceIntegrator;
import io.mosip.registration.mdm.util.MdmRequestResponseBuilder;
import lombok.Getter;
import lombok.Setter;

/**
 * Holds the Biometric Device details
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Getter
@Setter
public class BioDevice {

	private String deviceType;
	private String deviceSubType;
	private String deviceModality;
	private int runningPort;
	private String runningUrl;
	private String status;
	private String providerName;
	private String providerId;
	private String serialVersion;
	private String certificationType;
	private String callbackId;
	private String deviceModel;
	private String deviceMake;
	private String firmWare;
	private String deviceExpiry;

	private MosipBioDeviceIntegrator mosipBioDeviceIntegrator;

	public Map<String, byte[]> capture() throws RegBaseCheckedException {

		String url = runningUrl + ":" + runningPort + "/" + MosipBioDeviceConstants.CAPTURE_ENDPOINT;

		/* build the request object for capture */
		MosipBioCaptureRequestDto mosipBioCaptureRequestDto = MdmRequestResponseBuilder
				.buildMosipBioCaptureRequestDto();

		return mosipBioDeviceIntegrator.capture(url, MosipBioDeviceConstants.CAPTURE_SERVICENAME,
				mosipBioCaptureRequestDto, MosipBioCaptureResponseDto.class);

	}

	public byte[] forceCapture() {
		return null;

	}

	public byte[] stream() {
		return null;

	}

	public int deviceStatus() {
		return 0;

	}

}
