package io.mosip.kernel.templatemanager.freemarker.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import freemarker.template.Template;
import io.mosip.kernel.core.templatemanager.exception.TemplateParsingException;
import io.mosip.kernel.core.templatemanager.exception.TemplateResourceNotFoundException;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.templatemanager.freemarker.builder.TemplateManagerBuilderImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= {TemplateManagerBuilderImpl.class})
public class TemplatemanagerTest {
	
	@Mock
	private Template mockTemplate;
	private Map<String, Object> valuesMap;
	private TemplateManager templateManager;

	private static final String expected = "<html>" + "<head>" + "</head>" + "<body>"
			+ "<h1>Welcome to Cafe Coffee Day Store</h1>" + "" + "<p>6 Coffee on Sale!" + ""
			+ "We are proud to Offer these fine Coffee at these amazing prices." + "" + "this month only" + ""
			+ "Choose from :" + "</p>" + " <p><b>latte for only 150</b></p>" + " <p><b>Tea for only 100</b></p>"
			+ " <p><b>Green Tea for only 110</b></p>" + " <p><b>latte for only 150</b></p>"
			+ " <p><b>Tea for only 100</b></p>" + " <p><b>Green Tea for only 110</b></p>" + " "
			+ " <h4>Call  @ <b>1234567</b> Today</h4> " + " </body>" + "</html>";

	@Before
	public void setup() {
		templateManager = new TemplateManagerBuilderImpl().build();
	}

	@Before
	public void prepareData() {
		valuesMap = new HashMap<>();
		List<Item> itemList = new ArrayList<>();
		Item item1 = new Item("latte", "150");
		Item item2 = new Item("Tea", "100");
		Item item3 = new Item("Green Tea", "110");
		Item item4 = new Item("latte", "150");
		Item item5 = new Item("Tea", "100");
		Item item6 = new Item("Green Tea", "110");

		itemList.add(item1);
		itemList.add(item2);
		itemList.add(item3);
		itemList.add(item4);
		itemList.add(item5);
		itemList.add(item6);
		valuesMap.put("itemName", "Coffee");
		valuesMap.put("storeName", "Cafe Coffee Day");
		valuesMap.put("phoneNo", "1234567");
		valuesMap.put("itemList", itemList);
	}

	@Test

	public void testEvaluate() throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("test.ftl");

		InputStream data = templateManager.merge(is, valuesMap);
		StringWriter writer = new StringWriter();
		IOUtils.copy(data, writer, "UTF-8");
		String actual = writer.toString();
		assertEquals(expected, actual);

	}

	@Test(expected = TemplateParsingException.class)
	public void testEvaluateParseException() throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("parserException_template.ftl");
		Map<String, Object> values = new HashMap<>();
		values.put("dummy", "test");
		templateManager.merge(is, values);
	}

	@Test(expected = NullPointerException.class)
	public void testEvaluateNullValues() throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("test.ftl");
		templateManager.merge(is, null);

	}

	@Test(expected = TemplateParsingException.class)
	public void testMergeTemplateNoValue() throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("test.ftl");
		templateManager.merge(is, new HashMap<>());
	}

	@Test(expected = NullPointerException.class)
	public void testEvaluateNullTemplate() throws IOException {
		Map<String, Object> values = new HashMap<>();
		values.put("name", "Abhishek");
		templateManager.merge(null, values);

	}

	@Test(expected = NullPointerException.class)
	public void testEvaluateNullParams() throws IOException {

		templateManager.merge(null, null);
	}

	@Test

	public void testMergeDeafultEncoding() throws IOException {
		String template = "test.ftl";
		StringWriter writer = new StringWriter();
		Map<String, Object> values = new HashMap<>();
		List<Item> itemList = new ArrayList<>();
		Item item1 = new Item("latte", "150");
		Item item2 = new Item("Tea", "100");
		Item item3 = new Item("Green Tea", "110");
		Item item4 = new Item("latte", "150");
		Item item5 = new Item("Tea", "100");
		Item item6 = new Item("Green Tea", "110");

		itemList.add(item1);
		itemList.add(item2);
		itemList.add(item3);
		itemList.add(item4);
		itemList.add(item5);
		itemList.add(item6);
		values.put("itemName", "Coffee");
		values.put("storeName", "Cafe Coffee Day");
		values.put("phoneNo", "1234567");
		values.put("itemList", itemList);
		boolean actual = templateManager.merge(template, writer, values);
		assertTrue(actual);
		assertEquals(expected, writer.toString());
	}

	@Test(expected = TemplateResourceNotFoundException.class)
	public void testMergeDefaultEncodingInvalidtemplate() throws IOException {
		String template = "template.ftl";
		StringWriter writer = new StringWriter();
		Map<String, Object> values = new HashMap<>();
		Dummy dummyTest = new Dummy();
		values.put("dummy", dummyTest);
		templateManager.merge(template, writer, values);
	}

	@Test(expected = TemplateParsingException.class)
	public void testMergeDefaultEncodingInvalidtemplateContent() throws IOException {
		String template = "parserException_template.ftl";
		StringWriter writer = new StringWriter();
		Map<String, Object> values = new HashMap<>();
		Dummy dummyTest = new Dummy();
		values.put("dummy", dummyTest);
		templateManager.merge(template, writer, values);
	}

	@Test(expected = TemplateResourceNotFoundException.class)
	public void testMergeInvalidTemplate() throws IOException {

		StringWriter writer = new StringWriter();
		Map<String, Object> values = new HashMap<>();
		values.put("name", "Abhishek");
		templateManager.merge("dem245.ftl", writer, values, "UTF-8");

	}

	@Test

	public void testMergeEncoding() throws IOException {
		String template = "test.ftl";
		StringWriter writer = new StringWriter();
		Map<String, Object> values = new HashMap<>();
		List<Item> itemList = new ArrayList<>();
		Item item1 = new Item("latte", "150");
		Item item2 = new Item("Tea", "100");
		Item item3 = new Item("Green Tea", "110");
		Item item4 = new Item("latte", "150");
		Item item5 = new Item("Tea", "100");
		Item item6 = new Item("Green Tea", "110");

		itemList.add(item1);
		itemList.add(item2);
		itemList.add(item3);
		itemList.add(item4);
		itemList.add(item5);
		itemList.add(item6);
		values.put("itemName", "Coffee");
		values.put("storeName", "Cafe Coffee Day");
		values.put("phoneNo", "1234567");
		values.put("itemList", itemList);
		boolean actual = templateManager.merge(template, writer, values, "UTF-8");
		assertTrue(actual);
		assertEquals(expected, writer.toString());
	}

	@Test(expected = TemplateResourceNotFoundException.class)
	public void testMergeEncodingInvalidtemplate() throws IOException {
		String template = "template.ftl";
		StringWriter writer = new StringWriter();
		Map<String, Object> values = new HashMap<>();
		Dummy dummyTest = new Dummy();
		values.put("dummy", dummyTest);
		templateManager.merge(template, writer, values, "UTf-8");
	}

	@Test(expected = TemplateParsingException.class)
	public void testMergeEncodingInvalidtemplateContent() throws IOException {
		String template = "parserException_template.ftl";
		StringWriter writer = new StringWriter();
		Map<String, Object> values = new HashMap<>();
		Dummy dummyTest = new Dummy();
		values.put("dummy", dummyTest);
		templateManager.merge(template, writer, values, "UTF-8");
	}

	@Test(expected = TemplateResourceNotFoundException.class)
	public void testMergeEncodingInvalidTemplate() throws IOException {

		StringWriter writer = new StringWriter();
		Map<String, Object> values = new HashMap<>();
		values.put("name", "Abhishek");
		templateManager.merge("dem245.ftl", writer, values, "UTF-8");

	}

	@Test
	public void testMergeTemplate() throws IOException {
		String template = "test.ftl";
		StringWriter writer = new StringWriter();
		boolean result = templateManager.merge(template, writer, valuesMap, "UTF-8");
		assertTrue(result);
	}

	@Test(expected = NullPointerException.class)
	public void testMergeNullTemplateName() throws IOException {
		templateManager.merge(null, new StringWriter(), valuesMap);
	}

	@Test(expected = NullPointerException.class)
	public void testMergeNullWriter() throws IOException {
		templateManager.merge("test.flt", null, valuesMap);
	}

	@Test(expected = NullPointerException.class)
	public void testMergeNullValues() throws IOException {
		templateManager.merge("test.flt", new StringWriter(), null);
	}

	@Test(expected = IOException.class)
	public void testMergeIOException() throws IOException {
		Writer mockWriter = Mockito.mock(Writer.class);
		Mockito.doThrow(IOException.class).when(mockWriter).write(Mockito.anyString());
		templateManager.merge("test.ftl", mockWriter, valuesMap);
	}

}
