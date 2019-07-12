package io.mosip.registrationProcessor.perf.client;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.gtranslate.Language;
import com.gtranslate.Translator;

import io.mosip.registrationProcessor.perf.service.RegPacketProcessor;
import io.mosip.registrationProcessor.perf.service.TestDataGenerator;
import io.mosip.registrationProcessor.perf.util.PropertiesUtil;

public class Main {

	public static void main(String[] args) {
		String CONFIG_FILE = "config.properties";
		new PropertiesUtil().loadProperties(CONFIG_FILE);
		if (PropertiesUtil.USE_PROXY) {
			System.setProperty("http.proxyHost", "172.22.218.218");
			System.setProperty("http.proxyPort", "8085");
			System.setProperty("https.proxyHost", "172.22.218.218");
			System.setProperty("https.proxyPort", "8085");
		}

		if (args.length != 1) {
			System.out.println("Takes  one parameter");
			System.out.println("test_data: To generate initial test data");
			System.out.println("packet_gen: To generate the packets");
		} else {
			String mode = args[0];
			switch (mode) {
			case "test_data":
				generateTestDataInCSV();
				break;
			case "packet_gen":
				processRegPacket();
				break;
			default:
				System.out.println("takes input as");
				System.out.println("test_data: To generate initial test data");
				System.out.println("packet_gen: To generate the packets");

			}
		}

	}

	private static void generateTestDataInCSV() {
		String csvPath = PropertiesUtil.TEST_DATA_CSV_FILE_PATH;
		TestDataGenerator testDataGenerator = new TestDataGenerator();
		testDataGenerator.generateTestDataInCSV(csvPath);
	}

	private static void processRegPacket() {
		RegPacketProcessor regpacketProcessor = new RegPacketProcessor();
		regpacketProcessor.processValidPacket();
	}

}
