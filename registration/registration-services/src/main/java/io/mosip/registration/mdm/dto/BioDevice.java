package io.mosip.registration.mdm.dto;

import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.constants.MosipBioDeviceConstants;
import io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator;
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

	private IMosipBioDeviceIntegrator mosipBioDeviceIntegrator;

	public CaptureResponseDto capture() throws RegBaseCheckedException {

		String url = runningUrl + ":" + runningPort + "/" + MosipBioDeviceConstants.CAPTURE_ENDPOINT;

		/* build the request object for capture */
		CaptureRequestDto mosipBioCaptureRequestDto = MdmRequestResponseBuilder.buildMosipBioCaptureRequestDto(this);

		return mosipBioDeviceIntegrator.capture(url, mosipBioCaptureRequestDto, CaptureResponseDto.class);

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
