package io.mosip.registration.processor.core.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.registration.processor.core.constant.PacketFiles;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.FieldValueArray;

@RunWith(SpringRunner.class)
public class IdentityIteratorUtilTest {
	
	@InjectMocks
	IdentityIteratorUtil identityIteratorUtil;
	
	@Test
	public void testGetHashSequence() throws ApisResourceAccessException, IOException {
		List<FieldValueArray> fieldValueArrayList = new ArrayList<FieldValueArray>();
		FieldValueArray applicantBiometric = new FieldValueArray();
		applicantBiometric.setLabel(PacketFiles.APPLICANTBIOMETRICSEQUENCE.name());
		List<String> applicantBiometricValues = new ArrayList<String>();
		applicantBiometricValues.add(PacketFiles.BOTHTHUMBS.name());
		applicantBiometric.setValue(applicantBiometricValues);
		fieldValueArrayList.add(applicantBiometric);
		FieldValueArray introducerBiometric = new FieldValueArray();
		introducerBiometric.setLabel(PacketFiles.INTRODUCERBIOMETRICSEQUENCE.name());
		List<String> introducerBiometricValues = new ArrayList<String>();
		introducerBiometricValues.add("introducerLeftThumb");
		introducerBiometric.setValue(introducerBiometricValues);
		fieldValueArrayList.add(introducerBiometric);
		FieldValueArray applicantDemographic = new FieldValueArray();
		applicantDemographic.setLabel(PacketFiles.APPLICANTDEMOGRAPHICSEQUENCE.name());
		List<String> applicantDemographicValues = new ArrayList<String>();
		applicantDemographicValues.add(PacketFiles.APPLICANTPHOTO.name());
		applicantDemographicValues.add("ProofOfBirth");
		applicantDemographicValues.add("ProofOfRelation");
		applicantDemographicValues.add("ProofOfAddress");
		applicantDemographicValues.add("ProofOfIdentity");
		applicantDemographic.setValue(applicantDemographicValues);
		fieldValueArrayList.add(applicantDemographic);
		List<String> list=identityIteratorUtil.getHashSequence(fieldValueArrayList, PacketFiles.APPLICANTBIOMETRICSEQUENCE.name());
		
		assertEquals(1,list.size());
		
	}
	
	@Test
	public void testGetMetadataLabelValue() throws ApisResourceAccessException, IOException {
		FieldValue registrationType = new FieldValue();
		registrationType.setLabel("registrationType");
		registrationType.setValue("resupdate");

		FieldValue applicantType = new FieldValue();
		applicantType.setLabel("applicantType");
		applicantType.setValue("Child");

		FieldValue isVerified = new FieldValue();
		isVerified.setLabel("isVerified");
		isVerified.setValue("Verified");
		List<FieldValue> list=Arrays.asList(registrationType, applicantType, isVerified);
		String value=identityIteratorUtil.getMetadataLabelValue(list,"registrationType");
		
		assertEquals("resupdate",value);
		
	}
	
	@Test
	public void testGetFieldValue() throws ApisResourceAccessException, IOException {
		FieldValue registrationType = new FieldValue();
		registrationType.setLabel("registrationType");
		registrationType.setValue("resupdate");

		FieldValue applicantType = new FieldValue();
		applicantType.setLabel("applicantType");
		applicantType.setValue("Child");

		FieldValue isVerified = new FieldValue();
		isVerified.setLabel("isVerified");
		isVerified.setValue("Verified");
		List<FieldValue> list=Arrays.asList(registrationType, applicantType, isVerified);
		String value=identityIteratorUtil.getFieldValue(list,"registrationType");
		
		assertEquals("resupdate",value);
		
	}
	
}
