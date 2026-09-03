package io.mosip.authentication.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.mosip.kernel.cryptomanager.controller.CryptomanagerController;
import io.mosip.kernel.keymanagerservice.controller.KeymanagerController;
import io.mosip.kernel.partnercertservice.controller.PartnerCertManagerController;
import io.mosip.kernel.signature.controller.SignatureController;

/**
 * These kernel controllers were only ever deployed via the removed
 * authentication-internal-service (context-path ".../internal"), so their
 * endpoints must stay under "/internal" here too, matching the 1.3.1-rc.1
 * gateway rules and api-test-rig config. Their source lives in the kernel
 * jar, so the prefix is added here instead of via @RequestMapping.
 */
@Configuration
public class InternalKernelControllerPathConfig implements WebMvcConfigurer {

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		configurer.addPathPrefix("/internal", HandlerTypePredicate.forBasePackageClass(KeymanagerController.class)
				.or(HandlerTypePredicate.forBasePackageClass(SignatureController.class))
				.or(HandlerTypePredicate.forBasePackageClass(CryptomanagerController.class))
				.or(HandlerTypePredicate.forBasePackageClass(PartnerCertManagerController.class)));
	}
}
