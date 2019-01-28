/**
 * 
 */
package io.mosip.registration.util.kernal.cbeff.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import io.mosip.registration.dto.cbeff.jaxbclasses.BDBInfoType;
import io.mosip.registration.dto.cbeff.jaxbclasses.BIRType;
import io.mosip.registration.dto.cbeff.jaxbclasses.SingleAnySubtypeType;
import io.mosip.registration.dto.cbeff.jaxbclasses.SingleType;
import io.mosip.registration.util.kernal.cbeff.constant.CbeffConstant;
import io.mosip.registration.util.kernal.cbeff.exception.CbeffException;

/**
 * @author Ramadurai Pandian
 *
 */
public class CbeffValidator {
	
	public static boolean validateXML(BIRType bir) throws CbeffException
	{
		if(bir==null)
		{
			throw new CbeffException("BIR value is null");
		}
		List<BIRType> birList = bir.getBIR();
		for(BIRType birType:birList)
		{
			if(birType!=null)
			{
				if(birType.getBDB().length<0)
				{
					throw new CbeffException("BDB value can't be empty");
				}
				if(birType.getBDBInfo()!=null)
				{
					BDBInfoType bdbInfo = birType.getBDBInfo();
					if(!bdbInfo.getFormatOwner().equals(CbeffConstant.ISO_FORMAT_OWNER))
					{
						throw new CbeffException("Patron Format Owner should be standard specified of value "+CbeffConstant.ISO_FORMAT_OWNER);
					}
					List<SingleType> singleTypeList =bdbInfo.getType();
					if(singleTypeList==null || singleTypeList.isEmpty())
					{
						throw new CbeffException("Type value needs to be provided");
					}
					if(!validateFormatType(bdbInfo.getFormatType(),singleTypeList))
					{
						throw new CbeffException("Patron Format type is invalid");
					}
				}
				else
				{
					throw new CbeffException("BDB information can't be empty");
				}
			}
		}
		return false;
		
	}

	private static boolean validateFormatType(long formatType, List<SingleType> singleTypeList) {
		SingleType singleType = singleTypeList.get(0);
		switch(singleType.value()){
		case "Finger" :
			return formatType==CbeffConstant.FORMAT_TYPE_FINGER || formatType==CbeffConstant.FORMAT_TYPE_FINGER_MINUTIAE;
		case "Iris" :
			return formatType==CbeffConstant.FORMAT_TYPE_IRIS;
		case "Face" :
			return formatType==CbeffConstant.FORMAT_TYPE_FACE;
		case "HandGeometry" :
			return formatType==CbeffConstant.FORMAT_TYPE_FACE;
		}
			
		return false;
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
		JAXBElement<BIRType> jaxBir = unmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(fileBytes)), BIRType.class);
		BIRType bir = jaxBir.getValue();
		return bir;
	}

	public static List<String> getBDBListFromType(SingleType singleType, BIRType bir) throws Exception {
		List<String> BDBList = new ArrayList<>();
		if(bir.getBIR()!=null && bir.getBIR().size()>0)
		{
			for(BIRType birType : bir.getBIR())
			{
				BDBInfoType bdbInfo = birType.getBDBInfo();
				if(bdbInfo!=null)
				{
					List<SingleType> singleTypeList = bdbInfo.getType();
					if(singleTypeList.contains(singleType))
					{
						BDBList.add(new String(birType.getBDB(),"UTF-8"));
					}
				}
			}
		}
		return BDBList;
	}

	public static List<String> getBDBListFromSubType(SingleAnySubtypeType singleAnySubType, BIRType bir) throws Exception {
		List<String> BDBList = new ArrayList<>();
		if(bir.getBIR()!=null && bir.getBIR().size()>0)
		{
			for(BIRType birType : bir.getBIR())
			{
				BDBInfoType bdbInfo = birType.getBDBInfo();
				if(bdbInfo!=null)
				{
					List<String> singleTypeList = bdbInfo.getSubtype();
					if(singleTypeList.contains(singleAnySubType.value()))
					{
						BDBList.add(new String(birType.getBDB(),"UTF-8"));
					}
				}
			}
		}
		return BDBList;
	}
}
