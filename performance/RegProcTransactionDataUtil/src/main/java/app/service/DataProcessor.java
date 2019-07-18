package app.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.Map.Entry;

import app.dbaccess.DBAccessImpl;
import app.entity.TransactionEntity;
import app.util.CSVUtil;
import app.util.ExcelUtil;
import app.util.PropertiesUtil;

public class DataProcessor {

	DBAccessImpl dbAccess;

	private Set<String> activities;

	public void processData() {

		List<TransactionEntity> trList;

		dbAccess = new DBAccessImpl();

		List<String> regidList = null;

		String csvFile = PropertiesUtil.REGID_FILE;

		regidList = CSVUtil.loadRegIds(csvFile);

		Map<String, Map<String, Long>> regIdsMap = new HashMap<>();

		activities = new LinkedHashSet<>();

//		int c = 0;
//		for (String regid : regidList) {
//			trList = dbAccess.getRegTransactionForRegId(regid);
//			Map<String, Long> diffMap = processForRegId(trList);
//			regIdsMap.put(regid, diffMap);
//			if (c == 0) {
//				activities = diffMap.keySet();
//			} else {
//				Set<String> activities1 = diffMap.keySet();
//				if (activities1.size() != activities.size()) {
//					System.err.println("Number of activities not matching for regid:- " + regid);
//				}
//			}
//
//		}
//
//		computeAverageDifference(regIdsMap, activities);

		obtainActivitiesMappedByInitialCreationTime(regidList);

	}

	private void obtainActivitiesMappedByInitialCreationTime(List<String> regidList) {
		List<TransactionEntity> trList;
		Map<String, Map<String, Long>> regIdsMappedByStartTime = new HashMap<>();
		int c = 0;
		for (String regid : regidList) {
			trList = dbAccess.getRegTransactionForRegId(regid);

			Map<String, Long> diffMap = processForRegId(trList);
			String time = obtainLeastCreationTime(trList);
			regIdsMappedByStartTime.put(time, diffMap);
			if (c == 0) {
				activities = diffMap.keySet();
			} else {
				Set<String> activities1 = diffMap.keySet();
				if (activities1.size() != activities.size()) {
					System.err.println("Number of activities not matching for regid:- " + regid);
				}
			}
		}

		try {

			ExcelUtil.processDataToWriteToExcel(regIdsMappedByStartTime, activities);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private String obtainLeastCreationTime(List<TransactionEntity> trList) {
		Collections.sort(trList);
		LocalDateTime localDateTime = trList.get(0).getCreateDateTime();
		return localDateTime.toString();
	}

	private void computeAverageDifference(Map<String, Map<String, Long>> regIdsMap, Set<String> activities) {
		Set<Entry<String, Map<String, Long>>> entrySet = regIdsMap.entrySet();

		Map<String, Double> avgMap = new HashMap<>();
		Map<String, Long> sumsMap = new HashMap<>();

		int countRecords = regIdsMap.size();

		for (Entry<String, Map<String, Long>> entry : entrySet) {
			String regId = entry.getKey();
			Map<String, Long> activityMap = entry.getValue();

//			Set<Entry<String, Long>> activitySet = activityMap.entrySet();
//			Iterator<Entry<String, Long>> iterator = activitySet.iterator();
			for (String activity : activities) {

				if (null == sumsMap.get(activity)) {
					sumsMap.put(activity, activityMap.get(activity));
				} else {
					sumsMap.put(activity, sumsMap.get(activity) + activityMap.get(activity));
				}

			}
		}
		sumsMap.entrySet();
		for (Entry<String, Long> entry : sumsMap.entrySet()) {

			avgMap.put(entry.getKey(), (entry.getValue() / new Double(countRecords)));

		}

		for (Entry<String, Double> entry : avgMap.entrySet()) {

			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}

	}

	private Map<String, Long> processForRegId(List<TransactionEntity> trList) {

		Collections.sort(trList);

		Map<String, Long> activityMap = new HashMap<>();

		for (int i = 0; i < trList.size() - 1; i++) {
			TransactionEntity te1 = trList.get(i);
			TransactionEntity te2 = trList.get(i + 1);

			LocalDateTime date1 = te1.getCreateDateTime();
			LocalDateTime date2 = te2.getCreateDateTime();

			// UTC
			// ZonedDateTime zdt1 = date1.atZone(ZoneId.of("UTC"));
			// ZonedDateTime zdt2 = date2.atZone(ZoneId.of("UTC"));

			long localDTInMilli1 = date1.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
			long localDTInMilli2 = date2.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();

			long difference = localDTInMilli2 - localDTInMilli1;
			String key = te2.getTrntypecode();
			if (null != activityMap.get(key)) {
				key += "1";
			}
			activityMap.put(key, difference);
		}

		return activityMap;

	}

}
