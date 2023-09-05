package io.mosip.authentication.internal.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.mosip.authentication.common.manager.IdAuthFraudAnalysisEventManager;
import io.mosip.authentication.common.service.builder.MatchInputBuilder;
import io.mosip.authentication.common.service.cache.MasterDataCache;
import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.config.SwaggerConfig;
import io.mosip.authentication.common.service.exception.IdAuthExceptionHandler;
import io.mosip.authentication.common.service.facade.AuthFacadeImpl;
import io.mosip.authentication.common.service.factory.AuditRequestFactory;
import io.mosip.authentication.common.service.factory.RestRequestFactory;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.helper.InternalRestHelperConfig;
import io.mosip.authentication.common.service.helper.WebSubHelper;
import io.mosip.authentication.common.service.impl.AuthContextClazzRefProvider;
import io.mosip.authentication.common.service.impl.AuthTxnServiceImpl;
import io.mosip.authentication.common.service.impl.AuthtypeStatusImpl;
import io.mosip.authentication.common.service.impl.BioAuthServiceImpl;
import io.mosip.authentication.common.service.impl.DemoAuthServiceImpl;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.impl.IdServiceImpl;
import io.mosip.authentication.common.service.impl.KeyBindedTokenAuthServiceImpl;
import io.mosip.authentication.common.service.impl.OTPAuthServiceImpl;
import io.mosip.authentication.common.service.impl.OTPServiceImpl;
import io.mosip.authentication.common.service.impl.hotlist.HotlistServiceImpl;
import io.mosip.authentication.common.service.impl.idevent.CredentialStoreServiceImpl;
import io.mosip.authentication.common.service.impl.idevent.IdChangeEventHandlerServiceImpl;
import io.mosip.authentication.common.service.impl.masterdata.MasterDataCacheUpdateServiceImpl;
import io.mosip.authentication.common.service.impl.notification.NotificationServiceImpl;
import io.mosip.authentication.common.service.impl.patrner.PartnerCACertEventServiceImpl;
import io.mosip.authentication.common.service.impl.patrner.PartnerServiceImpl;
import io.mosip.authentication.common.service.integration.CredentialRequestManager;
import io.mosip.authentication.common.service.integration.DataShareManager;
import io.mosip.authentication.common.service.integration.IdTemplateManager;
import io.mosip.authentication.common.service.integration.KeyManager;
import io.mosip.authentication.common.service.integration.MasterDataManager;
import io.mosip.authentication.common.service.integration.NotificationManager;
import io.mosip.authentication.common.service.integration.OTPManager;
import io.mosip.authentication.common.service.integration.PartnerServiceManager;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.util.BioMatcherUtil;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.IdaRequestResponsConsumerUtil;
import io.mosip.authentication.common.service.util.KeyBindedTokenMatcherUtil;
import io.mosip.authentication.common.service.validator.AuthFiltersValidator;
import io.mosip.authentication.common.service.validator.OTPRequestValidator;
import io.mosip.authentication.common.service.websub.impl.AuthTransactionStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.AuthTypeStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.AuthTypeStatusEventSubscriber;
import io.mosip.authentication.common.service.websub.impl.CredentialStoreStatusEventPublisher;
import io.mosip.authentication.common.service.websub.impl.HotlistEventInitializer;
import io.mosip.authentication.common.service.websub.impl.IdAuthFraudAnalysisEventPublisher;
import io.mosip.authentication.common.service.websub.impl.IdChangeEventsInitializer;
import io.mosip.authentication.common.service.websub.impl.MasterDataUpdateEventInitializer;
import io.mosip.authentication.common.service.websub.impl.PartnerCACertEventInitializer;
import io.mosip.authentication.common.service.websub.impl.PartnerServiceEventsInitializer;
import io.mosip.authentication.core.util.DemoMatcherUtil;
import io.mosip.authentication.core.util.DemoNormalizer;
import io.mosip.authentication.core.util.IdTypeUtil;
import io.mosip.authentication.core.util.IdValidationUtil;
import io.mosip.authentication.internal.service.batch.CredentialStoreJobExecutionListener;
import io.mosip.authentication.internal.service.manager.InternalAuthSecurityManager;
import io.mosip.kernel.biosdk.provider.factory.BioAPIFactory;
import io.mosip.kernel.biosdk.provider.impl.BioProviderImpl_V_0_8;
import io.mosip.kernel.biosdk.provider.impl.BioProviderImpl_V_0_9;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.retry.RetryAspect;
import io.mosip.kernel.core.retry.RetryConfig;
import io.mosip.kernel.core.retry.RetryListenerImpl;
import io.mosip.kernel.core.util.RetryUtil;
import io.mosip.kernel.crypto.jce.core.CryptoCore;
import io.mosip.kernel.cryptomanager.controller.CryptomanagerController;
import io.mosip.kernel.cryptomanager.service.impl.CryptomanagerServiceImpl;
import io.mosip.kernel.cryptomanager.util.CryptomanagerUtils;
import io.mosip.kernel.dataaccess.hibernate.config.HibernateDaoConfig;
import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
import io.mosip.kernel.keymanager.hsm.health.HSMHealthCheck;
import io.mosip.kernel.keymanager.hsm.impl.KeyStoreImpl;
import io.mosip.kernel.keymanagerservice.controller.KeymanagerController;
import io.mosip.kernel.keymanagerservice.entity.CACertificateStore;
import io.mosip.kernel.keymanagerservice.helper.KeymanagerDBHelper;
import io.mosip.kernel.keymanagerservice.helper.PrivateKeyDecryptorHelper;
import io.mosip.kernel.keymanagerservice.helper.SessionKeyDecrytorHelper;
import io.mosip.kernel.keymanagerservice.service.impl.KeymanagerServiceImpl;
import io.mosip.kernel.keymanagerservice.util.KeymanagerUtil;
import io.mosip.kernel.partnercertservice.controller.PartnerCertManagerController;
import io.mosip.kernel.partnercertservice.helper.PartnerCertManagerDBHelper;
import io.mosip.kernel.partnercertservice.service.impl.PartnerCertificateManagerServiceImpl;
import io.mosip.kernel.pinvalidator.impl.PinValidatorImpl;
import io.mosip.kernel.signature.controller.SignatureController;
import io.mosip.kernel.signature.service.impl.SignatureServiceImpl;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;
import io.mosip.kernel.tokenidgenerator.generator.TokenIDGenerator;
import io.mosip.kernel.zkcryptoservice.service.impl.ZKCryptoManagerServiceImpl;

/**
 * Spring-boot class for ID Authentication Application.
 *
 * @author Dinesh Karuppiah
 */
@SpringBootApplication(exclude = { HibernateDaoConfig.class })
@Import(value = { IdValidationUtil.class, IDAMappingConfig.class, KeyBindedTokenAuthServiceImpl.class,
		KeyManager.class, AuthContextClazzRefProvider.class,
		RestRequestFactory.class, IdInfoFetcherImpl.class, OTPManager.class, MasterDataManager.class,
		MasterDataCache.class, MasterDataCacheUpdateServiceImpl.class, MasterDataUpdateEventInitializer.class, MatchInputBuilder.class,
		NotificationManager.class, NotificationServiceImpl.class, IdTemplateManager.class, TemplateManagerBuilderImpl.class,
		IdAuthExceptionHandler.class, AuthFacadeImpl.class, OTPAuthServiceImpl.class, IdInfoHelper.class, CbeffImpl.class,
		IdServiceImpl.class, AuditRequestFactory.class, DemoAuthServiceImpl.class, BioAuthServiceImpl.class, TokenIdManager.class,
		SwaggerConfig.class, AuditHelper.class, PinValidatorImpl.class, BioMatcherUtil.class, BioAPIFactory.class,
		BioProviderImpl_V_0_8.class, BioProviderImpl_V_0_9.class, OTPServiceImpl.class,
		OTPRequestValidator.class, InternalAuthSecurityManager.class, AuthTxnServiceImpl.class, AuthtypeStatusImpl.class,
		CryptoCore.class, PartnerServiceImpl.class, CryptomanagerServiceImpl.class, KeyGenerator.class, CryptomanagerUtils.class,
		KeymanagerServiceImpl.class, KeymanagerUtil.class, IdChangeEventHandlerServiceImpl.class, SignatureServiceImpl.class,
		KeyStoreImpl.class, KeymanagerDBHelper.class, ZKCryptoManagerServiceImpl.class, PartnerServiceManager.class,
		DataShareManager.class, TokenIDGenerator.class, IdTypeUtil.class, WebSubHelper.class,
		PartnerCACertEventServiceImpl.class, PartnerCertificateManagerServiceImpl.class, PartnerCertManagerDBHelper.class, AuthTypeStatusEventSubscriber.class,
		IdChangeEventsInitializer.class, SignatureController.class, CryptomanagerController.class, KeymanagerController.class,
		CACertificateStore.class, PartnerCACertEventInitializer.class, PartnerCertManagerController.class, RetryConfig.class,
		RetryUtil.class, RetryListenerImpl.class, RetryAspect.class, CredentialStoreServiceImpl.class,
		CredentialStoreJobExecutionListener.class, HotlistServiceImpl.class, HotlistEventInitializer.class,
		AuthTransactionHelper.class, CredentialStoreStatusEventPublisher.class, AuthTypeStatusEventPublisher.class,
		AuthTransactionStatusEventPublisher.class, PartnerServiceEventsInitializer.class, CredentialRequestManager.class,
		DemoNormalizer.class, DemoMatcherUtil.class, IdAuthFraudAnalysisEventManager.class,
		IdAuthFraudAnalysisEventPublisher.class, AuthFiltersValidator.class, SessionKeyDecrytorHelper.class, InternalRestHelperConfig.class, IdaRequestResponsConsumerUtil.class,
		io.mosip.kernel.cryptomanager.dto.AuthorizedRolesDTO.class,
		io.mosip.kernel.keymanagerservice.dto.AuthorizedRolesDTO.class,
		io.mosip.kernel.partnercertservice.dto.AuthorizedRolesDTO.class,
		io.mosip.kernel.signature.dto.AuthorizedRolesDTO.class,
		EnvUtil.class, KeyBindedTokenMatcherUtil.class, HSMHealthCheck.class, PrivateKeyDecryptorHelper.class })
@ComponentScan(basePackages = { "io.mosip.authentication.internal.service.*", "${mosip.auth.adapter.impl.basepackage}",
		"io.mosip.kernel.core.logger.config",
		"io.mosip.authentication.common.service.config" }, excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {
				"io.mosip.idrepository.core.config.IdRepoDataSourceConfig.*" }))
@EnableJpaRepositories(basePackages = { "io.mosip.authentication.common.service.repository.*",
		"io.mosip.kernel.keymanagerservice.repository.*" })
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
