package io.mosip.registrationprocessor.stages.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.status",
		"io.mosip.registration.processor.filesystem.ceph.adapter.impl", "io.mosip.registration.processor.core",
		"io.mosip.registration.processor.rest.client", "io.mosip.registration.processor.stages.osivalidator",
		"io.mosip.registration.processor.packet.storage" })
public class DemodedupeApplication 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(DemodedupeApplication.class, args);
    }
}