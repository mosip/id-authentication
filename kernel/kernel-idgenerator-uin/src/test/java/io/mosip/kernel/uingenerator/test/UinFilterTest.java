/**
 * 
 */
package io.mosip.kernel.uingenerator.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.idgenerator.uin.util.UinFilterUtils;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UinFilterUtils.class)
public class UinFilterTest {

	@Value("${mosip.kernel.uin.test.valid-uin}")
	private String validUin;

	@Value("${mosip.kernel.uin.test.invalid-sequence-asc-uin}")
	private String invalidSequenceAscendingUin;

	@Value("${mosip.kernel.uin.test.invalid-sequence-desc-uin}")
	private String invalidSequenceDescendingUin;

	@Value("${mosip.kernel.uin.test.invalid-repeating-uin}")
	private String invalidReaptingUin;

	@Value("${mosip.kernel.uin.test.invalid-repeating-one-uin}")
	private String invalidReaptingOneUin;

	@Value("${mosip.kernel.uin.test.valid-repeating-uin}")
	private String validReaptingUin;

	@Value("${mosip.kernel.uin.test.invalid-repeating-block-uin}")
	private String invalidReaptingBlockUin;

	@Value("${mosip.kernel.uin.test.valid-repeating-block-uin}")
	private String validReaptingBlockUin;

	@Autowired
	private UinFilterUtils uinFilterUtils;

	@Test
	public void filterIdTest() {
		boolean res = uinFilterUtils.isValidId(validUin);
		assertThat(res, is(true));
	}

	@Test
	public void filterSeqAscFailTest() {

		boolean res = uinFilterUtils.isValidId(invalidSequenceAscendingUin);
		assertThat(res, is(false));
	}

	@Test
	public void filterSeqDescFailTest() {

		boolean res = uinFilterUtils.isValidId(invalidSequenceDescendingUin);
		assertThat(res, is(false));
	}

	@Test
	public void filterRepeatFailTest() {

		boolean res = uinFilterUtils.isValidId(invalidReaptingUin);
		assertThat(res, is(false));
	}

	@Test
	public void filterRepeatOneFailTest() {

		boolean res = uinFilterUtils.isValidId(invalidReaptingOneUin);
		assertThat(res, is(false));
	}

	@Test
	public void filterRepeatPassTest() {

		boolean res = uinFilterUtils.isValidId(validReaptingUin);
		assertThat(res, is(true));
	}

	@Test
	public void filterRepeatBlockFailTest() {

		boolean res = uinFilterUtils.isValidId(invalidReaptingBlockUin);
		assertThat(res, is(false));
	}

	@Test
	public void filterRepeatBlockPassTest() {

		boolean res = uinFilterUtils.isValidId(validReaptingBlockUin);
		assertThat(res, is(false));
	}

}
