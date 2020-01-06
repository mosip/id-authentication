package io.mosip.kernel.masterdata.test.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestPojo {
	private String name;
	private int id;
	private double amount;
	private long mobileNo;
	private float salary;
	private LocalDate joinDate;
	private LocalDateTime lastUpdated;
	private short level;
	private Boolean active;

	public TestPojo(String name, int id, double amount) {
		this.name = name;
		this.id = id;
		this.amount = amount;
	}

	public TestPojo() {
	}
}
