package io.mosip.authentication.fw.precon;

import static io.mosip.authentication.fw.util.AuthTestsUtil.getPropertyFromFilePath;

import java.io.ByteArrayInputStream; 
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import io.mosip.authentication.fw.util.AuthTestsUtil;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
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
	private static Document xmlDocument;
	
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
				String valueFromProperty = AuthTestsUtil.getPropertyFromFilePath(mappingFilePath).getProperty(keys[0]);
				xpath = valueFromProperty.replace("$" + keys[1] + "$", keys[2]);
			} else
				xpath = AuthTestsUtil.getPropertyFromFilePath(mappingFilePath).getProperty(mappingFieldName);
			return getValueFromXmlContent(new String(Files.readAllBytes(Paths.get(inputFilePath))), xpath.toString());
		} catch (Exception exception) {
			XMLPRECONDTION_LOGGER
					.error("Exception Occured in retrieving the value from xml file: " + exception.getMessage());
			return exception.toString();
		}
	}
	
	/**
	 * The method wil update the xml file and generate in output file
	 * 
	 * @author Athila
	 * @param inputFilePath
	 * @param fieldvalue
	 * @param outputFilePath
	 * @param propFileName
	 * @return true or false
	 */
	public static boolean parseAndwriteXmlFile(String inputFilePath, Map<String, String> fieldvalue,
			String outputFilePath, String propFileName) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			xmlDocument = docBuilder.parse(inputFilePath);
			fieldvalue.forEach((key, value) -> {
				XPath xpath = XPathFactory.newInstance().newXPath();
				// Evaluate Xpath
				NodeList nodes = evaluateXpath(xpath,
						getPropertyFromFilePath(propFileName).getProperty(key).toString());
				nodes.item(0).setNodeValue(value);
			});
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(new DOMSource(xmlDocument), new StreamResult(new File(outputFilePath)));
			return true;
		} catch (Exception exception) {
			XMLPRECONDTION_LOGGER.error("Exception Occured in XML Precondtion: " + exception.getMessage());
			return false;
		}
	}
	
	private static NodeList evaluateXpath(XPath xpath, String xpathStr) {
		try {
			return (NodeList) xpath.evaluate(xpathStr, xmlDocument, XPathConstants.NODESET);
		} catch (Exception exp) {
			XMLPRECONDTION_LOGGER
					.error("Exception occured in xpath. Check correct xpath used in mapping" + exp.getMessage());
			return null;
		}
	}
	
/*	public static void main(String arg[])
	{
		String inputFile="D:\\git\\idrepo_automation\\automationtests\\src\\test\\resources\\ida\\TestData\\idRepo\\CreateUIN\\cbeff-bio-data.xml";
		Map<String, String> fieldvalue = new HashMap<String, String>();
		fieldvalue.put("versionMajor", "45jhorwirhfh");
		String outputFilePath="D:\\git\\idrepo_automation\\automationtests\\src\\test\\resources\\ida\\TestData\\idRepo\\CreateUIN\\out-cbeff-bio-data.xml";
		String propFileName="D:\\git\\idrepo_automation\\automationtests\\src\\test\\resources\\ida\\TestData\\idRepo\\CreateUIN\\mapping.properties";
		parseAndwriteXmlFile(inputFile,fieldvalue,outputFilePath,propFileName);
	}*/
}
