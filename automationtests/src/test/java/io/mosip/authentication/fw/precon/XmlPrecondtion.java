
package io.mosip.authentication.fw.precon;

import java.io.ByteArrayInputStream; 
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.w3c.dom.Document;
import io.mosip.authentication.fw.util.IdaScriptsUtil;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import org.apache.log4j.Logger;

/**
 * The class is to handle all XML file or message process activity
 * 
 * @author Vignesh
 *
 */
public class XmlPrecondtion{
	private static final Logger XMLPRECONDTION_LOGGER = Logger.getLogger(XmlPrecondtion.class);
	
	/**
	 * The method get node value from xml file
	 * 
	 * @param path, xml file path
	 * @param expression, xml xpath
	 * @return value, xml node or attribute value
	 */
	public static String getValueFromXmlFile(String path, String expression) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			File inputFile = new File(path);
			builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(inputFile);
			doc.getDocumentElement().normalize();
			XPath xPath = XPathFactory.newInstance().newXPath();
			return xPath.compile(expression).evaluate(doc).toString();
		} catch (Exception e) {
			XMLPRECONDTION_LOGGER.error("Exception in xml precondtion : " + e.getMessage());
			return e.getMessage();
		}
	}
	
	/**
	 * The method get node value from xml content
	 * 
	 * @param content, XML content
	 * @param expression, XML xpath
	 * @return value,  xml node or attribute value
	 */
	public static String getValueFromXmlContent(String content, String expression) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			StringBuilder xmlStringBuilder = new StringBuilder();
			xmlStringBuilder.append(content);
			ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
			builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(input);
			doc.getDocumentElement().normalize();
			XPath xPath = XPathFactory.newInstance().newXPath();
			return xPath.compile(expression).evaluate(doc).toString();
		} catch (Exception e) {
			XMLPRECONDTION_LOGGER.error("Exception in xml precondtion : " + e.getMessage());
			return e.getMessage();
		}
	}
	
	/**
	 * The method get the value from XML using mapping dic
	 * 
	 * @param inputFilePath, XML file path
	 * @param mappingFileName, Mapping file path
	 * @param mappingFieldName, Mapping field name
	 * @return value, xml node or attribute value
	 */
	public static String getValueFromXmlUsingMapping(String inputFilePath, String mappingFilePath, String mappingFieldName) {
		try {
			String xpath;
			if (mappingFieldName.contains(":")) {
				String keys[] = mappingFieldName.split(":");
				String valueFromProperty = IdaScriptsUtil.getPropertyFromFilePath(mappingFilePath).getProperty(keys[0]);
				xpath = valueFromProperty.replace("$" + keys[1] + "$", keys[2]);
			} else
				xpath = IdaScriptsUtil.getPropertyFromFilePath(mappingFilePath).getProperty(mappingFieldName);
			return getValueFromXmlContent(new String(Files.readAllBytes(Paths.get(inputFilePath))), xpath.toString());
		} catch (Exception exception) {
			XMLPRECONDTION_LOGGER
					.error("Exception Occured in retrieving the value from xml file: " + exception.getMessage());
			return exception.toString();
		}
	}
}
