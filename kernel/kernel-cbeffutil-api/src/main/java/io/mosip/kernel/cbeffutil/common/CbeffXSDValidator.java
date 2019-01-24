/**
 * 
 */
package io.mosip.kernel.cbeffutil.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

/**
 * @author M1049825
 *
 */
public class CbeffXSDValidator {
	
	public static boolean validateXMLSchema(String xsdPath, byte[] xmlByte) throws IOException {
		FileOutputStream fos = null;
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			File tempFile = new File("C://Users/M1049825/Documents/img/cbeffupdate.xml");
			fos = new FileOutputStream(tempFile);
			fos.write(xmlByte);
			Schema schema = factory.newSchema(new File(xsdPath));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(tempFile));
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			return false;
		}
		finally
		{
			fos.close();
		}
		return true;
	}
	
	public static boolean validateXML(byte[] xsdBytes, byte[] xmlBytes) throws Exception {
		try
		{
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new StreamSource(new ByteArrayInputStream(xsdBytes)));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new ByteArrayInputStream(xmlBytes)));
			return true;
		}catch(Exception ex)
		{
			System.out.println(ex);
			return false;
		}
	}

}
