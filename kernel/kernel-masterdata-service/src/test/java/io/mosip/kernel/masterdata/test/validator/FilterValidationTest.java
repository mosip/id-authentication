package io.mosip.kernel.masterdata.test.validator;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.mosip.kernel.masterdata.dto.request.SearchFilter;
import io.mosip.kernel.masterdata.exception.ValidationException;
import io.mosip.kernel.masterdata.validator.FilterTypeValidator;

@RunWith(JUnit4.class)
public class FilterValidationTest {

	private FilterTypeValidator validator;

	private SearchFilter noColumnFilter;
	private SearchFilter noValuefilter;
	private SearchFilter filter;
	private SearchFilter betweenfilter;
	private SearchFilter noFilterType;
	private SearchFilter invalidColumn;
	private SearchFilter invalidFilter;
	private SearchFilter emptyColumnAndTypeFilter;

	@Before
	public void setup() {
		validator = new FilterTypeValidator();
		noColumnFilter = new SearchFilter();
		noColumnFilter.setType("equals");
		noColumnFilter.setValue("REG");

		noValuefilter = new SearchFilter();
		noValuefilter.setColumnName("column3");
		noValuefilter.setType("equals");

		betweenfilter = new SearchFilter();
		betweenfilter.setColumnName("column1");
		betweenfilter.setType("between");
		betweenfilter.setFromValue("2019-01-01T01:01:01.000Z");
		betweenfilter.setToValue("2019-01-07T01:01:01.000Z");

		filter = new SearchFilter();
		filter.setColumnName("column2");
		filter.setType("contains");
		filter.setValue("*mosip*");

		noFilterType = new SearchFilter();
		noFilterType.setColumnName("column3");

		invalidColumn = new SearchFilter();
		invalidColumn.setType("equals");
		invalidColumn.setValue("2019-01-01T01:01:01.000Z");
		invalidColumn.setColumnName("column6");

		invalidFilter = new SearchFilter();
		invalidFilter.setColumnName("column5");
		invalidFilter.setType("equals");
		invalidFilter.setValue("abcd");

		emptyColumnAndTypeFilter = new SearchFilter();
		emptyColumnAndTypeFilter.setColumnName("");
		emptyColumnAndTypeFilter.setType("");
		emptyColumnAndTypeFilter.setValue("");
	}

	@Test
	public void testValidate() {
		validator.validate(TestPojo.class, Arrays.asList(filter));
	}

	@Test(expected = ValidationException.class)
	public void testNoFilterValidate() {
		validator.validate(TestPojo.class, Arrays.asList(noFilterType));
	}

	@Test(expected = ValidationException.class)
	public void testNoColumnValidate() {
		validator.validate(TestPojo.class, Arrays.asList(noColumnFilter));
	}

	@Test(expected = ValidationException.class)
	public void testNoBetweenValuesValidate() {
		validator.validate(TestPojo.class, Arrays.asList(invalidColumn));
	}

	@Test(expected = ValidationException.class)
	public void testStartsWithValidate() {
		validator.validate(TestPojo.class, Arrays.asList(invalidColumn));
	}

	@Test(expected = ValidationException.class)
	public void testNullFilterValuesValidate() {
		validator.validate(TestPojo.class, Arrays.asList(new SearchFilter()));
	}

	@Test(expected = ValidationException.class)
	public void testFilterNotSupprtedValidate() {
		validator.validate(TestPojo.class, Arrays.asList(invalidFilter));
	}

	@Test
	public void testEmptyFilterListValidate() {
		validator.validate(TestPojo.class, Arrays.asList());
	}

	@Test
	public void testEmptyFilterValuesListValidate() {
		validator.validate(TestPojo.class, Arrays.asList(emptyColumnAndTypeFilter));
	}

}
