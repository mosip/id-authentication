package org.mosip.kernel.uingenerator.test;

import org.springframework.batch.core.Job;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component(value = "singleJobLauncherTestUtils")
class SingleJobLauncherTestUtils extends JobLauncherTestUtils {

	@Autowired
	@Override
	public void setJob(Job job) {
		super.setJob(job);
	}
}