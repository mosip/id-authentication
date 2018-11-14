package io.mosip.kernel.datamapper.orika.test.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import io.mosip.kernel.core.datamapper.spi.DataConverter;

public class PersonListConverter implements DataConverter<List<Person>, List<Personne>> {

	@Override
	public void convert(List<Person> source, List<Personne> destination) {
		source.forEach((p) -> {
			Personne personne = new Personne();
			LocalDate date=p.getDob();
			LocalDate now= LocalDate.now();
			Period period = Period.between(date, now);
			personne.setAge(period.getYears());
         
			destination.add(personne);
		});
	}


}
