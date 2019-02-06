package io.mosip.registration.processor.packet.uploader.config;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePropertySource;

import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.idvalidator.rid.impl.RidValidatorImpl;
import io.mosip.registration.processor.packet.uploader.archiver.util.PacketArchiver;
import io.mosip.registration.processor.packet.uploader.stage.PacketUploaderStage;
import io.mosip.registration.processor.status.dao.SyncRegistrationDao;


/**
 * @author Mukul Puspam
 *
 */
@Configuration
public class PacketUploaderConfig {
	/**
	 * loads config server values
	 * 
	 * @param env
	 * @return
	 * @throws IOException
	 */
	@Bean
	public PropertySourcesPlaceholderConfigurer getPropertySourcesPlaceholderConfigurer(Environment env) throws IOException {

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
		List<String> applicationNames = getAppNames(env);
		Resource[] appResources = new Resource[applicationNames.size()];

		for (int i = 0; i < applicationNames.size(); i++) {
			String loc = env.getProperty("spring.cloud.config.uri") + "/registration-processor/"
					+ env.getProperty("spring.profiles.active") + "/" + env.getProperty("spring.cloud.config.label")
					+ "/" + applicationNames.get(i) + "-" + env.getProperty("spring.profiles.active") + ".properties";
			appResources[i] = resolver.getResources(loc)[0];			
			((AbstractEnvironment) env).getPropertySources()
            .addLast(new ResourcePropertySource(applicationNames.get(i), loc));
		}
		pspc.setLocations(appResources);
		return pspc;
	}

	/**
	 * Gets list of application name mentioned in bootstrap.properties
	 * 
	 * @param env
	 * @return
	 */
	public List<String> getAppNames(Environment env) {
		String names = env.getProperty("spring.application.name");
		return Stream.of(names.split(",")).collect(Collectors.toList());
	}

	/**
	 * PacketUploaderStage Bean
	 * @return
	 */
	@Bean
	public PacketUploaderStage getPacketUploaderStage() {
		return new PacketUploaderStage();
	}
	
	/**
	 * PacketArchiver Bean
	 * @return
	 */
	@Bean
	public PacketArchiver getPacketArchiver() {
		return new PacketArchiver();
	}
}
