package io.mosip.util;

import java.util.regex.Pattern;

public class UIN_Assertions {

	public static boolean ascendingMethod(String uin){
	 	   char first_char=uin.charAt(0); 
	 	   String latest_uin="";
	 	   for(int i=0;i<uin.length();i++){
	 		   if(first_char>'9'){
	 			  first_char='0';
	 		   }
	 		   
	 		   
	 		  latest_uin=latest_uin+first_char;
	 	 	   first_char++;
	 	   }
	 	   
	 	   if(uin.equals(latest_uin)){
	 		   return true;
	 	   }else{
	 		   return false;
	 	   }
		}
	public static boolean descendingMethod(String uin){
	 	   char first_char=uin.charAt(0); 
	 	   String latest_uin="";
	 	   for(int i=0;i<uin.length();i++){
	 		   if(first_char<'0'){
	 			  first_char='9';
	 		   }
	 		   
	 		   
	 		  latest_uin=latest_uin+first_char;
	 	 	   first_char--;
	 	   }
	 	   
	 	   if(uin.equals(latest_uin)){
	 		   return true;
	 	   }else{
	 		   return false;
	 	   }
		}
	public static boolean asserUinWithPattern(String uin, String regX)
	{
		Pattern p=Pattern.compile(regX);
		return p.matcher(uin).find();
	}
}
