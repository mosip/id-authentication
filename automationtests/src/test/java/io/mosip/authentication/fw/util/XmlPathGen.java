package io.mosip.authentication.fw.util; 

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
 
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
 
public class XmlPathGen
{
    public static void main(String[] args) throws Exception
    {
    	XmlPathGen obj = new XmlPathGen();
    	obj.xml();
        
    }
    
    public void xml() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException
    {
    	String xmlFile = "C:/Users/M1049813/git/mosip-test/Dev-ida-qa/mosip-qa/src/test/resources/ida/TestData/UINData/CreateTestData/input/cbeff-bio-data.xml";
        
        //Get DOM
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document xml = db.parse(xmlFile);
 
        //Get XPath
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
    
    }

} 