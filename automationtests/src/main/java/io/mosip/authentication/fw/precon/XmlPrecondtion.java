package io.mosip.authentication.fw.precon;

import static io.mosip.authentication.fw.util.AuthTestsUtil.getPropertyFromFilePath;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import io.mosip.authentication.fw.util.AuthTestsUtil;
import io.mosip.authentication.testdata.Precondtion;
import io.mosip.authentication.testdata.TestDataConfig;
import io.mosip.authentication.testdata.mapping.XmlXpathGeneration;

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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

/**
 * The class is to handle all XML file or message process activity
 * 
 * @author Vignesh
 *
 */
public class XmlPrecondtion extends MessagePrecondtion{
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
	 * The method get node value from xml file
	 * 
	 * @param path, xml file path
	 * @param expression, xml xpath
	 * @return value, xml node or attribute value
	 */
	public static Map<String, String> getValueFromXmlFile(String path, Map<String, String> expressionMap) {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Map<String, String> returnMap = null;
		try {
			returnMap = new HashMap<String, String>();
			File inputFile = new File(path);
			builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(inputFile);
			doc.getDocumentElement().normalize();
			XPath xPath = XPathFactory.newInstance().newXPath();
			for (Entry<String, String> entry : expressionMap.entrySet()) {
				String value = xPath.compile(entry.getValue()).evaluate(doc).toString();
				if (value != null)
					returnMap.put(entry.getValue(), value);
				else
					returnMap.put(entry.getValue(), "null");
			}
			return returnMap;
		} catch (Exception e) {
			XMLPRECONDTION_LOGGER.error("Exception in xml precondtion, while retrieving the value from xml : " + e.getMessage());
			return returnMap;
		}
	}

	/**
	 * The method get node value from xml content
	 * 
	 * @param content, XML content
	 * @param expression, XML xpath
	 * @return value, xml node or attribute value
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
	public static String getValueFromXmlUsingMapping(String inputFilePath, String mappingFilePath,
			String mappingFieldName) {
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
	public Map<String, String> parseAndWriteFile(String inputFilePath, Map<String, String> fieldvalue,
			String outputFilePath, String propFileName) {
		try {
			fieldvalue = Precondtion.getKeywordObject(TestDataConfig.getModuleName()).precondtionKeywords(fieldvalue);
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(false);
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			xmlDocument = docBuilder.parse(inputFilePath);
			for (Entry<String, String> entry : fieldvalue.entrySet()) {
				XPath xpath = XPathFactory.newInstance().newXPath();
				// Evaluate Xpath
				String expression = getPropertyFromFilePath(propFileName).getProperty(entry.getKey()).toString();
				String normalisedExpression = normalisedXpath(expression);
				if (normalisedExpression.contains("@"))
					updateAttributeValue(xpath, expression, normalisedExpression, entry.getValue());
				else
					updateNodeValue(xpath, normalisedExpression, entry.getValue());
			}
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(new DOMSource(xmlDocument), new StreamResult(new File(outputFilePath)));
			return fieldvalue;
		} catch (Exception exception) {
			XMLPRECONDTION_LOGGER.error("Exception Occured in XML Precondtion: " + exception.getMessage());
			return fieldvalue;
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

	private static String normalisedXpath(String xpath) {
		String normalisedXpath = "//";
		xpath = xpath.replace("//", "");
		String[] values = xpath.split(Pattern.quote("/"));
		for (int i = 0; i < values.length; i++) {
			if (values[i].contains("@"))
				normalisedXpath = normalisedXpath + "@"
						+ values[i].substring(values[i].indexOf(":") + 1, values[i].length()) + "/";
			else
				normalisedXpath = normalisedXpath + values[i].substring(values[i].indexOf(":") + 1, values[i].length())
						+ "/";
		}
		normalisedXpath = normalisedXpath.substring(0, normalisedXpath.length() - 1);
		return normalisedXpath;
	}

	private static void updateNodeValue(XPath xpath, String normalisedExpression, String newValue) {
		if (newValue.equalsIgnoreCase("$REMOVE$")) {
			normalisedExpression = normalisedExpression.replace("/text()", "");
			NodeList nodes = evaluateXpath(xpath, normalisedExpression);
			for (int i = 0, len = nodes.getLength(); i < len; i++) {
				Node node = nodes.item(i);
				node.getParentNode().removeChild(node);
			}
		} else if (!newValue.equalsIgnoreCase("$IGNORE$")) {
			NodeList nodes = evaluateXpath(xpath, normalisedExpression);
			for (int i = 0, len = nodes.getLength(); i < len; i++) {
				Node node = nodes.item(i);
				node.setTextContent(newValue);
			}
		}
	}

	private static void updateAttributeValue(XPath xpath, String expression, String normalisedExpression,
			String newValue) {
		String attr = expression.substring(expression.indexOf("@") + 1, expression.length());
		normalisedExpression = normalisedExpression.substring(0, normalisedExpression.indexOf("@") - 1);
		NodeList nodes = evaluateXpath(xpath, normalisedExpression);
		for (int i = 0, len = nodes.getLength(); i < len; i++) {
			Node node = nodes.item(i);
			NamedNodeMap attrNode = node.getAttributes();
			Node nodeAttr = attrNode.getNamedItem(attr);
			nodeAttr.setTextContent(newValue);
			if (newValue.equalsIgnoreCase("$REMOVE$"))
				node.getAttributes().removeNamedItem(attr);
		}
	}

	@Override
	public Map<String, String> retrieveMappingAndItsValueToPerformOutputValidation(String filePath) {
		return getValueFromXmlFile(filePath, XmlXpathGeneration.generateXpath(filePath));
	}
}
