package io.mosip.preregistration.batchjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@ComponentScan(basePackages="io.mosip.*")
public class PreRegistrationBatchJob 
{
    public static void main( String[] args )
    {
        SpringApplication.run(PreRegistrationBatchJob.class, args);
    }
}
