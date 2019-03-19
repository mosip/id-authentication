package io.mosip.registration.processor.stages.osivalidator.utils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.fsadapter.spi.FileSystemAdapter;
import io.mosip.registration.processor.core.constant.JsonConstant;
import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.Identity;
import io.mosip.registration.processor.core.packet.dto.PacketMetaInfo;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.util.IdentityIteratorUtil;
import io.mosip.registration.processor.core.util.JsonUtil;

@Service
public class OSIUtils {

	/** The adapter. */
	@Autowired
	private FileSystemAdapter adapter;
	
	private IdentityIteratorUtil identityIteratorUtil = new IdentityIteratorUtil();
	
	public RegOsiDto getOSIDetailsFromMetaInfo(String registrationId,Identity identity) throws UnsupportedEncodingException {
		 
		RegOsiDto regOsi = new RegOsiDto();
		//regOsi.setIntroducerFingerpImageName(identity.getBiometric().getIntroducer().getIntroducerFingerprint().getImageName());
		regOsi.setIntroducerFingerpType(getMetaDataValue(JsonConstant.INTRODUCERFINGERPRINTTYPE,identity));
		regOsi.setIntroducerId("");// not found in json
		//regOsi.setIntroducerIrisImageName(identity.getBiometric().getIntroducer().getIntroducerIris().getImageName());
		regOsi.setIntroducerIrisType(getMetaDataValue(JsonConstant.INTRODUCERIRISTYPE,identity));
		//regOsi.setIntroducerPhotoName(identity.getBiometric().getIntroducer().getIntroducerImage().getImageName());
		regOsi.setIntroducerRegId(getMetaDataValue(JsonConstant.INTRODUCERRID,identity));
		regOsi.setIntroducerTyp(getMetaDataValue(JsonConstant.INTRODUCERTYPE,identity));
		regOsi.setIntroducerUin(getMetaDataValue(JsonConstant.INTRODUCERUIN,identity));
		regOsi.setOfficerFingerpImageName(getOsiDataValue(JsonConstant.OFFICERFINGERPRINTIMAGE,identity));
		regOsi.setOfficerfingerType(getMetaDataValue(JsonConstant.OFFICERFINGERPRINTTYPE,identity));
		regOsi.setOfficerHashedPin(getMetaDataValue(JsonConstant.OFFICERPIN,identity));
		regOsi.setOfficerHashedPwd(getOsiDataValue(JsonConstant.OFFICERPWR,identity));
		regOsi.setOfficerId(getOsiDataValue(JsonConstant.OFFICERID,identity));
		//regOsi.setOfficerIrisImageName("");  // not found in json
		regOsi.setOfficerIrisType(getMetaDataValue(JsonConstant.OFFICERIRISTYPE,identity));
		regOsi.setOfficerPhotoName(getOsiDataValue(JsonConstant.OFFICERPHOTONAME,identity));
		regOsi.setOfficerOTPAuthentication(getOsiDataValue(JsonConstant.OFFICEROTPAUTHENTICATION,identity));
		regOsi.setPreregId(getMetaDataValue(JsonConstant.PREREGISTRATIONID,identity));
		regOsi.setRegId(getMetaDataValue(JsonConstant.REGISTRATIONID,identity));
		//regOsi.setSupervisorFingerpImageName(""); // not found in json
		regOsi.setSupervisorBiometricFileName(getOsiDataValue(JsonConstant.SUPERVISORBIOMETRICFILENAME,identity));
		regOsi.setSupervisorFingerType(getMetaDataValue(JsonConstant.SUPERVISORFINGERPRINTTYPE,identity));
		regOsi.setSupervisorHashedPin(getOsiDataValue(JsonConstant.OFFICERPHOTONAME,identity));
		regOsi.setSupervisorHashedPwd(getOsiDataValue(JsonConstant.SUPERVISORPWR,identity));
		regOsi.setSupervisorId(getOsiDataValue(JsonConstant.SUPERVISORID,identity));
		regOsi.setSupervisorIrisImageName(""); // not found in json
		regOsi.setSupervisorIrisType(getOsiDataValue(JsonConstant.SUPERVISORIRISTYPE,identity));
		regOsi.setSupervisorPhotoName("");  // not found in json
		regOsi.setSupervisorOTPAuthentication(getOsiDataValue(JsonConstant.SUPERVISOROTPAUTHENTICATION,identity));
		
		return regOsi;
	}
	
	public String getOsiDataValue(String label,Identity identity) throws UnsupportedEncodingException {
		List<FieldValue> osiData = identity.getOsiData();
		return identityIteratorUtil.getMetadataLabelValue(osiData, label);

	}
	public String getMetaDataValue(String label,Identity identity) throws UnsupportedEncodingException {
		List<FieldValue> metadata = identity.getMetaData();
		return identityIteratorUtil.getMetadataLabelValue(metadata, label);

	}
	
	public Identity getIdentity(String registrationId) throws UnsupportedEncodingException {
		InputStream packetMetaInfoStream = adapter.getFile(registrationId, PacketFiles.PACKET_META_INFO.name());
		PacketMetaInfo packetMetaInfo = (PacketMetaInfo) JsonUtil.inputStreamtoJavaObject(packetMetaInfoStream,PacketMetaInfo.class);
		return packetMetaInfo.getIdentity();

	}

}
