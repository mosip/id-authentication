package io.mosip.kernel.templatemanager.velocity.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.spi.templatemanager.MosipTemplateManager;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateConfigureBuilder;
import io.mosip.kernel.templatemanager.velocity.exception.TemplateMethodInvocationException;
import io.mosip.kernel.templatemanager.velocity.exception.TemplateParsingException;
import io.mosip.kernel.templatemanager.velocity.exception.TemplateResourceNotFoundException;

@RunWith(SpringRunner.class)
public class TemplatemanagerTest {

	private MosipTemplateManager templateManager;
	Map<String, Object> valueMap;
	private static final String expected = "<html>\r\n" + "<head>\r\n" + "</head>\r\n" + "<body>\r\n"
			+ "<h1>Welcome to Cafe Coffee Day Store</h1>\r\n" + "\r\n" + "<p>6 Coffee on Sale!\r\n" + "\r\n"
			+ "We are proud to Offer these fine Coffee at these amazing prices.\r\n" + "\r\n" + "this month only\r\n"
			+ "\r\n" + "Choose from :\r\n" + "</p>\r\n" + "  	<p><b>latte for only 150</b></p>\r\n"
			+ "  	<p><b>Tea for only 100</b></p>\r\n" + "  	<p><b>Green Tea for only 110</b></p>\r\n"
			+ "  	<p><b>latte for only 150</b></p>\r\n" + "  	<p><b>Tea for only 100</b></p>\r\n"
			+ "  	<p><b>Green Tea for only 110</b></p>\r\n" + "  \r\n" + " <h4>Call  @ <b>1234567</b> Today</h4> \r\n"
			+ " </body>\r\n" + "</html>";

	@Before
	public void setup() {
		templateManager = new TemplateConfigureBuilder().enableCache(false).resourceLoader("classpath").build();
	}

	@Before
	public void prepareData() {
		valueMap = new HashMap<>();
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
		valueMap.put("itemName", "Coffee");
		valueMap.put("storeName", "Cafe Coffee Day");
		valueMap.put("phoneNo", "1234567");
		valueMap.put("itemList", itemList);
	}

	@Test
	@Ignore
	public void testEvaluate() throws IOException {

		InputStream is = this.getClass().getClassLoader().getResourceAsStream("test.vm");

		InputStream data = templateManager.merge(is, valueMap);
		StringWriter writer = new StringWriter();
		IOUtils.copy(data, writer, "UTF-8");
		String actual = writer.toString();
		assertEquals(expected, actual);

	}

	@Test(expected = TemplateMethodInvocationException.class)
	
	public void testEvaluateMethodInvocationException() throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("methodInvocation_template.vm");
		Map<String, Object> values = new HashMap<>();
		Dummy dummyTest = new Dummy();
		values.put("dummy", dummyTest);
		templateManager.merge(is, values);
	}

	@Test(expected = TemplateParsingException.class)
	public void testEvaluateParseException() throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("parserException_template.vm");
		Map<String, Object> values = new HashMap<>();
		values.put("dummy", "test");
		templateManager.merge(is, values);
	}

	@Test(expected = NullPointerException.class)
	public void testEvaluateNullValues() throws IOException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("test.vm");
		InputStream data = templateManager.merge(is, null);
		assertNull(data);

	}

	@Test(expected = NullPointerException.class)
	public void testEvaluateNullTemplate() throws IOException {
		Map<String, Object> values = new HashMap<>();
		values.put("name", "Abhishek");
		InputStream data = templateManager.merge(null, values);
		assertNull(data);

	}

	@Test(expected = NullPointerException.class)
	public void testEvaluateNullParams() throws IOException {

		InputStream data = templateManager.merge(null, null);
		assertNull(data);
	}

	@Test
	@Ignore
	public void testMergeDeafultEncoding() throws IOException {
		String template = "test.vm";
		StringWriter writer = new StringWriter();

		boolean actual = templateManager.merge(template, writer, valueMap);
		assertTrue(actual);
		assertEquals(expected, writer.toString());
	}

	@Test(expected = TemplateMethodInvocationException.class)
	public void testMergeDefaultEncodingMethodInvocationException() throws IOException {
		String template = "methodInvocation_template.vm";
		StringWriter writer = new StringWriter();
		Map<String, Object> values = new HashMap<>();
		Dummy dummyTest = new Dummy();
		values.put("dummy", dummyTest);
		templateManager.merge(template, writer, values);
	}

	@Test(expected = TemplateResourceNotFoundException.class)
	public void testMergeDefaultEncodingInvalidtemplate() throws IOException {
		String template = "template.vm";
		StringWriter writer = new StringWriter();
		Map<String, Object> values = new HashMap<>();
		Dummy dummyTest = new Dummy();
		values.put("dummy", dummyTest);
		templateManager.merge(template, writer, values);
	}

	@Test(expected = TemplateParsingException.class)
	public void testMergeDefaultEncodingInvalidtemplateContent() throws IOException {
		String template = "parserException_template.vm";
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
		templateManager.merge("dem245.vm", writer, values, "UTF-8");

	}

	@Test
	@Ignore
	public void testMergeEncoding() throws IOException {
		String template = "test.vm";
		StringWriter writer = new StringWriter();

		boolean actual = templateManager.merge(template, writer, valueMap, "UTF-8");
		assertTrue(actual);
		assertEquals(expected, writer.toString());
	}

	@Test
	@Ignore
	public void testMergeEncodingFileResource() throws IOException {
		templateManager = new TemplateConfigureBuilder().build();
		String template = "/test.vm";
		StringWriter writer = new StringWriter();

		boolean actual = templateManager.merge(template, writer, valueMap, "UTF-8");
		assertTrue(actual);
		assertEquals(expected, writer.toString());
	}

	@Test(expected = TemplateMethodInvocationException.class)
	public void testMergeEncodingMethodInvocationException() throws IOException {
		String template = "methodInvocation_template.vm";
		StringWriter writer = new StringWriter();
		Map<String, Object> values = new HashMap<>();
		Dummy dummyTest = new Dummy();
		values.put("dummy", dummyTest);
		templateManager.merge(template, writer, values, "UTF-8");
	}

	@Test(expected = TemplateResourceNotFoundException.class)
	public void testMergeEncodingInvalidtemplate() throws IOException {
		String template = "template.vm";
		StringWriter writer = new StringWriter();
		Map<String, Object> values = new HashMap<>();
		Dummy dummyTest = new Dummy();
		values.put("dummy", dummyTest);
		templateManager.merge(template, writer, values, "UTf-8");
	}

	@Test(expected = TemplateParsingException.class)
	public void testMergeEncodingInvalidtemplateContent() throws IOException {
		String template = "parserException_template.vm";
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
		templateManager.merge("dem245.vm", writer, values, "UTF-8");

	}
}
