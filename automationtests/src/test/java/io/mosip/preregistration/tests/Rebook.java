package io.mosip.preregistration.tests;

import java.util.ArrayList;
import java.util.List;

import io.restassured.response.Response;

public class Rebook {
	public List<String> reBookGetAppointmentDetails(Response fetchCenterResponse, String date) {

		List<String> appointmentDetails = new ArrayList<>();

		int countCenterDetails = fetchCenterResponse.jsonPath().getList("response.centerDetails").size();
		for (int i = 0; i < countCenterDetails; i++) {
			if (fetchCenterResponse.jsonPath().get("response.centerDetails[0].date").toString() == date) {
				try {
					fetchCenterResponse.jsonPath().get("response.centerDetails[" + i + "].timeSlots[0].fromTime")
							.toString();
				} catch (NullPointerException e) {
					continue;
				}

			}
			try {
				fetchCenterResponse.jsonPath().get("response.centerDetails[" + i + "].timeSlots[0].fromTime")
						.toString();
			} catch (NullPointerException e) {
				continue;
			}
			appointmentDetails.add(fetchCenterResponse.jsonPath().get("response.regCenterId").toString());
			appointmentDetails
					.add(fetchCenterResponse.jsonPath().get("response.centerDetails[" + i + "].date").toString());
			appointmentDetails.add(fetchCenterResponse.jsonPath()
					.get("response.centerDetails[" + i + "].timeSlots[0].fromTime").toString());
			appointmentDetails.add(fetchCenterResponse.jsonPath()
					.get("response.centerDetails[" + i + "].timeSlots[0].toTime").toString());
			break;
		}
		return appointmentDetails;
	}

}
