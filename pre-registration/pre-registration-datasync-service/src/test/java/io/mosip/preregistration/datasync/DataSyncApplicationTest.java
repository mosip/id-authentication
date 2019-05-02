package io.mosip.preregistration.datasync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Data sync Application
 * 
 * @author M1046129 - Jagadishwari
 *
 */
@SpringBootApplication(scanBasePackages = { "io.mosip.preregistration.core.*,io.mosip.preregistration.document.*"
		+ ",io.mosip.preregistration.application.*,io.mosip.preregistration.datasync.*"
		+ ",io.mosip.kernel.emailnotifier.*,io.mosip.kernel.smsnotifier.*,io.mosip.kernel.cryotomanager.*,io.mosip.kernel.auditmanger.*,io.mosip.kernel.idgenerator.*" })
public class DataSyncApplicationTest {

	public static void main(String[] args) {
		SpringApplication.run(DataSyncApplicationTest.class, args);
	}
}
