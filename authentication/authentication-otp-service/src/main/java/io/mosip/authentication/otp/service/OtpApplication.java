package io.mosip.authentication.otp.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.mosip.authentication.common.manager.IdAuthFraudAnalysisEventManager;
import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.cache.MasterDataCache;
import io.mosip.authentication.common.service.cache.MasterDataCacheInitializer;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.config.LangComparatorConfig;
import io.mosip.authentication.common.service.config.OpenApiProperties;
import io.mosip.authentication.common.service.config.SwaggerConfig;
import io.mosip.authentication.common.service.exception.IdAuthExceptionHandler;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.helper.ExternalRestHelperConfig;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.common.service.impl.AuthContextClazzRefProvider;
import io.mosip.authentication.common.service.impl.AuthtypeStatusImpl;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.impl.IdServiceImpl;
import io.mosip.authentication.common.service.impl.KeyBindedTokenAuthServiceImpl;
import io.mosip.authentication.common.service.impl.OTPAuthServiceImpl;
import io.mosip.authentication.common.service.impl.OTPServiceImpl;
import io.mosip.authentication.common.service.impl.hotlist.HotlistServiceImpl;
import io.mosip.authentication.common.service.impl.masterdata.MasterDataCacheUpdateServiceImpl;
import io.mosip.authentication.common.service.impl.notification.NotificationServiceImpl;
import io.mosip.authentication.common.service.impl.patrner.PartnerCACertEventServiceImpl;
import io.mosip.authentication.common.service.impl.patrner.PartnerServiceImpl;
import io.mosip.authentication.common.service.integration.IdTemplateManager;
import io.mosip.authentication.common.service.integration.KeyManager;
import io.mosip.authentication.common.service.integration.MasterDataManager;
import io.mosip.authentication.common.service.integration.NotificationManager;
import io.mosip.authentication.common.service.integration.OTPManager;
import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.IdaRequestResponsConsumerUtil;
import io.mosip.authentication.common.service.util.KeyBindedTokenMatcherUtil;
import io.mosip.authentication.common.service.validator.OTPRequestValidator;
import io.mosip.authentication.common.service.websub.IdAuthWebSubInitializer;
import io.mosip.authentication.common.service.websub.impl.AuthAnonymousEventPublisher;
import io.mosip.authentication.common.service.websub.impl.AuthTransactionStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.IdAuthFraudAnalysisEventPublisher;
import io.mosip.authentication.common.service.websub.impl.MasterDataUpdateEventInitializer;
import io.mosip.authentication.common.service.websub.impl.PartnerCACertEventInitializer;
import io.mosip.authentication.common.service.websub.impl.PartnerServiceEventsInitializer;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.authentication.core.util.IdValidationUtil;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.retry.RetryAspect;
import io.mosip.kernel.core.retry.RetryConfig;
import io.mosip.kernel.core.retry.RetryListenerImpl;
import io.mosip.kernel.core.util.RetryUtil;
import io.mosip.kernel.crypto.jce.core.CryptoCore;
import io.mosip.kernel.cryptomanager.service.impl.CryptomanagerServiceImpl;
import io.mosip.kernel.cryptomanager.util.CryptomanagerUtils;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keymanager.hsm.health.HSMHealthCheck;
import io.mosip.kernel.keymanager.hsm.impl.KeyStoreImpl;
import io.mosip.kernel.keymanagerservice.helper.KeymanagerDBHelper;
import io.mosip.kernel.keymanagerservice.helper.PrivateKeyDecryptorHelper;
import io.mosip.kernel.keymanagerservice.helper.SessionKeyDecrytorHelper;
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
@SpringBootApplication(exclude = { HibernateDaoConfig.class })
@Import(value = {IdValidationUtil.class, IDAMappingConfig.class, KeyBindedTokenAuthServiceImpl.class,
		KeyManager.class, AuthContextClazzRefProvider.class,
		RestRequestFactory.class, IdInfoFetcherImpl.class, OTPManager.class, MasterDataManager.class, MatchInputBuilder.class,
		NotificationManager.class, NotificationServiceImpl.class, IdTemplateManager.class, TemplateManagerBuilderImpl.class,
		IdAuthExceptionHandler.class, OTPAuthServiceImpl.class, IdInfoHelper.class, CbeffImpl.class,
		IdServiceImpl.class, AuditRequestFactory.class, TokenIdManager.class,
		SwaggerConfig.class, AuditHelper.class, IdAuthExceptionHandler.class, PinValidatorImpl.class,
		OTPServiceImpl.class, OTPRequestValidator.class, IdAuthSecurityManager.class, AuthtypeStatusImpl.class, CryptoCore.class,
		PartnerServiceImpl.class, CryptomanagerServiceImpl.class, KeyGenerator.class, CryptomanagerUtils.class,
		KeymanagerServiceImpl.class, KeymanagerUtil.class, TokenIDGeneratorServiceImpl.class, TokenIDGenerator.class,
		PartnerServiceManager.class, SignatureServiceImpl.class, KeyStoreImpl.class, KeymanagerDBHelper.class,
		ZKCryptoManagerServiceImpl.class, IdTypeUtil.class, MasterDataCache.class, MasterDataCacheInitializer.class,
		PartnerCertificateManagerServiceImpl.class, PartnerCertManagerDBHelper.class, WebSubHelper.class,
		PartnerServiceEventsInitializer.class, RetryConfig.class, RetryUtil.class,
		RetryListenerImpl.class, RetryAspect.class, AuthTransactionHelper.class, HotlistServiceImpl.class,
		AuthTransactionStatusEventPublisher.class, MasterDataCacheUpdateServiceImpl.class,
		MasterDataUpdateEventInitializer.class, IdAuthFraudAnalysisEventManager.class, IdAuthFraudAnalysisEventPublisher.class,
		LangComparatorConfig.class, OpenApiProperties.class, SessionKeyDecrytorHelper.class, ExternalRestHelperConfig.class, IdaRequestResponsConsumerUtil.class,
		PartnerCACertEventServiceImpl.class, PartnerCACertEventInitializer.class, 
		IdAuthWebSubInitializer.class, AuthAnonymousEventPublisher.class, EnvUtil.class, KeyBindedTokenMatcherUtil.class, 
		HSMHealthCheck.class, PrivateKeyDecryptorHelper.class })
@ComponentScan(basePackages = { "io.mosip.authentication.otp.service.*",
		"io.mosip.kernel.core.logger.config", "${mosip.auth.adapter.impl.basepackage}" }, excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {
				"io.mosip.idrepository.core.config.IdRepoDataSourceConfig.*" }))
@EnableJpaRepositories(basePackages = { "io.mosip.authentication.common.service.repository.*",
		"io.mosip.kernel.keymanagerservice.repository.*" })
public class OtpApplication {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(OtpApplication.class, args);
	}

}
