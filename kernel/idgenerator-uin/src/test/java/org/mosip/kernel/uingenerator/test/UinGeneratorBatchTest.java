/**
 * 
 */
package org.mosip.kernel.uingenerator.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mosip.kernel.uingenerator.UinGeneratorApp;
import org.mosip.kernel.uingenerator.batch.UinBatchConfig;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { UinGeneratorApp.class, SingleJobLauncherTestUtils.class, UinBatchConfig.class })
@TestPropertySource("classpath:/test.application.properties")
public class UinGeneratorBatchTest {

	@Autowired
	@Qualifier(value = "singleJobLauncherTestUtils")
	private JobLauncherTestUtils singleJobLauncherTestUtils;

	@Autowired
	private UinBatchConfig uinBatchConfig;

	@Test
	public void testSingleJob() throws Exception {
		JobExecution jobExecution = singleJobLauncherTestUtils.launchJob();
		uinBatchConfig.uinGeneratorScheduler();
		assertThat(jobExecution.getStatus(), is(BatchStatus.COMPLETED));

	}

}
