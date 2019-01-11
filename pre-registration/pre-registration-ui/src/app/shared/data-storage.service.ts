import { Injectable } from '@angular/core';
import { environment } from './../../environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';

import { Applicant } from '../registration/dashboard/modal/dashboard.modal';
import { BookingModelRequest } from './booking-request.model';
import * as appConstants from './../app.constants';

@Injectable({
  providedIn: 'root'
})
export class DataStorageService {
  constructor(private httpClient: HttpClient) {}

  BASE_URL = environment.BASE_URL;
  SEND_FILE_URL = this.BASE_URL + 'document/v0.1/pre-registration/documents';
  DELETE_FILE_URL = this.BASE_URL + 'document/v0.1/pre-registration/deleteDocument';
  GET_FILE_URL = this.BASE_URL + 'document/v0.1/pre-registration/getDocument';
  MASTER_DATA_URL = 'https://integ.mosip.io/' + 'masterdata/v1.0/';
  AVAILABILITY_URL = this.BASE_URL + 'booking/v0.1/pre-registration/booking/availability';
  BOOKING_URL = this.BASE_URL + 'booking/v0.1/pre-registration/booking/book';
  TRANSLITERATION_URL = 'http://A2ML29824:9098/dev-PreRegTranslitration/v0.1/pre-registration/translitrate';
  TEST_URL = 'http://A2ML27085:9092/';
  LANGUAGE_CODE = 'ENG';
  DISTANCE = 2000;

  getUsers(value: string) {
    return this.httpClient.get<Applicant[]>(this.BASE_URL + appConstants.APPEND_URL.applicants, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append(appConstants.PARAMS_KEYS.getUsers, value)
    });
  }

  getUser(preRegId: string) {
    return this.httpClient.get(this.BASE_URL + appConstants.APPEND_URL.get_applicant, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append(appConstants.PARAMS_KEYS.getUser, preRegId)
    });
  }

  getTransliteration(request) {
    const obj = {
      id: appConstants.IDS.transliteration,
      reqTime: '2019-01-02T11:01:31.211Z',
      ver: appConstants.VERSION,
      request: request
    };

    return this.httpClient.post(this.BASE_URL + appConstants.APPEND_URL.transliteration, obj);
  }

  getUserDocuments(preRegId) {
    console.log('pre reg id', preRegId);

    return this.httpClient.get(this.GET_FILE_URL, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append('pre_registration_id', preRegId)
    });
  }

  addUser(identity: any) {
    const obj = {
      id: appConstants.IDS.newUser,
      ver: appConstants.VERSION,
      reqTime: '2019-01-02T11:01:31.211Z',
      request: identity
    };
    console.log('data being sent', obj);

    return this.httpClient.post(this.BASE_URL + appConstants.APPEND_URL.applicants, obj);
  }

  sendFile(formdata: FormData) {
    return this.httpClient.post(this.SEND_FILE_URL, formdata);
    // console.log('servvice called', formdata);
  }

  deleteRegistration(preId: string) {
    return this.httpClient.delete(this.BASE_URL, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append(appConstants.PARAMS_KEYS.deleteUser, preId)
    });
  }

  cancelAppointment(data: BookingModelRequest) {
    console.log('cancel appointment data', data);
    return this.httpClient.put(this.BOOKING_URL, data);
  }

  getNearbyRegistrationCenters(coords: any) {
    return this.httpClient.get(
      this.MASTER_DATA_URL +
        'getcoordinatespecificregistrationcenters/' +
        this.LANGUAGE_CODE +
        '/' +
        coords.longitude +
        '/' +
        coords.latitude +
        '/' +
        this.DISTANCE
    );
  }

  getRegistrationCentersByName(locType: string, text: string) {
    return this.httpClient.get(
      this.MASTER_DATA_URL + 'registrationcenters/' + this.LANGUAGE_CODE + '/' + locType + '/' + text
    );
  }

  getAvailabilityData(registrationCenterId) {
    return this.httpClient.get(this.AVAILABILITY_URL, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append('registration_center_id', registrationCenterId)
    });
  }

  makeBooking(request: BookingModelRequest) {
    // const x = {
    //   id: 'mosip.pre-registration.booking.book',
    //   reqTime: '2018-12-10T08:24:10.749',
    //   request: [
    //     {
    //       newBookingDetails: {
    //         reg_date: "2018-12-13",
    //         registration_center_id: "1",
    //         time-slot-from: "09:00:00",
    //         time-slot-to: "09:13:00"
    //       },
    //       "oldBookingDetails": null,
    //       "pre_registration_id": "90597269106527"
    //     }
    //   ],
    //   "ver": "1.0"
    // }
    console.log('request inside service', request);
    return this.httpClient.post(this.BOOKING_URL, request);
  }

  getLocationMetadataHirearchy(value: string) {
    return this.httpClient.get(
      this.BASE_URL + appConstants.APPEND_URL.location + appConstants.APPEND_URL.location_metadata + value,
      {
        params: new HttpParams().append(appConstants.PARAMS_KEYS.locationHierarchyName, value)
      }
    );
  }

  getLocationImmediateHierearchy(lang: string, location: string) {
    return this.httpClient.get(
      this.BASE_URL +
        appConstants.APPEND_URL.location +
        appConstants.APPEND_URL.location_immediate_children +
        location +
        '/' +
        lang
    );
  }

  deleteFile(documentId) {
    return this.httpClient.delete(this.DELETE_FILE_URL, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append('documentId', documentId)
    });
  }

  getPreviewData(preRegId: string) {
    return this.httpClient.get(this.BASE_URL + appConstants.PREVIEW_DATA_APPEND_URL, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append(appConstants.PARAMS_KEYS.getUser, preRegId)
    });
  }

  getSecondaryLanguageLabels(langCode: string) {
    return this.httpClient.get(`./assets/i18n/${langCode}.json`);
  }
}
