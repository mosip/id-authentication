package io.mosip.authentication.service;

import org.json.JSONException;
import org.json.JSONObject;

public class TestJson {

	public static void main(String[] args) {
		String res = "{\"id\":\"mosip.id.read\",\"version\":\"1.0\",\"timestamp\":\"2019-03-07T10:13:09.422Z\",\"status\":\"ACTIVATED\",\"response\":{\"identity\":{\"IDSchemaVersion\":1.0,\"fullName\":[{\"language\":\"ara\",\"value\":\"\u0627\u0628\u0631\u0627\u0647\u064A\u0645 \u0628\u0646 \u0639\u0644\u064A\"},{\"language\":\"fre\",\"value\":\"Ibrahim Ibn Ali\"}],\"dateOfBirth\":\"\",\"age\":45,\"gender\":[{\"language\":\"ara\",\"value\":\"\u0627\u0644\u0630\u0643\u0631\"},{\"language\":\"fre\",\"value\":\"m\u00E2le\"}],\"addressLine1\":[{\"language\":\"ara\",\"value\":\"\u0639\u0646\u0648\u0627\u0646 \u0627\u0644\u0639\u064A\u0646\u0629 \u0633\u0637\u0631 1\"},{\"language\":\"fre\",\"value\":\"exemple d'adresse ligne 1\"}],\"addressLine2\":[{\"language\":\"ara\",\"value\":\"\u0639\u0646\u0648\u0627\u0646 \u0627\u0644\u0639\u064A\u0646\u0629 \u0633\u0637\u0631 2\"},{\"language\":\"fre\",\"value\":\"exemple d'adresse ligne 2\"}],\"addressLine3\":[{\"language\":\"ara\",\"value\":\"\u0639\u0646\u0648\u0627\u0646 \u0627\u0644\u0639\u064A\u0646\u0629 \u0633\u0637\u0631 2\"},{\"language\":\"fre\",\"value\":\"exemple d'adresse ligne 2\"}],\"region\":[{\"language\":\"ara\",\"value\":\"\u0637\u0646\u062C\u0629 - \u062A\u0637\u0648\u0627\u0646 - \u0627\u0644\u062D\u0633\u064A\u0645\u0629\"},{\"language\":\"fre\",\"value\":\"Tanger-T\u00E9touan-Al Hoceima\"}],\"province\":[{\"language\":\"ara\",\"value\":\"\u0641\u0627\u0633-\u0645\u0643\u0646\u0627\u0633\"},{\"language\":\"fre\",\"value\":\"F\u00E8s-Mekn\u00E8s\"}],\"city\":[{\"language\":\"ara\",\"value\":\"\u0627\u0644\u062F\u0627\u0631 \u0627\u0644\u0628\u064A\u0636\u0627\u0621\"},{\"language\":\"fre\",\"value\":\"Casablanca\"}],\"postalCode\":\"570004\",\"phone\":\"9663175928\",\"email\":\"ramaduraipandian.s@gmail.com\",\"CNIENumber\":\"6789545678909\",\"localAdministrativeAuthority\":[{\"language\":\"ara\",\"value\":\"\u0633\u0644\u0645\u0649\"},{\"language\":\"fre\",\"value\":\"salma\"}],\"parentOrGuardianRIDOrUIN\":212124324784912,\"parentOrGuardianName\":[{\"language\":\"ara\",\"value\":\"\u0633\u0644\u0645\u0649\"},{\"language\":\"fre\",\"value\":\"salma\"}],\"proofOfAddress\":{\"format\":\"pdf\",\"type\":\"drivingLicense\",\"value\":\"fileReferenceID\"},\"proofOfIdentity\":{\"format\":\"txt\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfRelationship\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"proofOfDateOfBirth\":{\"format\":\"pdf\",\"type\":\"passport\",\"value\":\"fileReferenceID\"},\"individualBiometrics\":{\"format\":\"cbeff\",\"version\":1.0,\"value\":\"fileReferenceID\"},\"parentOrGuardianBiometrics\":{\"format\":\"cbeff1\",\"version\":1.1,\"value\":\"fileReferenceID\"}}}}";
		JSONObject json;
		try {
			json = new JSONObject(res);
			System.out.println(json.get("Identity"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
