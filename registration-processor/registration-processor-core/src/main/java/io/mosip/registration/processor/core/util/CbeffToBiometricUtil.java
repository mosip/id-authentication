package io.mosip.registration.processor.core.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import io.mosip.kernel.bioapi.impl.BioApiImpl;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;
import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.bioapi.model.KeyValuePair;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.entity.BIRInfo;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.util.exception.BiometricTagMatchException;

/**
 * The Class CbeffToBiometricUtil.
 * 
 * @author M1048358 Alok
 * @author M1030448 Jyoti
 */
public class CbeffToBiometricUtil {

	/** The cbeffutil. */
	private CbeffUtil cbeffutil=new CbeffImpl();
	/**the bioApi */
	private IBioApi bioAPi =  new  BioApiImpl();

	/**
	 * Instantiates a new cbeff to biometric util.
	 *
	 * @param cbeffutil
	 *            the cbeffutil
	 */
	public CbeffToBiometricUtil(CbeffUtil cbeffutil) {
		this.cbeffutil = cbeffutil;
	}
	
	/**
	 * Instantiates  biometric util
	 * 
	 */
	public CbeffToBiometricUtil() {
		
	}

	/**
	 * Gets the photo.
	 *
	 * @param cbeffFileString
	 *            the cbeff file string
	 * @param type
	 *            the type
	 * @param subType
	 *            the sub type
	 * @return the photo
	 * @throws Exception
	 *             the exception
	 */
	public byte[] getImageBytes(String cbeffFileString, String type, List<String> subType) throws Exception {
		byte[] photoBytes = null;
		if (cbeffFileString != null) {
			List<BIRType> bIRTypeList = getBIRTypeList(cbeffFileString);
			photoBytes = getPhotoByTypeAndSubType(bIRTypeList, type, subType);
		}

		return photoBytes;
	}

	/**
	 * Gets the photo by type and sub type.
	 *
	 * @param bIRTypeList
	 *            the b IR type list
	 * @param type
	 *            the type
	 * @param subType
	 *            the sub type
	 * @return the photo by type and sub type
	 */
	private byte[] getPhotoByTypeAndSubType(List<BIRType> bIRTypeList, String type, List<String> subType) {
		byte[] photoBytes = null;
		for (BIRType birType : bIRTypeList) {
			if (birType.getBDBInfo() != null) {
				List<SingleType> singleTypeList = birType.getBDBInfo().getType();
				List<String> subTypeList = birType.getBDBInfo().getSubtype();

				boolean isType = isSingleType(type, singleTypeList);
				boolean isSubType = isSubType(subType, subTypeList);

				if (isType && isSubType) {
					photoBytes = birType.getBDB();
					break;
				}
			}
		}
		return photoBytes;
	}

	/**
	 * Checks if is sub type.
	 *
	 * @param subType
	 *            the sub type
	 * @param subTypeList
	 *            the sub type list
	 * @return true, if is sub type
	 */
	private boolean isSubType(List<String> subType, List<String> subTypeList) {
		return subTypeList.equals(subType) ? Boolean.TRUE : Boolean.FALSE;
	}

	/**
	 * Checks if is single type.
	 *
	 * @param type
	 *            the type
	 * @param singleTypeList
	 *            the single type list
	 * @return true, if is single type
	 */
	private boolean isSingleType(String type, List<SingleType> singleTypeList) {
		boolean isType = false;
		for (SingleType singletype : singleTypeList) {
			if (singletype.value().equalsIgnoreCase(type)) {
				isType = true;
			}
		}
		return isType;
	}

	/**
	 * Merge cbeff.
	 *
	 * @param cbeffFile1
	 *            the cbeff file 1
	 * @param cbeffFile2
	 *            the cbeff file 2
	 * @return the input stream
	 * @throws Exception
	 *             the exception
	 */
	public InputStream mergeCbeff(String cbeffFile1, String cbeffFile2) throws Exception {
		byte[] mergedCbeffByte = null;
		if(!checkFileVersions(cbeffFile1, cbeffFile2)) {
			throw new BiometricTagMatchException(PlatformErrorMessages.RPR_UTL_CBEFF_VERSION_MISMATCH.getCode());
		} 
		else {
			List<BIRType> file1BirTypeList = getBIRTypeList(cbeffFile1);
			List<BIRType> file2BirTypeList = getBIRTypeList(cbeffFile2);

			if (isBiometricTypeSame(file1BirTypeList, file2BirTypeList)) {
				throw new BiometricTagMatchException(PlatformErrorMessages.RPR_UTL_BIOMETRIC_TAG_MATCH.getCode());
			}

			file1BirTypeList.addAll(file2BirTypeList);
			mergedCbeffByte = cbeffutil.createXML(convertBIRTYPEtoBIR(file1BirTypeList));
		}
		return new ByteArrayInputStream(mergedCbeffByte);
	}

	/**
	 * Check file versions.
	 *
	 * @param cbeffFile1
	 *            the cbeff file 1
	 * @param cbeffFile2
	 *            the cbeff file 2
	 * @return true, if successful
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 * @throws SAXException
	 *             the SAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private boolean checkFileVersions(String cbeffFile1, String cbeffFile2)
			throws ParserConfigurationException, SAXException, IOException {
		InputSource source1 = new InputSource();
		source1.setCharacterStream(new StringReader(cbeffFile1));

		InputSource source2 = new InputSource();
		source2.setCharacterStream(new StringReader(cbeffFile2));

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc1 = dBuilder.parse(source1);
		Document doc2 = dBuilder.parse(source2);
		NodeList cbeffVersion1 = doc1.getElementsByTagName("CBEFFVersion");
		NodeList cbeffVersion2 = doc2.getElementsByTagName("CBEFFVersion");

		String version1 = cbeffVersion1.item(0).getChildNodes().item(0).getNextSibling().getTextContent();
		String version2 = cbeffVersion2.item(0).getChildNodes().item(0).getNextSibling().getTextContent();

		return version1.equals(version2) ? Boolean.TRUE : Boolean.FALSE;
	}

	/**
	 * Checks if is same type.
	 *
	 * @param file1BirTypeList
	 *            the file 1 bir type list
	 * @param file2BirTypeList
	 *            the file 2 bir type list
	 * @return true, if is same type
	 */
	private boolean isBiometricTypeSame(List<BIRType> file1BirTypeList, List<BIRType> file2BirTypeList) {
		boolean isTypeSame = false;
		for (BIRType bir1 : file1BirTypeList) {
			List<SingleType> singleTypeList1 = bir1.getBDBInfo().getType();
			for (BIRType bir2 : file2BirTypeList) {
				List<SingleType> singleTypeList2 = bir2.getBDBInfo().getType();
				if (singleTypeList1.equals(singleTypeList2)) {
					isTypeSame = true;
					break;
				}
			}
			if (isTypeSame)
				break;
		}
		return isTypeSame;
	}

	/**
	 * Extract cbeff with types.
	 *
	 * @param cbeffFile
	 *            the cbeff file
	 * @param types
	 *            the types
	 * @return the input stream
	 * @throws Exception
	 *             the exception
	 */
	public InputStream extractCbeffWithTypes(String cbeffFile, List<String> types) throws Exception {
		List<BIRType> extractedType = new ArrayList<>();
		byte[] newCbeffByte = null;
		List<BIRType> file2BirTypeList = getBIRTypeList(cbeffFile);
		for (BIRType birType : file2BirTypeList) {
			List<SingleType> singleTypeList = birType.getBDBInfo().getType();
			for (String type : types) {
				if (singleTypeList.get(0).value().equalsIgnoreCase(type)) {
					extractedType.add(birType);
				}
			}
		}
		if (!extractedType.isEmpty()) {
			newCbeffByte = cbeffutil.createXML(convertBIRTYPEtoBIR(extractedType));
		} else {
			return null;
		}

		return new ByteArrayInputStream(newCbeffByte);
	}

	/**
	 * Convert BIRTYP eto BIR.
	 *
	 * @param listOfBIR
	 *            the list of BIR
	 * @return the list
	 */
	public List<BIR> convertBIRTYPEtoBIR(List<BIRType> listOfBIR) {
		return listOfBIR.parallelStream().map(bir -> new BIR.BIRBuilder().withBdb(bir.getBDB())
				.withBirInfo(new BIRInfo.BIRInfoBuilder().withIntegrity(false).build())
				.withBdbInfo(Optional.ofNullable(bir.getBDBInfo())
						.map(bdbInfo -> new BDBInfo.BDBInfoBuilder().withIndex(bdbInfo.getIndex())
								.withFormatOwner(bdbInfo.getFormatOwner()).withFormatType(bdbInfo.getFormatType())
								.withQuality(bdbInfo.getQuality()).withType(bdbInfo.getType())
								.withSubtype(bdbInfo.getSubtype()).withPurpose(bdbInfo.getPurpose())
								.withLevel(bdbInfo.getLevel()).withCreationDate(bdbInfo.getCreationDate()).build())
						.orElseGet(() -> null))
				.build()).collect(Collectors.toList());
	}

	/**
	 * Gets the BIR type list.
	 *
	 * @param cbeffFileString
	 *            the cbeff file string
	 * @return the BIR type list
	 * @throws Exception
	 *             the exception
	 */
	public List<BIRType> getBIRTypeList(String cbeffFileString) throws Exception {
		return cbeffutil.getBIRDataFromXML(CryptoUtil.decodeBase64(cbeffFileString));
	}
	/**
	 * Gets the BIR type list.
	 * @param xmlBytes byte array of XML data
	 * @return the BIR type list
	 * @throws Exception
	 *             the exception
	 */
	public List<BIRType> getBIRDataFromXML(byte[] xmlBytes) throws Exception {
		return cbeffutil.getBIRDataFromXML(xmlBytes);
	}

	/**
	 * Gets the BIR template
	 * @param sample the sample
	 * @param flags the flags
	 * @return the biometric record
	 * @throws BiometricException 
	 */
	public BIR extractTemplate(BIR sample, KeyValuePair[] flags) throws BiometricException {
		return bioAPi.extractTemplate(sample, flags);
	}

}
