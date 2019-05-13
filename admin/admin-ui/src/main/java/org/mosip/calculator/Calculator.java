package org.mosip.calculator;

public class Calculator {

	private CalculatorService calculatorService;

	public int add(int firstNum, int secondNum) {

		return calculatorService.add(firstNum, secondNum);
	}

	public void showNumbers(int num) {

		System.out.println("showNumbers called");

		calculatorService.show(num);
	}

	public void checkNull(int num) {

		if (calculatorService.checkNull(num)) {
			throw new IllegalArgumentException("Invalid numbers");
		}
	}

}
