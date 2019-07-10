package io.mosip.resgistrationProcessor.perf.dbaccess;

import java.util.*;

import javax.persistence.Query;

import org.hibernate.Session;

import io.mosip.registrationProcessor.perf.entity.Location;

public class RegProcPerfDaoImpl {

	public List<Location> getCountry() {
		Session session = DBUtil.obtainSession();
		List<Location> locations = new ArrayList<>();
		Query q = session.createQuery("from Location where lang_code='eng' and hierarchy_level=0");
		locations = q.getResultList();
		System.out.println("locations.size() " + locations.size());
		session.close();
		return locations;
	}

	public List<Location> getLocations(String parentLocationCode, int hierarchyLevel) {
		List<Location> locations = new ArrayList<>();
		String query = "from Location where lang_code='eng' and hierarchy_level=" + hierarchyLevel
				+ " and parent_loc_code='" + parentLocationCode + "'";
		Session session = DBUtil.obtainSession();
		Query q = session.createQuery(query);
		locations = q.getResultList();
		session.close();
		return locations;
	}

	public String getTranslatedLocation(String locationName, String toLangCode, int hierarchy_level) {
		String result = "";
		String fromLangCode = "eng";
		// String queryString = "select name from Location where lang_code='" +
		// toLangCode
		// + "' and code in (SELECT code FROM Location where name= :locationName and
		// lang_code=:fromLangCode)";
		System.out.println("Fetching " + locationName + " in " + toLangCode);
		String queryString = "select name from Location where lang_code='" + toLangCode
				+ "' and code in (SELECT code FROM Location where name='" + locationName + "' and lang_code='"
				+ fromLangCode + "' and hierarchy_level=" + hierarchy_level + ")";
		Session session = DBUtil.obtainSession();
		Query query = session.createQuery(queryString);
		// query.setParameter("locationName", locationName);
		// query.setParameter("fromLangCode", fromLangCode);
		result = (String) query.getSingleResult();
		System.out.println("Name fetched is " + result);
		return result;
	}

}
