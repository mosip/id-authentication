package io.mosip.authentication.kyc.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.cache.MasterDataCache;
import io.mosip.authentication.common.service.cache.PartnerServiceCache;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.config.SwaggerConfig;
import io.mosip.authentication.common.service.controller.PartnerServiceCallbackControllerDelegate;
import io.mosip.authentication.common.service.exception.IdAuthExceptionHandler;
import io.mosip.authentication.common.service.facade.AuthFacadeImpl;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.helper.RestHelperImpl;
import io.mosip.authentication.common.service.helper.WebSubSubscriptionHelper;
import io.mosip.authentication.common.service.impl.AuthtypeStatusImpl;
import io.mosip.authentication.common.service.impl.BioAuthServiceImpl;
import io.mosip.authentication.common.service.impl.DemoAuthServiceImpl;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.impl.IdServiceImpl;
import io.mosip.authentication.common.service.impl.OTPAuthServiceImpl;
import io.mosip.authentication.common.service.impl.PinAuthServiceImpl;
import io.mosip.authentication.common.service.impl.match.DemoNormalizerImpl;
import io.mosip.authentication.common.service.impl.notification.NotificationServiceImpl;
import io.mosip.authentication.common.service.impl.patrner.PartnerServiceImpl;
import io.mosip.authentication.common.service.integration.IdRepoManager;
import io.mosip.authentication.common.service.integration.IdTemplateManager;
import io.mosip.authentication.common.service.integration.KeyManager;
import io.mosip.authentication.common.service.integration.MasterDataManager;
import io.mosip.authentication.common.service.integration.NotificationManager;
import io.mosip.authentication.common.service.integration.OTPManager;
import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.BioMatcherUtil;
import io.mosip.authentication.common.service.validator.AuthRequestValidator;
import io.mosip.authentication.common.service.websub.IdAuthWebSubInitializer;
import io.mosip.authentication.common.service.websub.impl.PartnerServiceEventsSubscriber;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.kernel.biosdk.provider.factory.BioAPIFactory;
import io.mosip.kernel.biosdk.provider.impl.BioProviderImpl_V_0_8;
import io.mosip.kernel.biosdk.provider.impl.BioProviderImpl_V_0_9;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.retry.RetryAspect;
import io.mosip.kernel.core.retry.RetryConfig;
import io.mosip.kernel.core.retry.RetryListenerImpl;
import io.mosip.kernel.core.util.RetryUtil;
import io.mosip.kernel.crypto.jce.core.CryptoCore;
import io.mosip.kernel.cryptomanager.service.impl.CryptomanagerServiceImpl;
import io.mosip.kernel.cryptomanager.util.CryptomanagerUtils;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;
import io.mosip.kernel.idvalidator.uin.impl.UinValidatorImpl;
import io.mosip.kernel.idvalidator.vid.impl.VidValidatorImpl;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keymanager.hsm.impl.KeyStoreImpl;
import io.mosip.kernel.keymanagerservice.helper.KeymanagerDBHelper;
import io.mosip.kernel.keymanagerservice.service.impl.KeymanagerServiceImpl;
import io.mosip.kernel.keymanagerservice.util.KeymanagerUtil;
import io.mosip.kernel.partnercertservice.helper.PartnerCertManagerDBHelper;
import io.mosip.kernel.partnercertservice.service.impl.PartnerCertificateManagerServiceImpl;
import io.mosip.kernel.pinvalidator.impl.PinValidatorImpl;
import io.mosip.kernel.signature.service.impl.SignatureServiceImpl;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;
import io.mosip.kernel.tokenidgenerator.generator.TokenIDGenerator;
import io.mosip.kernel.tokenidgenerator.service.impl.TokenIDGeneratorServiceImpl;
import io.mosip.kernel.zkcryptoservice.service.impl.ZKCryptoManagerServiceImpl;

/**
 * Spring-boot class for ID Authentication Application.
 *
 * @author Dinesh Karuppiah
 */
@SpringBootApplication(exclude = HibernateDaoConfig.class)
@Import(value = { UinValidatorImpl.class, VidValidatorImpl.class, IDAMappingConfig.class, KeyManager.class,
		RestHelperImpl.class, RestRequestFactory.class, IdInfoFetcherImpl.class, OTPManager.class,
		MasterDataManager.class, MatchInputBuilder.class, IdRepoManager.class, NotificationManager.class,
		NotificationServiceImpl.class, IdTemplateManager.class, TemplateManagerBuilderImpl.class,
		IdAuthExceptionHandler.class, AuthFacadeImpl.class, OTPAuthServiceImpl.class, IdInfoHelper.class,
		CbeffImpl.class, IdServiceImpl.class, AuditRequestFactory.class, DemoAuthServiceImpl.class,
		BioAuthServiceImpl.class, TokenIdManager.class, SwaggerConfig.class, AuditHelper.class,
		PinAuthServiceImpl.class, IdAuthExceptionHandler.class, AuthRequestValidator.class, PinValidatorImpl.class,
		BioMatcherUtil.class, BioAPIFactory.class, BioProviderImpl_V_0_8.class, BioProviderImpl_V_0_9.class,
		DemoNormalizerImpl.class, IdAuthSecurityManager.class, AuthtypeStatusImpl.class, CryptoCore.class,
		CryptomanagerServiceImpl.class, KeyGenerator.class, CryptomanagerUtils.class, KeymanagerServiceImpl.class,
		KeymanagerUtil.class, PartnerServiceImpl.class, TokenIDGeneratorServiceImpl.class, TokenIDGenerator.class,
		PartnerServiceManager.class, ZKCryptoManagerServiceImpl.class, SignatureServiceImpl.class, KeyStoreImpl.class,
		KeymanagerDBHelper.class, IdTypeUtil.class, MasterDataCache.class, PartnerServiceCache.class, PartnerCertificateManagerServiceImpl.class, PartnerCertManagerDBHelper.class,
		WebSubSubscriptionHelper.class, PartnerServiceCallbackControllerDelegate.class, IdAuthWebSubInitializer.class,
		PartnerServiceEventsSubscriber.class, RetryConfig.class, RetryUtil.class, RetryListenerImpl.class,
		RetryAspect.class, AuthTransactionHelper.class })
@ComponentScan(basePackages = { "io.mosip.authentication.kyc.service.*", "io.mosip.kernel.core.logger.config" })
@EnableJpaRepositories(basePackages = { "io.mosip.kernel.keymanagerservice.repository.*" })
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