package io.mosip.kernel.masterdata.test.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import io.mosip.kernel.masterdata.utils.EmptyCheckUtils;

/**
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class EmptyCheckUtilsTest {

	@BeforeClass
	public static void setup() {

	}

	@Test
	public void isNullEmptyObjectTest() {
		Date d = null;
		assertTrue(EmptyCheckUtils.isNullEmpty(d));
	}


	@Test
	public void isNullEmptyStringTest() {
		String str = null;
		assertTrue(EmptyCheckUtils.isNullEmpty(""));
		assertTrue(EmptyCheckUtils.isNullEmpty("   "));
		assertTrue(EmptyCheckUtils.isNullEmpty(str));
		assertFalse(EmptyCheckUtils.isNullEmpty(" jfashd kjasdkjf"));
	}

	@Test
	public void isNullEmptyCollectionTest() {
		List<String> strings = null;
		Set<String> stringSet = new HashSet<>();
		List<String> names = new ArrayList<>();
		IntStream.of(10).forEach(i -> names.add("name : " + i));
		assertTrue(EmptyCheckUtils.isNullEmpty(strings));
		assertTrue(EmptyCheckUtils.isNullEmpty(stringSet));
		assertFalse(EmptyCheckUtils.isNullEmpty(names));
	}

	@Test
	public void isNullEmptyMapTest() {
		Map<Integer, String> nameRollMapp = null;
		Map<Integer, String> nameNumberMapp = new HashMap<>();
		Map<Integer, String> nameStateMapp = new HashMap<>();
		IntStream.of(10).forEach(i -> nameStateMapp.put(i, "State code : " + i));

		assertTrue(EmptyCheckUtils.isNullEmpty(nameRollMapp));
		assertTrue(EmptyCheckUtils.isNullEmpty(nameNumberMapp));
		assertFalse(EmptyCheckUtils.isNullEmpty(nameStateMapp));
	}

}
