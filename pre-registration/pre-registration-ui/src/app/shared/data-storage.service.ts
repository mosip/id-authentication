import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';

import { Applicant } from '../registration/dashboard/modal/dashboard.modal';
import { BookingModelRequest } from './booking-request.model';
import * as appConstants from './../app.constants';

@Injectable({
  providedIn: 'root'
})
export class DataStorageService {
  constructor(private httpClient: HttpClient) {}

  SEND_FILE_URL = 'http://integ.mosip.io/document/v0.1/pre-registration/documents';
  DELETE_FILE_URL = 'https://integ.mosip.io/document/v0.1/pre-registration/deleteDocument';
  GET_FILE_URL = 'http://integ.mosip.io/document/v0.1/pre-registration/getDocument';
  BASE_URL2 = 'https://integ.mosip.io/demographic/v0.1/pre-registration/applicationData';
  BASE_URL = 'https://integ.mosip.io/demographic/v0.1/pre-registration/applications';
  MASTER_DATA_URL = 'https://cors-anywhere.herokuapp.com/http://integ.mosip.io/masterdata/v1.0/';
  AVAILABILITY_URL = 'https://integ.mosip.io/booking/v0.1/pre-registration/booking/availability';
  BOOKING_URL = 'https://integ.mosip.io/booking/v0.1/pre-registration/booking/book';
  LOCATION_URL = 'https://integ.mosip.io/masterdata/';
  TRANSLITERATION_URL = 'http://A2ML29824:9098/dev-PreRegTranslitration/v0.1/pre-registration/translitrate';
  LANGUAGE_CODE = 'ENG';
  DISTANCE = 2000;

  getUsers(value: string) {
    return this.httpClient.get<Applicant[]>(this.BASE_URL, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append(appConstants.PARAMS_KEYS.getUsers, value)
    });
  }

  getUser(preRegId: string) {
    return this.httpClient.get(this.BASE_URL2, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append(appConstants.PARAMS_KEYS.getUser, preRegId)
    });
  }

  getTransliteration(request) {
    const obj = {
      id: appConstants.TRANSLITERATION_ID,
      reqTime: '2019-01-02T11:01:31.211Z',
      ver: appConstants.VERSION,
      request: request
    };

    return this.httpClient.post(this.TRANSLITERATION_URL, obj);
  }

  getUserDocuments(preRegId) {
    return this.httpClient.get(this.GET_FILE_URL, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append('preId', preRegId)
    });
  }

  addUser(identity: any) {
    const obj = {
      id: appConstants.NEW_USER_ID,
      ver: appConstants.VERSION,
      reqTime: '2019-01-02T11:01:31.211Z',
      request: identity
    };
    console.log('data being sent', obj);

    return this.httpClient.post(this.BASE_URL, obj);
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
      params: new HttpParams().append('RegCenterId', registrationCenterId)
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
    return this.httpClient.get(this.LOCATION_URL + appConstants.LOCATION_APPEND_URL, {
      params: new HttpParams().append(appConstants.PARAMS_KEYS.locationHierarchyName, value)
    });
  }

  getLocationImmediateHierearchy(lang: string, location: string) {
    return this.httpClient.get(
      this.LOCATION_URL + appConstants.LOCATION_IMMEDIATE_CHILDREN_APPEND_URL + location + '/' + lang
    );
  }

  // getLocationList(locationCode: string, langCode: string) {
  //   const URL = 'https://integ.mosip.io/masterdata/v1.0/locations/';
  //   return this.httpClient
  //     .get(URL, {
  //       observe: 'body',
  //       responseType: 'json',
  //       params: new HttpParams().append('locationCode', locationCode).append('langCode', langCode)
  //     })
  //     .subscribe(res => console.log(res));
  // }

  deleteFile(documentId) {
    return this.httpClient.delete(this.DELETE_FILE_URL, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append('documentId', documentId)
    });
  }
}
