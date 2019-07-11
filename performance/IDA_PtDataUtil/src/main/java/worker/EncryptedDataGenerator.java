package worker;

import java.io.IOException;

import com.google.gson.Gson;

import pt.dto.unencrypted.AddressAuthEntity;
import pt.dto.unencrypted.EncryptionResponse;
import pt.util.HTTPUtil;
import pt.util.YMLUtil;

public class EncryptedDataGenerator {

	public EncryptedDataGenerator() {

	}

	public EncryptionResponse generateEncryptedData() {
		Gson gson = new Gson();
		EncryptionResponse encResponse = null;
		YMLUtil ymlUtil = new YMLUtil();
		AddressAuthEntity entity = null;
		try {
			entity = ymlUtil.loadUnencryptedData();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String url = "http://localhost:8081/identity/identity/encrypt";
		String type = "POST";
		String response = "";
		try {
			response = HTTPUtil.sendHttpRequest(url, gson.toJson(entity), type);
		} catch (IOException e) {
			e.printStackTrace();
		}
		encResponse = gson.fromJson(response, EncryptionResponse.class);
		

		return encResponse;
	}

}
