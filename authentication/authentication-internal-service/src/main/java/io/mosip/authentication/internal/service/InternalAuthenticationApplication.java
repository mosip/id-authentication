package io.mosip.authentication.internal.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
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
import io.mosip.authentication.common.service.impl.AuthTxnServiceImpl;
import io.mosip.authentication.common.service.impl.AuthtypeStatusImpl;
import io.mosip.authentication.common.service.impl.BioAuthServiceImpl;
import io.mosip.authentication.common.service.impl.DemoAuthServiceImpl;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.impl.IdServiceImpl;
import io.mosip.authentication.common.service.impl.OTPAuthServiceImpl;
import io.mosip.authentication.common.service.impl.OTPServiceImpl;
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
import io.mosip.authentication.common.service.interceptor.IdaTransactionInterceptor;
import io.mosip.authentication.common.service.validator.OTPRequestValidator;
import io.mosip.authentication.core.spi.bioauth.util.BioMatcherUtil;
import io.mosip.authentication.internal.service.integration.RestHelperImpl;
import io.mosip.authentication.internal.service.manager.InternalAuthTransactionManager;
import io.mosip.kernel.bioapi.impl.BioApiImpl;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
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
@SpringBootApplication(exclude=HibernateDaoConfig.class)
@Import(value = {UinValidatorImpl.class, VidValidatorImpl.class, IDAMappingConfig.class,
		KeyManager.class, RestHelperImpl.class, RestRequestFactory.class, IdInfoFetcherImpl.class,
		OTPManager.class, MasterDataManager.class, MatchInputBuilder.class, IdRepoManager.class,
		NotificationManager.class, NotificationServiceImpl.class, IdTemplateManager.class,
		TemplateManagerBuilderImpl.class, IdAuthExceptionHandler.class, AuthFacadeImpl.class, OTPAuthServiceImpl.class,
		IdInfoHelper.class, CbeffImpl.class, IdServiceImpl.class, AuditRequestFactory.class, DemoAuthServiceImpl.class,
		BioAuthServiceImpl.class, TokenIdManager.class, SwaggerConfig.class, AuditHelper.class,
		PinAuthServiceImpl.class, PinValidatorImpl.class, BioMatcherUtil.class, BioApiImpl.class,
		IdObjectPatternValidator.class, DemoNormalizerImpl.class, OTPServiceImpl.class, OTPRequestValidator.class,IdaTransactionInterceptor.class,
		InternalAuthTransactionManager.class,AuthTxnServiceImpl.class,AuthtypeStatusImpl.class })
@ComponentScan({ "io.mosip.authentication.internal.service.*", "io.mosip.kernel.auth.adapter.*" })
public class InternalAuthenticationApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(InternalAuthenticationApplication.class, args);
	}

}
