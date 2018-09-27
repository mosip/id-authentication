package org.mosip.kernel.ridgenerator;

import java.util.Random;

import org.mosip.kernel.core.utils.StringUtil;
import org.mosip.kernel.ridgenerator.RidGenerator;
import org.mosip.kernel.ridgenerator.exception.MosipEmptyInputException;
import org.mosip.kernel.ridgenerator.exception.MosipInputLengthException;
import org.mosip.kernel.ridgenerator.exception.MosipNullValueException;

/**
 * This class is used to test the functionality of RID Generator
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class App {
	static Random rn = new Random();

	public static void main(String[] args)
			throws MosipNullValueException, MosipEmptyInputException, MosipInputLengthException {
		RidGenerator eidGen = new RidGenerator();
		System.out.println("Hello World!");
		// String eid=null;
		String var1 = "asdfg";
		String var2 = "qwert678";
		StringUtil.removeLeftChar(var1, 4);
		StringUtil.removeLeftChar(var2, 5);
		Random rn = new Random();
		// checkInput("1234","145");
		// System.out.println(value);
		int n = 10000 + rn.nextInt(90000);
		// DateTimeFormatter aFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		long formattedTime = System.currentTimeMillis();
		// LocalDateTime today = LocalDateTime.now();
		// today.toString();
		// String foramttedString = today.format(aFormatter);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(var1).append(var2).append(n).append(formattedTime);
		// eid=stringBuilder.toString();
		// System.out.println(eid);
		System.out.println(eidGen.ridGeneration(null, null));
		/*
		 * System.out.println("Current DateTime="+foramttedString); Random rnd = new
		 * Random(); int n = 10000 + rnd.nextInt(90000); SimpleDateFormat formatter =
		 * new SimpleDateFormat("yyyyMMddHHmmss"); Date date = new Date(); String
		 * currTimestamp=formatter.format(date); eid=agent1+machine1+n+currTimestamp;
		 * //System.out.println(eid);
		 */ }
}
