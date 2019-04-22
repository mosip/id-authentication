package io.mosip.preregistration.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import io.mosip.preregistration.dao.PreregistratonDAO;
import io.mosip.preregistration.entity.DemographicEntity;
import io.mosip.util.ReadFolder;

public class SampleTest {

	public static void main(String[] args) {
		
		
		PreregistratonDAO dao=new PreregistratonDAO();
		/* List<? extends Object> objs = dao.preregFetchPreregDetails();
		//String[] arr = s.toArray(new String[] {});
		//System.out.println(Arrays.deepToString(arr));
		
		
		int size = objs.size();
		Object[] TestData = null;
		//int i = 0;
		for (Object obj : objs) {
			TestData = (Object[]) obj;
			
			System.out.println("My Test Data Data1::::" + TestData[0]);
			System.out.println("My Test Data Data1::::" + TestData[1]);
			
		}
		System.out.println("My Test Data Data2::::" + TestData[0]);
		
		*/
		/*Update*/
		
		 int objs = dao.updateStatusCode("Consumed","52182457031403");
			//String[] arr = s.toArray(new String[] {});
			System.out.println("Result::"+objs);
			
			
		
		
		// TODO Auto-generated method stub
		/*
		String testCaseName = null;
		testCaseName="CopyUploadedDocumentByPassingSourcePreIdForWhichNoDocUploaded";
		String val = testCaseName.contains("smoke")
				?(testCaseName="cond1"):testCaseName.contains("CopyUploadedDocumentByPassingInvalidCatCode")
				?(testCaseName="cond2"):testCaseName.contains("CopyUploadedDocumentByPassingInvalidDestinationPreId")
				?(testCaseName="cond3"):testCaseName.contains("CopyUploadedDocumentByPassingInvalidSourcePreId")
				?(testCaseName="cond4"):testCaseName.contains("CopyUploadedDocumentByPassingDestPreIdForWhichPOADocAlreadyExists")
				?(testCaseName="cond5"):(testCaseName="cond6");
		
         System.out.println("val:"+val.toString());
         
         switch (val) {
 		case "cond1":
 			
 			System.out.println("1111");
         break;
 		case "cond6":
 			System.out.println("2222");
 	         break;
 		default:
 			System.out.println("33333");
 			break;
 		

 	}
         
*/         
		/*
		HashMap<String, String> parm= new HashMap<>();
		parm.put("preRegistrationId", "98989898");
		parm.put("poerpepro", "45345345435");
		System.out.println("jhjhj:"+parm.entrySet().stream().map(o -> o.getValue()).collect(Collectors.toList()));
		System.out.println("reeewrwer:"+parm.entrySet().stream().map(o -> o.getKey()).collect(Collectors.toList()));
		
		
		*/
		/*
		Map<String, Integer> items = new HashMap<>();
        items.put("key 1", 1);
        items.put("key 2", 2);
        items.put("key 3", 3);
         //System.out.println("gfhfhf::"+items.forEach((k,v)->(k+v )));
        items.forEach((k,v)->System.out.println("Item : " + k + " Count : " + v));
		
		*/
        
        /*
        
        Map<String, String> jbtObj = new HashMap<String, String>();
		jbtObj.put("Website Name","Java Beginners Tutorial");
		jbtObj.put("Language", "Java");
		jbtObj.put("Topic", "Collection");
		for (Map.Entry<String, String> entry : jbtObj.entrySet()) {
			System.out.println(entry.getKey() + " : "+ entry.getValue());
		}
		// Iterating over collection object using iteration even before Java 5
		Iterator<Entry<String, String>> iterator = jbtObj.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> thisEntry = (Entry<String, String>) iterator.next();
			Object key = thisEntry.getKey();
			Object value = thisEntry.getValue();
			System.out.println("yuyuyuyyu:"+key+" : "+value);
		}
        
		*/
		/*HashMap<String, String> parm= new HashMap<>();
		parm.put("preRegistrationId", "98989898");
		parm.put("poerpepro", "45345345435");
		System.out.println("hjjh"+parm);
		Object firstKey = parm.keySet().toArray()[0];
		Object valueForFirstKey = parm.get(firstKey);
		System.out.println(parm.get(parm.keySet().toArray()[0]).toString());
		System.out.println("Size of HashMap : " + parm.size());
		
		for(int i=0;i<parm.size();i++)
		{	
		System.out.println("jhjhjhh::jljljl::"+parm.get(parm.keySet().toArray()[i]).toString());
		}
		
		int i=0;
			
		System.out.println("jhjhjhh::jljljl::"+parm.get(parm.keySet().toArray()[i]).toString());
		System.out.println("jhjhjhh::jljljl::"+parm.get(parm.keySet().toArray()[i+1]).toString());
		*/
		/*
		         
		Set<String> keySet = parm.keySet(); 
		Collection<String> values = parm.values();
		System.out.println("kjkjkjkjkjkjkjkjkj::::"+keySet);
		*//*ArrayList<String> listOfKeys = new ArrayList<String>(keySet);
		ArrayList<String> listOfValues = new ArrayList<String>(values);
		for(int i=0;i<listOfValues.size();i++)
		{
			String g = listOfValues.get(i);
			 
			System.out.println("kjkjkjkjkjkjkjkjkj::::"+keySet);
			
		}*/
		
		//System.out.println("Get Key Value:"+parm.get("preRegistrationId"));
		//System.out.println(parm.keySet().toArray()[0]);
		//System.out.println(parm.values());
	}

}
