package io.mosip.kernel.datamapper.orika.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Personne {

	private String nom;
	private String surnom;
	private int age;
	
}
