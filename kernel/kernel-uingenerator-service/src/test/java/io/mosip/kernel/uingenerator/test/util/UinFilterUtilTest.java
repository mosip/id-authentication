package io.mosip.kernel.uingenerator.test.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${mosip.kernel.uin.test.valid-uin}")
	private String validUin;

	@Value("${mosip.kernel.uin.test.invalid-sequence-asc-uin}")
	private String invalidSequenceAscendingUin;

	@Value("${mosip.kernel.uin.test.invalid-sequence-desc-uin}")
	private String invalidSequenceDescendingUin;

	@Value("${mosip.kernel.uin.test.invalid-repeating-uin}")
	private String invalidRepeatingUin;

	@Value("${mosip.kernel.uin.test.invalid-repeating-one-uin}")
	private String invalidRepeatingOneUin;

	@Value("${mosip.kernel.uin.test.valid-repeating-uin}")
	private String validRepeatingUin;

	@Value("${mosip.kernel.uin.test.invalid-repeating-block-uin}")
	private String invalidRepeatingBlockUin;

	@Value("${mosip.kernel.uin.test.valid-repeating-block-uin}")
	private String validRepeatingBlockUin;

	@Value("${mosip.kernel.uin.test.invalid-repeating-group-uin}")
	private String invalidRepeatingGroupUin;

	@Value("${mosip.kernel.uin.test.invalid-repeating-reverse-group-uin}")
	private String invalidRepeatingReverseGroupUin;

	@Value("${mosip.kernel.uin.test.valid-adjacent-even-digit-uin}")
	private String validAdjacentEvenDigitUin;

	@Value("${mosip.kernel.uin.test.invalid-adjacent-even-digit-uin}")
	private String invalidAdjacentEvenDigitUin;

	@Autowired
	private UinFilterUtil uinFilterUtils;

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

		boolean res = uinFilterUtils.isValidId(invalidRepeatingUin);
		assertThat(res, is(false));
	}

	@Test
	public void filterRepeatOneFailTest() {

		boolean res = uinFilterUtils.isValidId(invalidRepeatingOneUin);
		assertThat(res, is(false));
	}

	@Test
	public void filterRepeatPassTest() {

		boolean res = uinFilterUtils.isValidId(validRepeatingUin);
		assertThat(res, is(true));
	}

	@Test
	public void filterRepeatBlockFailTest() {

		boolean res = uinFilterUtils.isValidId(invalidRepeatingBlockUin);
		assertThat(res, is(false));
	}

	@Test
	public void filterRepeatBlockPassTest() {

		boolean res = uinFilterUtils.isValidId(validRepeatingBlockUin);
		assertThat(res, is(true));
	}

	@Test
	public void filterRepeatGroupFailTest() {

		boolean res = uinFilterUtils.isValidId(invalidRepeatingGroupUin);
		assertThat(res, is(false));
	}

	@Test
	public void filterReverseRepeatGroupFailTest() {

		boolean res = uinFilterUtils.isValidId(invalidRepeatingReverseGroupUin);
		assertThat(res, is(false));
	}

	@Test
	public void filterAdjacentEvenDigitFailTest() {

		boolean res = uinFilterUtils.isValidId(validAdjacentEvenDigitUin);
		assertThat(res, is(false));
	}

	@Test
	public void filterAdjacentEvenDigitPassTest() {

		boolean res = uinFilterUtils.isValidId(invalidAdjacentEvenDigitUin);
		assertThat(res, is(true));
	}

}
