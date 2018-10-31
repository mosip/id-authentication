/**
 * 
 */
package io.mosip.kernel.uingenerator.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import io.mosip.kernel.idgenerator.uin.util.UinFilterUtils;


/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
public class UinFilterTest {

	@Test
	public void filterIdTest() {
		String id = "1029384756";
		boolean res = UinFilterUtils.isValidId(id);
		assertThat(res, is(true));
	}

	@Test
	public void filterSeqAscFailTest() {
		String id = "123";
		boolean res = UinFilterUtils.isValidId(id);
		assertThat(res, is(false));
	}

	@Test
	public void filterSeqDescFailTest() {
		String id = "987";
		boolean res = UinFilterUtils.isValidId(id);
		assertThat(res, is(false));
	}

	@Test
	public void filterRepeatFailTest() {
		String id = "1199";
		boolean res = UinFilterUtils.isValidId(id);
		assertThat(res, is(false));
	}

	@Test
	public void filterRepeatOneFailTest() {
		String id = "101202";
		boolean res = UinFilterUtils.isValidId(id);
		assertThat(res, is(false));
	}

	@Test
	public void filterRepeatPassTest() {
		String id = "39032802";
		boolean res = UinFilterUtils.isValidId(id);
		assertThat(res, is(true));
	}

	@Test
	public void filterRepeatBlockFailTest() {
		String id = "198198";
		boolean res = UinFilterUtils.isValidId(id);
		assertThat(res, is(false));
	}

	@Test
	public void filterRepeatBlockPassTest() {
		String id = "19841984";
		boolean res = UinFilterUtils.isValidId(id);
		assertThat(res, is(false));
	}

}
