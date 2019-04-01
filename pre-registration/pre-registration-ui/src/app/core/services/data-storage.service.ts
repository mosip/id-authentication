import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

import { BookingModelRequest } from '../../shared/booking-request.model';
import * as appConstants from '../../app.constants';
import Utils from '../../app.util';
import { AppConfigService } from '../../app-config.service';
import { Applicant } from '../../shared/models/dashboard-model/dashboard.modal';
import { ConfigService } from './config.service';

@Injectable({
  providedIn: 'root'
})
export class DataStorageService {
  constructor(
    private httpClient: HttpClient,
    private appConfigService: AppConfigService,
    private configService: ConfigService
  ) {}

  BASE_URL = this.appConfigService.getConfig()['BASE_URL'];
  PRE_REG_URL = this.appConfigService.getConfig()['PRE_REG_URL'];

  //here
  getUsers(value: string) {
    // const url = this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.applicants + appConstants.APPENDER + value;
    return this.httpClient.get<Applicant[]>(this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.applicants, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append(appConstants.PARAMS_KEYS.getUsers, value)
    });
  }

  //here
  getUser(preRegId: string) {
    // const url = this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.get_applicant + appConstants.APPENDER + preRegId;
    return this.httpClient.get(this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.get_applicant, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append(appConstants.PARAMS_KEYS.getUser, preRegId)
    });
  }

  getGenderDetails() {
    return this.httpClient.get(this.BASE_URL + appConstants.APPEND_URL.gender);
    // return this.httpClient.get(this.BASE_URL + appConstants.APPEND_URL.gender);
  }

  getTransliteration(request: any) {
    const obj = {
      id: appConstants.IDS.transliteration,
      requesttime: Utils.getCurrentDate(),
      version: appConstants.VERSION,
      request: request
    };

    return this.httpClient.post(this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.transliteration, obj);
  }

  getUserDocuments(preRegId) {
    console.log('documents fetched for : ', preRegId);

    return this.httpClient.get(this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.document, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append(appConstants.PARAMS_KEYS.getDocument, preRegId)
    });
  }

  addUser(identity: any) {
    const obj = {
      id: appConstants.IDS.newUser,
      version: appConstants.VERSION,
      requesttime: Utils.getCurrentDate(),
      request: identity
    };
    console.log('data being sent', obj);

    return this.httpClient.post(this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.applicants, obj);
  }

  updateUser(identity: any) {
    const obj = {
      id: appConstants.IDS.newUser,
      version: appConstants.VERSION,
      requesttime: Utils.getCurrentDate(),
      request: identity
    };
    console.log('data being sent', obj);

    return this.httpClient.put(this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.applicants, obj);
  }

  sendFile(formdata: FormData) {
    return this.httpClient.post(this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.document, formdata);
    // console.log('servvice called', formdata);
  }

  //here
  deleteRegistration(preId: string) {
    // const url = this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.applicants + appConstants.APPENDER + preId;
    return this.httpClient.delete(this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.applicants, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append(appConstants.PARAMS_KEYS.deleteUser, preId)
    });
  }

  cancelAppointment(data: BookingModelRequest) {
    console.log('cancel appointment data', data);
    return this.httpClient.put(this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.booking_appointment, data);
  }

  getNearbyRegistrationCenters(coords: any) {
    return this.httpClient.get(
      this.BASE_URL +
        appConstants.APPEND_URL.master_data +
        appConstants.APPEND_URL.nearby_registration_centers +
        localStorage.getItem('langCode') +
        '/' +
        coords.longitude +
        '/' +
        coords.latitude +
        '/' +
        this.configService.getConfigByKey(appConstants.CONFIG_KEYS.preregistration_nearby_centers)
    );
  }

  getRegistrationCentersByName(locType: string, text: string) {
    return this.httpClient.get(
      this.BASE_URL +
        appConstants.APPEND_URL.master_data +
        appConstants.APPEND_URL.registration_centers_by_name +
        localStorage.getItem('langCode') +
        '/' +
        locType +
        '/' +
        text
    );
  }

  getLocationTypeData() {
    return this.httpClient.get(
      this.BASE_URL + appConstants.APPEND_URL.master_data + 'locations/' + localStorage.getItem('langCode')
    );
  }

  getAvailabilityData(registrationCenterId) {
    return this.httpClient.get(this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.booking_availability, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append(appConstants.PARAMS_KEYS.getAvailabilityData, registrationCenterId)
    });
  }

  makeBooking(request: BookingModelRequest) {
    console.log('request inside service', request);
    return this.httpClient.post(
      this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.booking_appointment,
      request
    );
  }

  getLocationMetadataHirearchy(value: string) {
    return this.httpClient.get(
      this.BASE_URL + appConstants.APPEND_URL.location + appConstants.APPEND_URL.location_metadata + value,
      // this.BASE_URL2 + appConstants.APPEND_URL.location + appConstants.APPEND_URL.location_metadata + value,
      {
        params: new HttpParams().append(appConstants.PARAMS_KEYS.locationHierarchyName, value)
      }
    );
  }

  getLocationImmediateHierearchy(lang: string, location: string) {
    return this.httpClient.get(
      // this.BASE_URL2 +
      this.BASE_URL +
        appConstants.APPEND_URL.location +
        appConstants.APPEND_URL.location_immediate_children +
        location +
        '/' +
        lang
    );
  }

  deleteFile(documentId) {
    return this.httpClient.delete(this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.document, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append(appConstants.PARAMS_KEYS.deleteFile, documentId)
    });
  }

  getSecondaryLanguageLabels(langCode: string) {
    return this.httpClient.get(`./assets/i18n/${langCode}.json`);
  }

  copyDocument(catCode: string, sourceId: string, destinationId: string) {
    const url =
      this.BASE_URL +
      this.PRE_REG_URL +
      appConstants.APPEND_URL.document_copy +
      '?catCode=POA&destinationPreId=' +
      destinationId +
      '&sourcePrId=' +
      sourceId;
    console.log('copy document URL', url);
    return this.httpClient.post(url, '');
  }

  generateQRCode(data: string) {
    return this.httpClient.post(this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.qr_code, data);
  }

  sendNotification(data: FormData) {
    return this.httpClient.post(
      this.BASE_URL +
        this.PRE_REG_URL +
        appConstants.APPEND_URL.notification +
        appConstants.APPEND_URL.send_notification,
      data
    );
  }

  recommendedCenters(langCode: string, locationHierarchyCode: number, data: string[]) {
    let url =
      this.BASE_URL +
      appConstants.APPEND_URL.master_data +
      'registrationcenters/' +
      langCode +
      '/' +
      locationHierarchyCode +
      '/names?';
    data.forEach(name => {
      url += 'name=' + name;
      if (data.indexOf(name) !== data.length - 1) {
        url += '&';
      }
    });
    if (url.charAt(url.length - 1) === '&') {
      url = url.substring(0, url.length - 1);
    }
    console.log(url);
    return this.httpClient.get(url);
  }

  getRegistrationCenterByIdAndLangCode(id: string, langCode: string) {
    const url = this.BASE_URL + appConstants.APPEND_URL.master_data + 'registrationcenters/' + id + '/' + langCode;
    return this.httpClient.get(url);
  }

  getGuidelineTemplate() {
    const url =
      this.BASE_URL +
      appConstants.APPEND_URL.master_data +
      'templates/' +
      localStorage.getItem('langCode') +
      '/' +
      'Onscreen-Acknowledgement';
    return this.httpClient.get(url);
  }

  getApplicantType(docuemntCategoryDto) {
    return this.httpClient.post(
      this.BASE_URL + appConstants.APPEND_URL.applicantType + appConstants.APPEND_URL.getApplicantType,
      docuemntCategoryDto
    );
  }

  getDocumentCategories(applicantCode) {
    const APPLICANT_VALID_DOCUMENTS_URL =
      this.BASE_URL +
      appConstants.APPEND_URL.location +
      appConstants.APPEND_URL.validDocument +
      applicantCode +
      '/languages';
    return this.httpClient.get(APPLICANT_VALID_DOCUMENTS_URL, {
      params: new HttpParams().append(appConstants.PARAMS_KEYS.getDocumentCategories, localStorage.getItem('langCode'))
    });
  }

  getConfig() {
    //    return this.httpClient.get('./assets/configs.json');
    return this.httpClient.get(
      this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.auth + appConstants.APPEND_URL.config
    );
  }

  sendOtp(userId: string) {
    console.log(userId);

    const req = {
      langCode: localStorage.getItem('langCode'),
      userId: userId
    };

    const obj = {
      id: appConstants.IDS.newUser,
      version: appConstants.VERSION,
      requesttime: Utils.getCurrentDate(),
      request: req
    };

    return this.httpClient.post(
      this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.auth + appConstants.APPEND_URL.send_otp,
      obj
    );
  }

  verifyOtp(userId: string, otp: string) {
    const request = {
      otp: otp,
      userId: userId
    };

    const requestObj = {
      id: appConstants.IDS.newUser,
      version: appConstants.VERSION,
      requesttime: Utils.getCurrentDate(),
      request: request
    };

    return this.httpClient.post(
      this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.auth + appConstants.APPEND_URL.login,
      requestObj
    );
  }

  onLogout() {
    const auth = this.BASE_URL + this.PRE_REG_URL + appConstants.APPEND_URL.auth + appConstants.APPEND_URL.logout;
    return this.httpClient.post(auth, '');
  }
}
