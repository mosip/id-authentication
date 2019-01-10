/**
 * 
 */
package io.mosip.kernel.uingenerator.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.idgenerator.uin.util.UinFilterUtils;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=UinFilterUtils.class)
public class UinFilterTest {

	@Autowired
	private UinFilterUtils uinFilterUtils;

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
