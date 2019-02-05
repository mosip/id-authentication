package io.mosip.registration.processor.status.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * The Registration Status API
 * 
 * @author Pranav Kumar
 *
 */
@SpringBootApplication
@ComponentScan(basePackages="io.mosip.registration.processor.*")
public class RegistrationStatusApiApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(RegistrationStatusApiApplication.class, args);
    }
}
