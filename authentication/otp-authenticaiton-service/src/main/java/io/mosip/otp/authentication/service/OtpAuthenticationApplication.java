package io.mosip.otp.authentication.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import io.mosip.authentication.common.config.IDAMappingConfig;
import io.mosip.authentication.common.factory.BiometricProviderFactory;
import io.mosip.authentication.common.factory.RestRequestFactory;
import io.mosip.authentication.common.helper.IdInfoHelper;
import io.mosip.authentication.common.helper.RestHelper;
import io.mosip.authentication.common.impl.indauth.service.IdInfoFetcherImpl;
import io.mosip.authentication.common.impl.notification.service.NotificationServiceImpl;
import io.mosip.authentication.common.integration.IdTemplateManager;
import io.mosip.authentication.common.integration.KeyManager;
import io.mosip.authentication.common.integration.MasterDataManager;
import io.mosip.authentication.common.integration.NotificationManager;
import io.mosip.authentication.common.integration.OTPManager;
import io.mosip.authentication.common.service.exception.IdAuthExceptionHandler;
import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * Spring-boot class for ID Authentication Application.
 *
 * @author Dinesh Karuppiah
 */
@SpringBootApplication
@Import(value = { HibernateDaoConfig.class, UinValidatorImpl.class, VidValidatorImpl.class, IDAMappingConfig.class,
		DecryptorImpl.class, KeyManager.class, RestHelper.class, RestRequestFactory.class, IdInfoFetcherImpl.class,
		BiometricProviderFactory.class, OTPManager.class, IdInfoHelper.class, MasterDataManager.class,
		NotificationManager.class, NotificationServiceImpl.class, IdTemplateManager.class,
		TemplateManagerBuilderImpl.class, IdAuthExceptionHandler.class })
public class OtpAuthenticationApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(OtpAuthenticationApplication.class, args);
	}

}
