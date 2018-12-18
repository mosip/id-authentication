package io.mosip.registration.dto.mastersync;

import java.util.List;

/**
 * @author Sreekar Chukka
 *
 * @since 1.0.0
 */
public class MasterSyncDto {

	private List<BiometricAttributeResponseDto> biometricattributes;

	private List<BiometricTypeResponseDto> biometrictypes;

	private List<LanguageResponseDto> languages;

	private List<BlacklistedWordsDto> blacklistedwords;

	private List<GenderTypeResponseDto> genders;

	private List<IdTypeDto> idtypes;

	private List<TitleResponseDto> titles;

	private List<DocumentCategoryDto> documentcategories;

	private List<DocumentTypeDto> documenttypes;

	private List<LocationDto> locations;
	
	private ReasonCategoryDto reasonCategory;
	
	

	/**
	 * @return the reasonCategory
	 */
	public ReasonCategoryDto getReasonCategory() {
		return reasonCategory;
	}

	/**
	 * @param reasonCategory the reasonCategory to set
	 */
	public void setReasonCategory(ReasonCategoryDto reasonCategory) {
		this.reasonCategory = reasonCategory;
	}

	/**
	 * @return the locations
	 */
	public List<LocationDto> getLocations() {
		return locations;
	}

	/**
	 * @param locations the locations to set
	 */
	public void setLocations(List<LocationDto> locations) {
		this.locations = locations;
	}

	/**
	 * @return the documenttypes
	 */
	public List<DocumentTypeDto> getDocumenttypes() {
		return documenttypes;
	}

	/**
	 * @param documenttypes the documenttypes to set
	 */
	public void setDocumenttypes(List<DocumentTypeDto> documenttypes) {
		this.documenttypes = documenttypes;
	}

	/**
	 * @return the documentcategories
	 */
	public List<DocumentCategoryDto> getDocumentcategories() {
		return documentcategories;
	}

	/**
	 * @param documentcategories the documentcategories to set
	 */
	public void setDocumentcategories(List<DocumentCategoryDto> documentcategories) {
		this.documentcategories = documentcategories;
	}

	/**
	 * @return the titles
	 */
	public List<TitleResponseDto> getTitles() {
		return titles;
	}

	/**
	 * @param titles the titles to set
	 */
	public void setTitles(List<TitleResponseDto> titles) {
		this.titles = titles;
	}

	/**
	 * @return the idtypes
	 */
	public List<IdTypeDto> getIdtypes() {
		return idtypes;
	}

	/**
	 * @param idtypes the idtypes to set
	 */
	public void setIdtypes(List<IdTypeDto> idtypes) {
		this.idtypes = idtypes;
	}

	/**
	 * @return the genders
	 */
	public List<GenderTypeResponseDto> getGenders() {
		return genders;
	}

	/**
	 * @param genders the genders to set
	 */
	public void setGenders(List<GenderTypeResponseDto> genders) {
		this.genders = genders;
	}

	/**
	 * @return the blacklistedwords
	 */
	public List<BlacklistedWordsDto> getBlacklistedwords() {
		return blacklistedwords;
	}

	/**
	 * @param blacklistedwords the blacklistedwords to set
	 */
	public void setBlacklistedwords(List<BlacklistedWordsDto> blacklistedwords) {
		this.blacklistedwords = blacklistedwords;
	}

	/**
	 * @return the languages
	 */
	public List<LanguageResponseDto> getLanguages() {
		return languages;
	}

	/**
	 * @param languages the languages to set
	 */
	public void setLanguages(List<LanguageResponseDto> languages) {
		System.out.println();
		this.languages = languages;
	}

	/**
	 * @return the biometricattributes
	 */
	public List<BiometricAttributeResponseDto> getBiometricattributes() {
		return biometricattributes;
	}

	/**
	 * @param biometricattributes the biometricattributes to set
	 */
	public void setBiometricattributes(List<BiometricAttributeResponseDto> biometricattributes) {
		this.biometricattributes = biometricattributes;
	}

	/**
	 * @return the biometrictypes
	 */
	public List<BiometricTypeResponseDto> getBiometrictypes() {
		return biometrictypes;
	}

	/**
	 * @param biometrictypes the biometrictypes to set
	 */
	public void setBiometrictypes(List<BiometricTypeResponseDto> biometrictypes) {
		this.biometrictypes = biometrictypes;
	}

}
