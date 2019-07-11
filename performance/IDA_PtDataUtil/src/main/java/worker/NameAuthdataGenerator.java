package worker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import pt.dto.auth.demo.Data;
import pt.dto.auth.demo.DemoAuthEntity;
import pt.dto.auth.demo.PersonalIdAuthEntity;
import pt.dto.auth.demo.NameBasedPersonalIdentity;
import pt.dto.auth.demo.PersonalIdentityRequest;
import pt.dto.idRepo.ResponseEntity;
import pt.dto.unencrypted.EncryptionResponse;
import pt.util.JSONUtil;
import pt.util.PropertiesUtil;

public class NameAuthdataGenerator {

	public NameAuthdataGenerator() {

	}

	public void loadJsons() throws FileNotFoundException {

		Util util = new Util();

		String basePath = PropertiesUtil.BASE_PATH;
		String requestsPath = basePath + File.separator + "Generated" + File.separator + "requests";
		String responsesPath = basePath + File.separator + "Generated" + File.separator + "responses";
		String addressAuthRequestsPath = basePath + File.separator + "Generated" + File.separator + "authRequests";
		int index = 0;
		File[] requestJsons = util.getAllFilesInDirectory(requestsPath);
		File[] responseJsons = util.getAllFilesInDirectory(responsesPath);
		for (File file : requestJsons) {
			String requestPath = file.getAbsolutePath();
			System.out.println(requestPath);
			DemoAuthEntity authEntity = JSONUtil.mapExternalJsonToObject(requestPath);
			List<Data> fullName = authEntity.getRequest().getIdentity().getFullName();
			String responsePath = responseJsons[index].getAbsolutePath();
			ResponseEntity response = JSONUtil.mapExternalJsonToObject1(responsePath);
			String entityUrl = response.getResponse().getEntity();
			String[] arr = entityUrl.split("/");
			String idvId = arr[arr.length - 1];

			String separator = "\\\\";
			String[] pathSections = requestPath.split(separator);
			System.out.println(pathSections[pathSections.length - 1]);
			String addressAuthRequestFile = addressAuthRequestsPath + File.separator
					+ pathSections[pathSections.length - 1];
			System.out.println("requestPath: ");
			System.out.println(requestPath);
			try {
				idvId = util.extractVidFromAddressAuthRequest(addressAuthRequestFile);
			} catch (Exception e) {
				e.printStackTrace();
			}

			PersonalIdAuthEntity personalIdAuthEntity = new PersonalIdAuthEntity();
			personalIdAuthEntity.setTspID(PropertiesUtil.TSPID);

			PersonalIdentityRequest identityRequest = new PersonalIdentityRequest();

			NameBasedPersonalIdentity identity = new NameBasedPersonalIdentity();
			identity.setName(fullName);
			identityRequest.setPersonalIdentity(identity);

			personalIdAuthEntity.setIdentityRequest(identityRequest);

			String filename = "authdata" + index + ".json";
			System.out.println("requestPath");
			System.out.println(requestPath);
			// requestPath.replaceAll("\\", "/");
			// System.out.println("requestPath");
			// System.out.println(requestPath);
			String[] rePathSections = requestPath.split("\\\\");
			filename = rePathSections[rePathSections.length - 1];

			EncryptionResponse encResponse = util.encryptPersonalIdentityData(personalIdAuthEntity, filename);
//			String authDataDir = basePath + File.separator + "Generated" + File.separator + "personalIdAuth"
//					+ File.separator + "authData" + File.separator + index;
			String authDataDir = basePath + File.separator + "Generated" + File.separator + "personalIdAuth"
					+ File.separator + "authRequests";
			File f = new File(authDataDir);
			if (!f.exists()) {
				f.mkdirs();
			}
			String authDataFile = authDataDir + File.separator + filename;
			util.generatePersonalIdAuthData(encResponse, authDataFile, idvId);

			if (index == 0)
				break;
			index++;

		}

	}

}
