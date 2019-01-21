package io.mosip.registration.dto.cbeff.jaxbclasses;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TestBiometric.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TestBiometric">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Unique"/>
 *     &lt;enumeration value="Duplicate"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "TestBiometric")
@XmlEnum
public enum TestBiometric {

    @XmlEnumValue("Unique")
    UNIQUE("Unique"),
    @XmlEnumValue("Duplicate")
    DUPLICATE("Duplicate");

    private final String value;

    TestBiometric(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TestBiometric fromValue(String v) {
        for (TestBiometric c: TestBiometric.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
