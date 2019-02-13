package io.mosip.registration.processor.abis;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ComponentScan.Filter;
import io.mosip.registration.processor.packet.storage.config.PacketStorageBeanConfig;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;
import io.mosip.registration.processor.status.config.RegistrationStatusBeanConfig;

@SpringBootApplication
@ComponentScan(basePackages = { "io.mosip.registration.processor.status.*",
"io.mosip.registration.processor.rest.client.*" }, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
		RegistrationStatusBeanConfig.class, RestConfigBean.class, PacketStorageBeanConfig.class, RestConfigBean.class }))
public class RestAbisApplication 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(RestAbisApplication.class, args);
    }
}
