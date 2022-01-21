package io.mosip.authentication.internal.service.batch;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.batch.core.JobExecution;

import java.util.*;

@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@RunWith(SpringRunner.class)
public class CredentialStoreJobExecutionListenerTest {

    @InjectMocks
    private CredentialStoreJobExecutionListener credentialStoreJobExecutionListener;

    private JobExecution jobExecution;

    /**
     * This class tests the afterJob method
     */
    @Test
    public void afterJobTest(){
        Long id = Long.valueOf(1213);
        JobParameters jobParameters = new JobParameters();
        String jobConfigurationName = "getJobConfigurationName";
        jobExecution = new JobExecution(id, jobParameters, jobConfigurationName);
//        case 1
        credentialStoreJobExecutionListener.afterJob(jobExecution);

        Collection<StepExecution> stepExecutions = new ArrayList<>();
        StepExecution step1 = jobExecution.createStepExecution("step1");
        step1.setReadCount(0);
        StepExecution step2 = jobExecution.createStepExecution("step2");
        step2.setReadCount(0);
        stepExecutions.add(step1);
        stepExecutions.add(step2);
//        case 2
        credentialStoreJobExecutionListener.afterJob(jobExecution);

        step1.setReadCount(2); step2.setReadCount(2);
        stepExecutions.clear();
        stepExecutions.add(step1); stepExecutions.add(step2);
//        case 3
        credentialStoreJobExecutionListener.afterJob(jobExecution);

        stepExecutions.clear();
        Exception exception1 = new Exception();
        step1.addFailureException(exception1);
        step2.addFailureException(exception1);
        stepExecutions.clear();
        stepExecutions.add(step1);
        stepExecutions.add(step2);
//        case 4
        credentialStoreJobExecutionListener.afterJob(jobExecution);
    }

    /**
     * This class tests the beforeJob method
     */
    @Test
    public void beforeJobTest(){
        Long id = Long.valueOf(1213);
        JobParameters jobParameters = new JobParameters();
        String jobConfigurationName = "getJobConfigurationName";
        jobExecution = new JobExecution(id, jobParameters, jobConfigurationName);
        credentialStoreJobExecutionListener.beforeJob(jobExecution);
    }
}