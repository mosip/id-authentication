package worker;

import java.io.File;
import java.io.FileNotFoundException;

import com.google.gson.Gson;

import pt.dto.auth.demo.DemoAuthEntity;
import pt.dto.encrypted.AuthEntity;
import pt.dto.idRepo.ResponseEntity;
import pt.dto.unencrypted.EncryptionResponse;
import pt.util.JSONUtil;
import pt.util.PropertiesUtil;

public class FailedRequestWorker {

	public FailedRequestWorker() {
	}

	public void processFailedRequests(int[] fileSuffixArr) {
		Util util = new Util();
		Gson gson = new Gson();
		String responseFileDir = PropertiesUtil.BASE_PATH + File.separator + "Generated" + File.separator + "responses";
		String requestsFileDir = PropertiesUtil.BASE_PATH + File.separator + "Generated" + File.separator + "requests";
		String authRequestFileDir = PropertiesUtil.BASE_PATH + File.separator + "Generated" + File.separator
				+ "authRequests";
		for (int fileSuffix : fileSuffixArr) {
			String filename = "authdata" + fileSuffix + ".json";
			String respFilePath = responseFileDir + File.separator + filename;
			String requestFilePath = requestsFileDir + File.separator + filename;
			String authRequestFilePath = authRequestFileDir + File.separator + filename;
			System.out.println(respFilePath);
			ResponseEntity response = null;
			try {
				response = JSONUtil.mapExternalJsonToObject1(respFilePath);
				System.out.println(gson.toJson(response.getResponse()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			String uin = "";

			// gson.toJson(response);
			if (response.getResponse() == null) {

				try {
					DemoAuthEntity authEntity = JSONUtil.mapExternalJsonToObject(requestFilePath);
					AuthEntity encrAuthEntity = JSONUtil.mapExternalJsonToAddressAuthEntity(authRequestFilePath);
					// uin = encrAuthEntity.getIdvId();
					uin = util.generateUin();
					String registrationId = util.generateRegistrationId();
					authEntity.setRegistrationId(registrationId);
					util.addIdentity(authEntity, respFilePath, uin);
					// TODO Need to set vid instead of uin
					encrAuthEntity.setIdvId(uin);
					JSONUtil.writeJSONToFile(gson.toJson(encrAuthEntity), authRequestFilePath);

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}

}
