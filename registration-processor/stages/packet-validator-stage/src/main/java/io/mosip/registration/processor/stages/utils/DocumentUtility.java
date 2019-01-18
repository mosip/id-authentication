/**
 * 
 */
package io.mosip.registration.processor.stages.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.packet.dto.Document;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.packet.storage.exception.IdentityNotFoundException;
import io.mosip.registration.processor.packet.storage.utils.Utilities;

/**
 * @author M1022006
 *
 */
@Component
public class DocumentUtility {

	/** The reg processor identity json. */
	@Autowired
	private RegistrationProcessorIdentity regProcessorIdentityJson;

	/** The demographic identity. */
	private JSONObject demographicIdentity = null;

	/** The Constant LANGUAGE. */
	private static final String FORMAT = "format";

	/** The Constant LABEL. */
	private static final String TYPE = "type";

	/** The Constant VALUE. */
	private static final String VALUE = "value";

	@Autowired
	private Utilities utility;

	public List<Document> getDocumentList(byte[] bytes) throws IOException, ParseException {

		List<Document> documentList = new ArrayList<>();
		JSONObject documentPOAnode = null;
		JSONObject documentPOInode = null;
		JSONObject documentPORnode = null;
		JSONObject documentPOBnode = null;

		String demographicJsonString = new String(bytes);
		String getIdentityJsonString = Utilities.getJson(utility.getConfigServerFileStorageURL(),
				utility.getGetRegProcessorIdentityJson());
		ObjectMapper mapIdentityJsonStringToObject = new ObjectMapper();

		regProcessorIdentityJson = mapIdentityJsonStringToObject.readValue(getIdentityJsonString,
				RegistrationProcessorIdentity.class);
		JSONParser parser = new JSONParser();
		JSONObject demographicJson = (JSONObject) parser.parse(demographicJsonString);
		String poAValue = regProcessorIdentityJson.getIdentity().getPoa().getValue();
		String poIValue = regProcessorIdentityJson.getIdentity().getPoi().getValue();
		String poRValue = regProcessorIdentityJson.getIdentity().getPor().getValue();
		String poBValue = regProcessorIdentityJson.getIdentity().getPob().getValue();
		demographicIdentity = (JSONObject) demographicJson.get(utility.getGetRegProcessorDemographicIdentity());
		if (demographicIdentity == null)
			throw new IdentityNotFoundException(PlatformErrorMessages.RPR_PIS_IDENTITY_NOT_FOUND.getMessage());
		documentPOAnode = getJsonObject(poAValue);
		documentPOInode = getJsonObject(poIValue);
		documentPORnode = getJsonObject(poRValue);
		documentPOBnode = getJsonObject(poBValue);

		if (documentPOAnode != null) {
			documentList.add(getDocument(documentPOAnode, poAValue));
		}
		if (documentPOInode != null) {
			documentList.add(getDocument(documentPOInode, poIValue));
		}
		if (documentPORnode != null) {
			documentList.add(getDocument(documentPORnode, poRValue));
		}
		if (documentPOBnode != null) {
			documentList.add(getDocument(documentPOBnode, poBValue));
		}
		return documentList;

	}

	private Document getDocument(JSONObject jsonNode, String category) {
		Document document = new Document();
		document.setDocumentCategory(category);
		document.setDocumentType((String) jsonNode.get(TYPE));
		document.setFormat((String) jsonNode.get(FORMAT));
		document.setDocumentName((String) jsonNode.get(VALUE));
		return document;
	}

	public JSONObject getJsonObject(Object identityKey) {
		JSONObject demographicJsonNode = null;
		if (demographicIdentity != null)
			demographicJsonNode = (JSONObject) demographicIdentity.get(identityKey);
		return (demographicJsonNode != null) ? demographicJsonNode : null;

	}

	public boolean checkSum(String registrationId) {
		boolean result = true;
		if (registrationId == null)
			result = false;

		return result;
	}

}
