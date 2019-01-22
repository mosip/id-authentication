/**
 * 
 */
package io.mosip.kernel.cbeffutil.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.naming.spi.ObjectFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import io.mosip.kernel.cbeffutil.constant.CbeffConstant;
import io.mosip.kernel.cbeffutil.exception.CbeffException;
import io.mosip.kernel.cbeffutil.jaxbclasses.BDBInfoType;
import io.mosip.kernel.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleAnySubtypeType;
import io.mosip.kernel.cbeffutil.jaxbclasses.SingleType;

/**
 * @author Ramadurai Pandian
 * 
 * An Utility Class to validate the data before generating an valid CBEFF XML and to get all
 * the data based on Type and SubType
 *
 */
public class CbeffValidator {

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

	private static long getFormatType(String type) {
		switch (type) {
		case "Finger":
			return CbeffConstant.FORMAT_TYPE_FINGER;
		case "Iris":
			return CbeffConstant.FORMAT_TYPE_IRIS;
		case "FMR":
			return CbeffConstant.FORMAT_TYPE_FINGER_MINUTIAE;
		case "Face":
			return CbeffConstant.FORMAT_TYPE_FACE;
		case "HandGeometry":
			return CbeffConstant.FORMAT_TYPE_FACE;
		}
		return 0;
	}

	public static byte[] createXMLBytes(BIRType bir) throws Exception {
		CbeffValidator.validateXML(bir);
		JAXBContext jaxbContext = JAXBContext.newInstance(BIRType.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(baos);
		jaxbMarshaller.marshal(bir, writer);
		byte[] savedData = baos.toByteArray();
		writer.close();
		return savedData;
	}

	public static BIRType getBIRFromXML(byte[] fileBytes) throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance(BIRType.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		JAXBElement<BIRType> jaxBir = unmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(fileBytes)),
				BIRType.class);
		BIRType bir = jaxBir.getValue();
		return bir;
	}

	public static Map<String, String> getBDBListFromType(SingleType singleType, BIRType bir) throws Exception {
		Map<String, String> BDBmap = new HashMap<>();
		if (bir.getBIR() != null && bir.getBIR().size() > 0) {
			for (BIRType birType : bir.getBIR()) {
				BDBInfoType bdbInfo = birType.getBDBInfo();
				if (bdbInfo != null) {
					List<SingleType> singleTypeList = bdbInfo.getType();
					if (singleTypeList.contains(singleType)) {
						BDBmap.put(singleType.toString(), new String(birType.getBDB(), "UTF-8"));
					}
				}
			}
		}
		return BDBmap;
	}

	public static Map<String, String> getBDBListFromSubType(SingleAnySubtypeType singleAnySubType, BIRType bir)
			throws Exception {
		Map<String, String> BDBmap = new HashMap<>();
		if (bir.getBIR() != null && bir.getBIR().size() > 0) {
			for (BIRType birType : bir.getBIR()) {
				BDBInfoType bdbInfo = birType.getBDBInfo();
				if (bdbInfo != null) {
					List<String> singleTypeList = bdbInfo.getSubtype();
					if (singleTypeList.contains(singleAnySubType.value())) {
						BDBmap.put(singleAnySubType.value(), new String(birType.getBDB(), "UTF-8"));
					}
				}
			}
		}
		return BDBmap;
	}

	public static Map<String, String> getBDBBasedOnTypeAndSubType(BIRType bir, String type, String subType)
			throws Exception {
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
						bdbMap.put(singleType.toString() + " " + String.join(" ", singleSubTypeList) + " "
								+ String.valueOf(bdbFormatType), new String(birType.getBDB(), "UTF-8"));
					} else if (singleType == null && singleSubTypeList.contains(singleAnySubType.value())) {
						List<String> singleTypeStringList = convertToList(singleTypeList);
						bdbMap.put(String.join(" ", singleSubTypeList) + " " + String.join(" ", singleTypeStringList)
								+ " " + String.valueOf(bdbFormatType), new String(birType.getBDB(), "UTF-8"));
					} else if (singleTypeList.contains(singleType)
							&& singleSubTypeList.contains(singleAnySubType != null ? singleAnySubType.value() : null)
							&& formatMatch) {
						bdbMap.put(singleAnySubType.toString() + " " + singleType.toString(),
								new String(birType.getBDB(), "UTF-8"));
					}
				}
			}
		}
		return bdbMap;
	}

	private static List<String> convertToList(List<SingleType> singleTypeList) {
		return singleTypeList.stream().map(Enum::name).collect(Collectors.toList());
	}

	private static SingleAnySubtypeType getSingleAnySubtype(String subType) {
		return subType != null ? SingleAnySubtypeType.fromValue(subType) : null;
	}

	private static SingleType getSingleType(String type) {
		switch (type) {
		case "FMR":
			return SingleType.FINGER;
		default:
			return SingleType.fromValue(type);
		}
	}
}
