package io.mosip.registration.processor.status.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ComponentScan.Filter;

import io.mosip.registration.processor.rest.client.config.RestConfigBean;
import io.mosip.registration.processor.status.config.RegistrationStatusBeanConfig;

/**
 * The Registration Status API
 * 
 * @author Pranav Kumar
 *
 */
@SpringBootApplication

@ComponentScan(basePackages= {"io.mosip.registration.processor.status.*","io.mosip.registration.processor.rest.client.*"},
excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {RegistrationStatusBeanConfig.class, RestConfigBean.class}))public class RegistrationStatusApiApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(RegistrationStatusApiApplication.class, args);
    }
}
