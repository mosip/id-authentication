package io.mosip.preregistration.tests;

public class TestSimple {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String testCaseName;
		testCaseName="prereg_RetrivePreIdByRegCenterId_fromDate_withDateLessThanCurrentDate";
		
		String val = null;
		String name = null;
		if (testCaseName.contains("smoke")) {
			val = testCaseName;
		} else {
			String[] parts = testCaseName.split("_");
			val = parts[0]+parts[1]+parts[2];
			name = parts[3];
		}
		
		
		if (name != null) {
			testCaseName = val + "_" + name;
		}
		
		System.out.println("Test Case name::"+testCaseName);
		
		
		/*String theFirst = "Java Programming";
		String ROM = "\"" + theFirst + "\"";
		
		
		System.out.println("huyuyuy:"+ROM);
*/
	}

}
