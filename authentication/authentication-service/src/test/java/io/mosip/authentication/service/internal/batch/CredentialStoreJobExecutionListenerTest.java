package io.mosip.authentication.service.internal.batch;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.batch.core.job.JobExecution;

import java.util.*;

@WebMvcTest
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
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
        jobExecution = new JobExecution(id, new JobInstance(id, "testJob"), jobParameters);
//        case 1
        credentialStoreJobExecutionListener.afterJob(jobExecution);

        Collection<StepExecution> stepExecutions = new ArrayList<>();
        StepExecution step1 = new StepExecution("step1", jobExecution);
        jobExecution.addStepExecution(step1);
        step1.setReadCount(0);
        StepExecution step2 = new StepExecution("step2", jobExecution);
        jobExecution.addStepExecution(step2);
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
        jobExecution = new JobExecution(id, new JobInstance(id, "testJob"), jobParameters);
        credentialStoreJobExecutionListener.beforeJob(jobExecution);
    }
}