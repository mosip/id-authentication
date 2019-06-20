package io.mosip.authentication.kyc.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.config.SwaggerConfig;
import io.mosip.authentication.common.service.exception.IdAuthExceptionHandler;
import io.mosip.authentication.common.service.facade.AuthFacadeImpl;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.helper.RestHelperImpl;
import io.mosip.authentication.common.service.impl.BioAuthServiceImpl;
import io.mosip.authentication.common.service.impl.DemoAuthServiceImpl;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.impl.IdServiceImpl;
import io.mosip.authentication.common.service.impl.OTPAuthServiceImpl;
import io.mosip.authentication.common.service.impl.PinAuthServiceImpl;
import io.mosip.authentication.common.service.impl.match.DemoNormalizerImpl;
import io.mosip.authentication.common.service.impl.notification.NotificationServiceImpl;
import io.mosip.authentication.common.service.integration.IdRepoManager;
import io.mosip.authentication.common.service.integration.IdTemplateManager;
import io.mosip.authentication.common.service.integration.KeyManager;
import io.mosip.authentication.common.service.integration.MasterDataManager;
import io.mosip.authentication.common.service.integration.NotificationManager;
import io.mosip.authentication.common.service.integration.OTPManager;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.core.spi.bioauth.util.BioMatcherUtil;
import io.mosip.kernel.bioapi.impl.BioApiImpl;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.crypto.jce.impl.DecryptorImpl;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;
import io.mosip.kernel.idobjectvalidator.impl.IdObjectPatternValidator;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.pinvalidator.impl.PinValidatorImpl;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * Spring-boot class for ID Authentication Application.
 *
 * @author Dinesh Karuppiah
 */
@SpringBootApplication
@Import(value = { HibernateDaoConfig.class, UinValidatorImpl.class, VidValidatorImpl.class, IDAMappingConfig.class,
		DecryptorImpl.class, KeyManager.class, RestHelperImpl.class, RestRequestFactory.class, IdInfoFetcherImpl.class,
		OTPManager.class, MasterDataManager.class, MatchInputBuilder.class, IdRepoManager.class,
		NotificationManager.class, NotificationServiceImpl.class, IdTemplateManager.class,
		TemplateManagerBuilderImpl.class, IdAuthExceptionHandler.class, AuthFacadeImpl.class, OTPAuthServiceImpl.class,
		IdInfoHelper.class, CbeffImpl.class, IdServiceImpl.class, AuditRequestFactory.class, DemoAuthServiceImpl.class,
		BioAuthServiceImpl.class, TokenIdManager.class, SwaggerConfig.class, AuditHelper.class,
		PinAuthServiceImpl.class, IdAuthExceptionHandler.class, AuthRequestValidator.class, PinValidatorImpl.class,IdObjectPatternValidator.class,
		BioMatcherUtil.class, BioApiImpl.class,DemoNormalizerImpl.class })
public class KycAuthenticationApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(KycAuthenticationApplication.class, args);
	}

}
