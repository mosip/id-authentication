/**
 * 
 */
package io.mosip.kernel.cbeffutil.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

import io.mosip.kernel.cbeffutil.constant.CbeffConstant;
import io.mosip.kernel.cbeffutil.exception.CbeffException;
import io.mosip.kernel.cbeffutil.jaxbclasses.BDBInfoType;
import io.mosip.kernel.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.util.CryptoUtil;

/**
 * @author Ramadurai Pandian
 * 
 * An Utility Class to validate the data before generating an valid CBEFF XML and to get all
 * the data based on Type and SubType
 *
 */
public class CbeffValidator {
	
	private static final String tempPath = "./src/main/resources";
	
	/**
	 * Method used for custom validation of the BIR
	 * 
	 * @param bir BIR data
	 * 
	 * @return boolean value if BIR is valid
	 * 
	 * @exception CbeffException when any condition fails
	 * 
	 */
	public static boolean validateXML(BIRType bir) throws CbeffException {
		if (bir == null) {
			throw new CbeffException("BIR value is null");
		}
		List<BIRType> birList = bir.getBIR();
		for (BIRType birType : birList) {
			if (birType != null) {
				if (birType.getBDB().length < 0) {
					throw new CbeffException("BDB value can't be empty");
				}
				if (birType.getBDBInfo() != null) {
					BDBInfoType bdbInfo = birType.getBDBInfo();
					if (!bdbInfo.getFormatOwner().equals(CbeffConstant.FORMAT_OWNER)) {
						throw new CbeffException("Patron Format Owner should be standard specified of value "
								+ CbeffConstant.FORMAT_OWNER);
					}
					List<SingleType> singleTypeList = bdbInfo.getType();
					if (singleTypeList == null || singleTypeList.isEmpty()) {
						throw new CbeffException("Type value needs to be provided");
					}
					if (!validateFormatType(bdbInfo.getFormatType(), singleTypeList)) {
						throw new CbeffException("Patron Format type is invalid");
					}
				} else {
					throw new CbeffException("BDB information can't be empty");
				}
			}
		}
		return false;

	}

	/**
	 * Method used for validation of Format Type
	 * 
	 * @param formatType format type
	 * 
	 * @param singleTypeList List of types
	 * 
	 * @return boolean value if format type is matching with type
	 * 
	 */
	private static boolean validateFormatType(long formatType, List<SingleType> singleTypeList) {
		SingleType singleType = singleTypeList.get(0);
		switch (singleType.value()) {
		case "Finger":
			return formatType == CbeffConstant.FORMAT_TYPE_FINGER
					|| formatType == CbeffConstant.FORMAT_TYPE_FINGER_MINUTIAE;
		case "Iris":
			return formatType == CbeffConstant.FORMAT_TYPE_IRIS;
		case "Face":
			return formatType == CbeffConstant.FORMAT_TYPE_FACE;
		case "HandGeometry":
			return formatType == CbeffConstant.FORMAT_TYPE_FACE;
		}

		return false;
	}

	/**
	 * Method used for getting Format Type Id from type string
	 * 
	 * @param type format type
	 * 
	 * @return long format type id
	 * 
	 */
	private static long getFormatType(String type) {
		switch (type.toLowerCase()) {
		case "finger":
			return CbeffConstant.FORMAT_TYPE_FINGER;
		case "iris":
			return CbeffConstant.FORMAT_TYPE_IRIS;
		case "fmr":
			return CbeffConstant.FORMAT_TYPE_FINGER_MINUTIAE;
		case "face":
			return CbeffConstant.FORMAT_TYPE_FACE;
		case "handgeometry":
			return CbeffConstant.FORMAT_TYPE_FACE;
		}
		return 0;
	}

	/**
	 * Method used for creating XML bytes using JAXB
	 * 
	 * @param bir BIR type
	 * 
	 * @return byte[] byte array of XML data
	 * 
	 * @exception Exception exception
	 * 
	 */
	public static byte[] createXMLBytes(BIRType bir,byte[] xsd) throws Exception {
		CbeffValidator.validateXML(bir);
		JAXBContext jaxbContext = JAXBContext.newInstance(BIRType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(baos);
		jaxbMarshaller.marshal(bir, writer);
		byte[] savedData = baos.toByteArray();
		writer.close();
		try
		{
		CbeffXSDValidator.validateXML(xsd, savedData);
		}
		catch(SAXException sax)
		{
			String message = sax.getMessage();
			message = message.substring(message.indexOf(":"));
			throw new CbeffException("XSD validation failed due to attribute "+message);
		}
		return savedData;
	}
	
	private static byte[] readXSD(String name) throws IOException {
		byte[] fileContent = Files.readAllBytes(Paths.get(tempPath + "/schema/" + name + ".xsd"));
		return fileContent;
	}

	/**
	 * Method used for BIR Type
	 * 
	 * @param fileBytes byte array of XML data
	 * 
	 * @return BIRType BIR data
	 * 
	 * @exception Exception exception
	 * 
	 */
	public static BIRType getBIRFromXML(byte[] fileBytes) throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(BIRType.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		JAXBElement<BIRType> jaxBir = unmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(fileBytes)),
				BIRType.class);
		BIRType bir = jaxBir.getValue();
		return bir;
	}

	/**
	 * Method used for searching Cbeff data based on type and subtype
	 * 
	 * @param bir BIR data
	 * 
	 * @param type format type
	 * 
	 * @param subType format subtype
	 * 
	 * @return bdbMap
	 * 
	 * @exception Exception exception
	 * 
	 */
	public static Map<String, String> getBDBBasedOnTypeAndSubType(BIRType bir, String type, String subType)
			throws Exception {
		
		if(type==null && subType==null)
		{
			return getAllLatestDatafromBIR(bir);
		}
		SingleType singleType = null;
		SingleAnySubtypeType singleAnySubType = null;
		Long formatType = null;
		if (type != null) {
			singleType = getSingleType(type);
		}
		if (subType != null) {
			singleAnySubType = getSingleAnySubtype(subType);
		}
		if (type != null) {
			formatType = getFormatType(type);
		}
		Map<String, String> bdbMap = new HashMap<>();
		if (bir.getBIR() != null && bir.getBIR().size() > 0) {
			for (BIRType birType : bir.getBIR()) {
				BDBInfoType bdbInfo = birType.getBDBInfo();

				if (bdbInfo != null) {
					List<String> singleSubTypeList = bdbInfo.getSubtype();
					List<SingleType> singleTypeList = bdbInfo.getType();
					Long bdbFormatType = bdbInfo.getFormatType();
					boolean formatMatch = bdbFormatType.equals(formatType);
					if (singleAnySubType == null && singleTypeList.contains(singleType) && formatMatch) {
						bdbMap.put(singleType.toString() + "_" + String.join(" ", singleSubTypeList) + "_"
								+ String.valueOf(bdbFormatType)+"_"+bdbInfo.getCreationDate().getTime(), CryptoUtil.encodeBase64String(birType.getBDB()));
					} else if (singleType == null && singleSubTypeList.contains(singleAnySubType.value())) {
						List<String> singleTypeStringList = convertToList(singleTypeList);
						bdbMap.put(String.join(" ", singleTypeStringList)+"_"+String.join(" ", singleSubTypeList)
								+ "_" + String.valueOf(bdbFormatType)+"_"+bdbInfo.getCreationDate().getTime(), CryptoUtil.encodeBase64String(birType.getBDB()));
					} else if (singleTypeList.contains(singleType)
							&& singleSubTypeList.contains(singleAnySubType != null ? singleAnySubType.value() : null)
							&& formatMatch) {
						bdbMap.put(singleType.toString()+"_"+singleAnySubType.value()+ "_" + String.valueOf(bdbFormatType)+ "_"+bdbInfo.getCreationDate().getTime(),
								CryptoUtil.encodeBase64String(birType.getBDB()));
					}
				}
			}
		}
		Map<String, String> map = new TreeMap<>(bdbMap);
		Map<String,String> finalMap = new HashMap<>();
		for(Map.Entry<String, String> mapEntry :map.entrySet())
		{
			String pattern = mapEntry.getKey().substring(0, mapEntry.getKey().lastIndexOf("_"));
			if(mapEntry.getKey().contains(pattern))
			{
				finalMap.put( mapEntry.getKey().substring(0, mapEntry.getKey().lastIndexOf("_")), mapEntry.getValue());
			}
		}
		return finalMap;
	}

	private static Map<String, String> getAllLatestDatafromBIR(BIRType bir) throws Exception {
		Map<String, String> bdbMap = new HashMap<>();
		if (bir.getBIR() != null && bir.getBIR().size() > 0) {
			for (BIRType birType : bir.getBIR()) {
				BDBInfoType bdbInfo = birType.getBDBInfo();

				if (bdbInfo != null) {
					List<String> singleSubTypeList = bdbInfo.getSubtype();
					List<SingleType> singleTypeList = bdbInfo.getType();
					if(singleSubTypeList.isEmpty())
					{
						singleSubTypeList = new ArrayList<>();
						singleSubTypeList.add("No Subtype");
					}
					Long bdbFormatType = bdbInfo.getFormatType();
					bdbMap.put(String.join(" ", singleTypeList.get(0).toString())+"_"+String.join(" ", singleSubTypeList)+ "_" + String.valueOf(bdbFormatType)+ "_"+bdbInfo.getCreationDate().getTime(),
							CryptoUtil.encodeBase64String(birType.getBDB()));
				}
			}
			}
		Map<String, String> map = new TreeMap<>(bdbMap);
		Map<String,String> finalMap = new HashMap<>();
		for(Map.Entry<String, String> mapEntry :map.entrySet())
		{
			String pattern = mapEntry.getKey().substring(0, mapEntry.getKey().lastIndexOf("_"));
			if(mapEntry.getKey().contains(pattern))
			{
				finalMap.put( mapEntry.getKey().substring(0, mapEntry.getKey().lastIndexOf("_")), mapEntry.getValue());
			}
		}
		return finalMap;
	}

	/**
	 * Method to convert single type list to string
	 * 
	 * */
	private static List<String> convertToList(List<SingleType> singleTypeList) {
		return singleTypeList.stream().map(Enum::name).collect(Collectors.toList());
	}

	/**
	 * Method to get enum sub type from string subtype
	 * 
	 * */
	private static SingleAnySubtypeType getSingleAnySubtype(String subType) {
		return subType != null ? SingleAnySubtypeType.fromValue(subType) : null;
	}

	/**
	 * Method to get enum type from string type
	 * 
	 * */
	private static SingleType getSingleType(String type) {
		if(isInEnum(type,SingleType.class))
		{
			return SingleType.valueOf(type);
		}
		else
		{
		switch (type) {
		case "FMR":
			return SingleType.FINGER;
		default:
			return SingleType.fromValue(type);
		}
		}
	}

	public static <E extends Enum<E>> boolean isInEnum(String value, Class<E> enumClass) {
		  for (E e : enumClass.getEnumConstants()) {
		    if(e.name().equals(value)) { return true; }
		  }
		  return false;
		}
	public static Map<String, String> getAllBDBData(BIRType bir, String type, String subType) throws Exception {
		SingleType singleType = null;
		SingleAnySubtypeType singleAnySubType = null;
		Long formatType = null;
		if (type != null) {
			singleType = getSingleType(type);
		}
		if (subType != null) {
			singleAnySubType = getSingleAnySubtype(subType);
		}
		if (type != null) {
			formatType = getFormatType(type);
		}
		Map<String, String> bdbMap = new HashMap<>();
		if (bir.getBIR() != null && bir.getBIR().size() > 0) {
			for (BIRType birType : bir.getBIR()) {
				BDBInfoType bdbInfo = birType.getBDBInfo();

				if (bdbInfo != null) {
					List<String> singleSubTypeList = bdbInfo.getSubtype();
					List<SingleType> singleTypeList = bdbInfo.getType();
					Long bdbFormatType = bdbInfo.getFormatType();
					boolean formatMatch = bdbFormatType.equals(formatType);
					if (singleAnySubType == null && singleTypeList.contains(singleType) && formatMatch) {
						bdbMap.put(singleType.toString() + "_" + String.join(" ", singleSubTypeList) + "_"
								+ String.valueOf(bdbFormatType)+"_"+bdbInfo.getCreationDate().getTime(), new String(birType.getBDB(), "UTF-8"));
					} else if (singleType == null && singleSubTypeList.contains(singleAnySubType.value())) {
						List<String> singleTypeStringList = convertToList(singleTypeList);
						bdbMap.put(String.join(" ", singleSubTypeList) + "_" + String.join(" ", singleTypeStringList)
								+ "_" + String.valueOf(bdbFormatType)+"_"+bdbInfo.getCreationDate().getTime(), new String(birType.getBDB(), "UTF-8"));
					} else if (singleTypeList.contains(singleType)
							&& singleSubTypeList.contains(singleAnySubType != null ? singleAnySubType.value() : null)
							&& formatMatch) {
						bdbMap.put(singleAnySubType.toString() + "_" + singleType.toString()+ "_" + String.valueOf(bdbFormatType)+ "_"+bdbInfo.getCreationDate().getTime(),
								new String(birType.getBDB(), "UTF-8"));
					}
				}
			}
		}
		return bdbMap;
	}
}
