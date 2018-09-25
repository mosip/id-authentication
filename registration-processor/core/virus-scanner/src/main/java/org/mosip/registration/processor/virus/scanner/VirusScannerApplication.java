package org.mosip.registration.processor.virus.scanner;

import org.mosip.registration.processor.virus.scanner.service.VirusScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VirusScannerApplication implements CommandLineRunner{
	public static void main(String[] args) {
		SpringApplication.run(VirusScannerApplication.class, args);
	}

	@Autowired
	VirusScannerService<Boolean, String> virusScannerService;
	
	/* (non-Javadoc)
	 * @see org.springframework.boot.CommandLineRunner#run(java.lang.String[])
	 */
	@Override
	public void run(String... args) throws Exception {
		boolean flag = virusScannerService.scanFile("C:/Users/M1039303/Desktop/disk/sdc/1001.zip");
		System.out.println(flag);
		flag = virusScannerService.scanFolder("C:/Users/M1039303/Desktop/disk/sdc");
		System.out.println(flag);
	}
}