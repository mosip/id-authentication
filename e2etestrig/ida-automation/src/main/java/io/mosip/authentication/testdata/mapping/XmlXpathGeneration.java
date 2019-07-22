package io.mosip.authentication.testdata.mapping;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import io.mosip.authentication.fw.precon.XmlPrecondtion;

/**
 * The class to generate list of xpath for xml
 * 
 * @author Vignesh
 *
 */
public class XmlXpathGeneration extends DefaultHandler {
	private static final Logger xmlXpathLogger = Logger.getLogger(XmlPrecondtion.class);
	private static Map<String, String> mappingFieldvalue = new HashMap<String, String>();
	private static Set<String> xpathList = new HashSet<String>();
	String xPath = "/";
	XMLReader xmlReader;
	XmlXpathGeneration parent;
	StringBuilder characters = new StringBuilder();
	Map<String, Integer> elementNameCount = new HashMap<String, Integer>();

	public XmlXpathGeneration(XMLReader xmlReader) {
		this.xmlReader = xmlReader;
	}

	private XmlXpathGeneration(String xPath, XMLReader xmlReader, XmlXpathGeneration parent) {
		this(xmlReader);
		this.xPath = xPath;
		this.parent = parent;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		Integer count = elementNameCount.get(qName);
		if (null == count)
			count = 1;
		else
			count++;
		elementNameCount.put(qName, count);
		String childXPath = xPath + "/" + qName + "[" + count + "]";
		int attsLength = attributes.getLength();
		for (int x = 0; x < attsLength; x++)
			xpathList.add(childXPath + "/@" + attributes.getQName(x));
		XmlXpathGeneration child = new XmlXpathGeneration(childXPath, xmlReader, this);
		xmlReader.setContentHandler(child);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		String value = characters.toString().trim();
		if (value.length() > 0) {
			// System.out.println(xPath + "='" + characters.toString() + "'");
			xpathList.add(xPath + "/text()");
		}
		xmlReader.setContentHandler(parent);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		characters.append(ch, start, length);
	}

	public static void generateXpath(String filePath, String ouputFilePath) {
		try {
			SAXParserFactory saxFactory = SAXParserFactory.newInstance();
			saxFactory.setNamespaceAware(false);
			SAXParser saxParser = saxFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(new XmlXpathGeneration(xmlReader));
			xmlReader.parse(new InputSource(new FileInputStream(filePath)));
			refactorFieldValueName();
			generateJsonMappingDic(ouputFilePath);
		} catch (Exception e) {
			xmlXpathLogger.info("Exception occured: " + e.getMessage());
		}
	}

	/**
	 * Method generate json mapping in property file
	 * 
	 * @param filePath
	 */
	public static void generateJsonMappingDic(String filePath) {
		Properties prop = new Properties();
		OutputStream output = null;
		try {
			output = new FileOutputStream(filePath);
			for (Entry<String, String> entry : mappingFieldvalue.entrySet()) {
				prop.setProperty(entry.getKey(), entry.getValue());
			}
			prop.store(output, "UTF-8");
		} catch (Exception e) {
			xmlXpathLogger.error(e.getMessage());
		}
	}

	/**
	 * Method to modify xpath value name
	 */
	private static void refactorFieldValueName() {
		System.out.println(xpathList);
		for (String entry : xpathList) {
			String tempValue = entry.replace("[", "").replace("]", "").replace("/text()", "").replace("@", "");
			String[] listValue = tempValue.split(Pattern.quote("/"));
			String fieldKey = "";
			for(int i=1;i<listValue.length;i++) {
				if(i!=4)
					fieldKey = fieldKey + listValue[listValue.length-i];
				else if(mappingFieldvalue.containsKey(fieldKey))
					fieldKey = fieldKey + listValue[listValue.length-i];
				else
					break;
			}
			fieldKey=fieldKey.replace("\"", "");
			fieldKey=fieldKey.replace(":", "");
			mappingFieldvalue.put(fieldKey, entry.toString());
		}
	}
}