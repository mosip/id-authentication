package io.mosip.registration.processor.abis;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "io.mosip.registration.processor.abis"})
public class RestAbisApplication 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(RestAbisApplication.class, args);
    }
}
