package org.mosip.auth.service;

import org.mosip.auth.service.factory.AuditRequestFactory;
import org.mosip.auth.service.factory.RestRequestFactory;
import org.mosip.auth.service.util.AuditUtil;
import org.mosip.auth.service.util.RestUtil;
import org.mosip.kernel.core.dao.config.impl.HibernateDaoConfig;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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
public class IdAuthenticationApplication implements CommandLineRunner {
	
	@Autowired
	AuditRequestFactory auditFactory;
	
	@Autowired
	RestRequestFactory restFactory;
	
	@Autowired
	MosipRollingFileAppender idaRollingFileAppender;

	public static void main(String[] args) {
		SpringApplication.run(IdAuthenticationApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		new AuditUtil(auditFactory, restFactory);
		new RestUtil(idaRollingFileAppender);
		AuditUtil.audit("moduleId", "description");
	}

}
