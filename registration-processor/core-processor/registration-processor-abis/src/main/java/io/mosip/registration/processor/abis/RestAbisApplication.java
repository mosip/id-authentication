package io.mosip.registration.processor.abis;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import io.mosip.registration.processor.abis.messagequeue.AbisMessageQueueImpl;
import io.mosip.registration.processor.packet.storage.config.PacketStorageBeanConfig;
import io.mosip.registration.processor.rest.client.config.RestConfigBean;
import io.mosip.registration.processor.status.config.RegistrationStatusBeanConfig;

@SpringBootApplication
@ComponentScan(basePackages = { "io.mosip.registration.processor.*" }, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
        RegistrationStatusBeanConfig.class, RestConfigBean.class, PacketStorageBeanConfig.class, RestConfigBean.class }))
public class RestAbisApplication {
	
	@Autowired
	AbisMessageQueueImpl abisMessageQueueImpl;
   
	public static void main( String[] args )
    {
        SpringApplication.run(RestAbisApplication.class, args);
    }
    
    @PostConstruct
    public void runAbisQueue() {
    	abisMessageQueueImpl.runAbisQueue();
    }
}
