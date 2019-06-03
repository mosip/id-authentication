package io.mosip.registration.mdm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RegistrationMdmServiceApplication {

	public static void main(String[] args) {
		File propsFilePath = new File(System.getProperty("user.dir") + "/props/mosip-application.properties");
		FileInputStream fileInputStream = null;
		Properties properties = new Properties();
		try {
			fileInputStream = new FileInputStream(propsFilePath);
			properties.load(fileInputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String port = properties.getProperty("mosip.server.port");

		SpringApplication app = new SpringApplication(RegistrationMdmServiceApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", port));
		app.run(args);
	}

}
