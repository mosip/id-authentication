import java.io.FileNotFoundException;

import com.google.gson.Gson;

import pt.util.PropertiesUtil;
import pt.util.SequentialNumberGenerator;
import worker.AddressAuthDataGenerator;
import worker.AddressAuthDataUpdater;
import worker.FailedRequestWorker;
import worker.IdvIdExtractor;
import worker.NameAuthDataUpdater;
import worker.NameAuthdataGenerator;

public class Main {

	private static final String CONFIG_FILE = "variables_config.properties";

//	static Gson gson = new Gson();

	public static void main(String[] args) {

		// new PropertiesUtil().loadProperties_debug();
		Integer number = 100001;
		SequentialNumberGenerator.assignStartIndex(number);
		new PropertiesUtil().loadProperties(CONFIG_FILE);
		// PropertiesUtil.loadProperties();
		//generateAddressAuthData();
		// generateNameAuthRequest();
		 updateGeneratedAuthData();
		 // extractIdvId();
		// System.lineSeparator();
		// handleFailedRequests();

		// new Util().currDD();

//		String jsonFile = "/auth/demo_auth_data.json";
//		DemoAuthEntity aE = JSONUtil.mapJSONToObject(jsonFile);
//		System.out.println(gson.toJson(aE));

	}

	private static void handleFailedRequests() {

		int[] fileSuffix0 = { 855, 1020, 1193, 1609, 3655, 3746, 3815, 4312, 4795 };
		int[] fileSuffix = { 1936, 1967 };
		FailedRequestWorker worker = new FailedRequestWorker();
		worker.processFailedRequests(fileSuffix);

	}

	private static void extractIdvId() {

		IdvIdExtractor vidExtractor = new IdvIdExtractor();
		vidExtractor.extractIdvId();

	}

	private static void updateGeneratedAuthData() {

		AddressAuthDataUpdater addressDataUpdater = new AddressAuthDataUpdater();
		NameAuthDataUpdater nameDataUpdater = new NameAuthDataUpdater();

		try {
			addressDataUpdater.updateAdressAuthData();
			nameDataUpdater.updateNameAuthData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	private static void generateAddressAuthData() {

		AddressAuthDataGenerator d = new AddressAuthDataGenerator();
		try {
			d.processData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void generateNameAuthRequest() {

		NameAuthdataGenerator nGen = new NameAuthdataGenerator();
		try {
			nGen.loadJsons();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
