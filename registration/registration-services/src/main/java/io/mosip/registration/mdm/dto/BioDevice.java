package io.mosip.registration.mdm.dto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private String certification;
	private String callbackId;
	private String deviceModel;
	private String deviceMake;
	private String firmWare;
	private String deviceExpiry;
	private String deviceId;
	private int deviceSubId;
	private String deviceProviderName;
	private String deviceProviderId;
	private String timestamp;
	
	private Map<String, Integer>  deviceSubIdMapper = new HashMap<String,Integer>() {
		{

			put("LEFT", 1);
			put("RIGHT", 2);
			put("THUMBS", 3);
		}
	};
	

	private IMosipBioDeviceIntegrator mosipBioDeviceIntegrator;

	public CaptureResponseDto capture() throws RegBaseCheckedException {

		String url = runningUrl + ":" + runningPort + "/" + MosipBioDeviceConstants.CAPTURE_ENDPOINT;
		
		CaptureResponseDto captureResponse = null;

		/* build the request object for capture */
		CaptureRequestDto mosipBioCaptureRequestDto = MdmRequestResponseBuilder.buildMosipBioCaptureRequestDto(this);
		String requestBody=null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			 BufferedWriter bW = new BufferedWriter(new FileWriter(new File("D:\\mosip_stream\\mosipstream.txt")));
			requestBody = mapper.writeValueAsString(mosipBioCaptureRequestDto);
			 CloseableHttpClient client = HttpClients.createDefault();
			   StringEntity requestEntity = new StringEntity(requestBody, ContentType.create("Content-Type", Consts.UTF_8));
			  HttpUriRequest  request = RequestBuilder.create("RCAPTURE").setUri(url).setEntity(requestEntity).build();
			 CloseableHttpResponse response = client.execute(request);
			 InputStream inputStram = response.getEntity().getContent();
			 BufferedReader bR = new BufferedReader(new InputStreamReader(inputStram));
			 String s;
			 StringBuffer responseBuffer = new StringBuffer();
			 while((s=bR.readLine())!=null) {
				 responseBuffer.append(s);
				 bW.write(s);
			 }
			 bR.close();
			 bW.close();
			 captureResponse = mapper.readValue(responseBuffer.toString(), CaptureResponseDto.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return captureResponse;

	}
	
	public InputStream stream() throws IOException {

		String url = runningUrl + ":" + runningPort + "/" + MosipBioDeviceConstants.STREAM_ENDPOINT;

		/* build the request object for capture */
		CaptureRequestDto mosipBioCaptureRequestDto = MdmRequestResponseBuilder.buildMosipBioCaptureRequestDto(this);

		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
		con.setRequestMethod("POST");
		String request = new ObjectMapper().writeValueAsString(mosipBioCaptureRequestDto);

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(request);
		wr.flush();
		wr.close();
		con.setReadTimeout(5000);
		con.connect();
		InputStream urlStream = con.getInputStream();
		
		return urlStream;


	}

	public byte[] forceCapture() {
		return null;

	}
	
	public void buildDeviceSubId(String slapType) {
		setDeviceSubId(deviceSubIdMapper.get(slapType));
	}

	public int deviceStatus() {
		return 0;

	}

}
