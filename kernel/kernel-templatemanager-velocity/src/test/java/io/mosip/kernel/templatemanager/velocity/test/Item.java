package io.mosip.kernel.templatemanager.velocity.test;

public class Item {

	String name;
	String price;

	public Item(String name, String price) {
		this.name = name;
		this.price = price;
	}

	public Item() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

}
