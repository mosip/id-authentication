package io.mosip.kernel.uingenerator.test.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import io.mosip.kernel.uingenerator.test.config.UinGeneratorTestConfiguration;
import io.mosip.kernel.uingenerator.util.UinFilterUtil;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UinGeneratorTestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class UinFilterUtilTest {

	@Autowired
	private UinFilterUtil uinFilterUtils;

	@Test
	public void filterIdTest() {
		String id = "1029384756";
		boolean res = uinFilterUtils.isValidId(id);
		assertThat(res, is(true));
	}

	@Test
	public void filterSeqAscFailTest() {
		String id = "123";
		boolean res = uinFilterUtils.isValidId(id);
		assertThat(res, is(false));
	}

	@Test
	public void filterSeqDescFailTest() {
		String id = "987";
		boolean res = uinFilterUtils.isValidId(id);
		assertThat(res, is(false));
	}

	@Test
	public void filterRepeatFailTest() {
		String id = "1199";
		boolean res = uinFilterUtils.isValidId(id);
		assertThat(res, is(false));
	}

	@Test
	public void filterRepeatOneFailTest() {
		String id = "101202";
		boolean res = uinFilterUtils.isValidId(id);
		assertThat(res, is(false));
	}

	@Test
	public void filterRepeatPassTest() {
		String id = "39032802";
		boolean res = uinFilterUtils.isValidId(id);
		assertThat(res, is(true));
	}

	@Test
	public void filterRepeatBlockFailTest() {
		String id = "198198";
		boolean res = uinFilterUtils.isValidId(id);
		assertThat(res, is(false));
	}

	@Test
	public void filterRepeatBlockPassTest() {
		String id = "19841984";
		boolean res = uinFilterUtils.isValidId(id);
		assertThat(res, is(false));
	}
}
