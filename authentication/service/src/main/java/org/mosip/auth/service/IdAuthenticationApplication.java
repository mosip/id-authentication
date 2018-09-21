package org.mosip.auth.service;

import org.mosip.kernel.core.dao.config.impl.HibernateDaoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * 
 * Spring-boot class for ID Authentication Application
 * 
 * @author Dinesh Karuppiah
 */

@SpringBootApplication
@Import(value= {HibernateDaoConfig.class})
public class IdAuthenticationApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdAuthenticationApplication.class, args);
	}

}
