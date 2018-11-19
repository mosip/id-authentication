package io.mosip.kernel.datamapper.orika.test.model;

import java.time.LocalDate;
import java.time.Period;

import io.mosip.kernel.core.datamapper.spi.DataConverter;

public class PersonConverter implements DataConverter<Person, Personne> {
	
	@Override
	public void convert(Person source, Personne destination) {
		Personne personne = new Personne();
		LocalDate date=source.getDob();
		LocalDate now= LocalDate.now();
		Period period = Period.between(date, now);
		personne.setAge(period.getYears());
	}

}
