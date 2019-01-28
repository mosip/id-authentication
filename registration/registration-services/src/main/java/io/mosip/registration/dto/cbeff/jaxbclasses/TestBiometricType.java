package io.mosip.registration.dto.cbeff.jaxbclasses;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author Balaji Sridharan
 * @since 1.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TestBiometricType")
public class TestBiometricType {

	@XmlAttribute(name = "xmlns")
	protected String xmlns;

	@XmlValue
	@XmlSchemaType(name = "string")
	protected TestBiometric testBiometric;

	/**
	 * @return the xmlns
	 */
	public String getXmlns() {
		return xmlns;
	}

	/**
	 * @param xmlns
	 *            the xmlns to set
	 */
	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	/**
	 * @return the testBiometric
	 */
	public TestBiometric getTestBiometric() {
		return testBiometric;
	}

	/**
	 * @param testBiometric
	 *            the testBiometric to set
	 */
	public void setTestBiometric(TestBiometric testBiometric) {
		this.testBiometric = testBiometric;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((testBiometric == null) ? 0 : testBiometric.hashCode());
		result = prime * result + ((xmlns == null) ? 0 : xmlns.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestBiometricType other = (TestBiometricType) obj;
		if (testBiometric != other.testBiometric)
			return false;
		if (xmlns == null) {
			if (other.xmlns != null)
				return false;
		} else if (!xmlns.equals(other.xmlns))
			return false;
		return true;
	}

}
