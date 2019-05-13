package io.mosip.registration.mdm.integrator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.mdm.dto.DeviceDiscoveryResponsetDto;
import io.mosip.registration.mdm.dto.CaptureResponseDto;
import io.mosip.registration.mdm.restclient.MosipBioDeviceServiceDelagate;
import io.mosip.registration.mdm.util.MdmRequestResponseBuilder;

/**
 * Handles the request and response of bio devices
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Service
public class MosipBioDeviceIntegratorImpl implements IMosipBioDeviceIntegrator {

	@Autowired
	protected MosipBioDeviceServiceDelagate mosipBioDeviceServiceDelagate;

	/* (non-Javadoc)
	 * @see io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#getDeviceInfo(java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	public Object getDeviceInfo(String url, String serviceName, Class<?> responseType) throws RegBaseCheckedException {
		return mosipBioDeviceServiceDelagate.invokeRestService(url, serviceName, null, responseType);

	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#getDeviceDiscovery(java.lang.String, java.lang.String, java.lang.String, java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<DeviceDiscoveryResponsetDto> getDeviceDiscovery(String url, String serviceName, String deviceType,
			Class<?> responseType) throws RegBaseCheckedException {

		return (List<DeviceDiscoveryResponsetDto>) mosipBioDeviceServiceDelagate.invokeRestService(url, serviceName,
				MdmRequestResponseBuilder.buildDeviceDiscoveryRequest(deviceType), Object[].class);

	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#capture(java.lang.String, java.lang.String, java.lang.Object, java.lang.Class)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, byte[]> capture(String url, String serviceName, Object request, Class<?> responseType)
			throws RegBaseCheckedException {
		ObjectMapper mapper = new ObjectMapper();
		String value = "";
		CaptureResponseDto mosipBioCaptureResponseDto=null;

		Map<String, Object> mosipBioCaptureResponseMap = (HashMap<String, Object>) mosipBioDeviceServiceDelagate.invokeRestService(url, serviceName, request, responseType);

		try {
			value = mapper.writeValueAsString(mosipBioCaptureResponseMap);
			mosipBioCaptureResponseDto = mapper.readValue(value, CaptureResponseDto.class);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return MdmRequestResponseBuilder.parseBioCaptureResponse(mosipBioCaptureResponseDto);

	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#getFrame()
	 */
	@Override
	public CaptureResponseDto getFrame() {
		return null;

	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#forceCapture()
	 */
	@Override
	public CaptureResponseDto forceCapture() {
		return null;

	}

	/* (non-Javadoc)
	 * @see io.mosip.registration.mdm.integrator.IMosipBioDeviceIntegrator#responseParsing()
	 */
	@Override
	public CaptureResponseDto responseParsing() {
		return null;

	}
}
