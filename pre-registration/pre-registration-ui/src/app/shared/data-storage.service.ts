import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Applicant } from '../registration/dashboard/dashboard.modal';
import { BookingModelRequest } from './booking-request.model';

@Injectable({
  providedIn: 'root'
})
export class DataStorageService {
  constructor(private httpClient: HttpClient) {}

  SEND_FILE_URL = 'http://integ.mosip.io/int-document/v0.1/pre-registration/documents';
  GET_FILE_URL = 'http://integ.mosip.io/int-document/v0.1/pre-registration/getDocument';
  BASE_URL2 = 'http://integ.mosip.io/int-demographic/v0.1/pre-registration/applicationData';
  BASE_URL = 'http://integ.mosip.io/int-demographic/v0.1/pre-registration/applications';
  // // obj: JSON;  yyyy-MM-ddTHH:mm:ss.SSS+000
  // https://pre-reg-df354.firebaseio.com/applications.json
  MASTER_DATA_URL = 'http://integ.mosip.io/masterdata/v1.0/';
  LANGUAGE_CODE = 'ENG';
  DISTANCE = 2000;

  AVAILABILITY_URL = 'http://integ.mosip.io/int-booking/v0.1/pre-registration/booking/availability';
  BOOKING_URL = 'http://integ.mosip.io/int-booking/v0.1/pre-registration/booking/book';

  getUsers(value) {
    return this.httpClient.get<Applicant[]>(this.BASE_URL, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append('userId', value)
    });
  }

  getUser(preRegId: string) {
    return this.httpClient.get(this.BASE_URL2, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append('preRegId', preRegId)
    });
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
      id: 'mosip.pre-registration.demographic.create',
      ver: '1.0',
      reqTime: '2018-10-17T07:22:57.086+0000',
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
      params: new HttpParams().append('preId', preId)
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
    const URL = 'http://a2ml29862:8080/v0.1/pre-registration/locations/location';
    return this.httpClient.get(URL, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append('hierarchyName', value)
    });
  }

  getLocationList(locationCode: string, langCode: string) {
    const URL = 'https://integ.mosip.io/masterdata/v1.0/locations/';
    return this.httpClient
      .get(URL, {
        observe: 'body',
        responseType: 'json',
        params: new HttpParams().append('locationCode', locationCode).append('langCode', langCode)
      })
      .subscribe(res => console.log(res));
  }
}
