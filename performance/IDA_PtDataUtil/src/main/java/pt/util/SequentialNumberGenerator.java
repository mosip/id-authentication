package pt.util;

public class SequentialNumberGenerator {

	private static Integer number;

	public SequentialNumberGenerator() {

	}

	public static void assignStartIndex(int startNumber) {
		number = startNumber;
	}

	public static Integer generateNext() {
		//System.out.println(startNumber);
		number = number + 1;
		if(number == 999999) {
			number = 100001;
		}
		//System.out.println(startNumber);
		return number;
	}

}
