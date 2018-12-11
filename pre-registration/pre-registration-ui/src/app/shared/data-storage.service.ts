import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, filter } from 'rxjs/operators';
import { Applicant } from '../registration/dashboard/dashboard.modal';
import { BookingModelRequest } from '../registration/center-selection/booking-request.model';
import { element } from '@angular/core/src/render3/instructions';

@Injectable({
  providedIn: 'root'
})
export class DataStorageService {
  constructor(private httpClient: HttpClient) {}

  SEND_FILE_URL =
    'http://preregistration-intgra.southindia.cloudapp.azure.com/int-demographic/v0.1/pre-registration/registration/documents';
  BASE_URL2 = 'http://a2ml27511:9092/v0.1/pre-registration/applicationData';
  BASE_URL =
    'http://localhost:9092/v0.1/pre-registration/applications';
  // // obj: JSON;  yyyy-MM-ddTHH:mm:ss.SSS+000
  // https://pre-reg-df354.firebaseio.com/applications.json
  MASTER_DATA_URL = 'http://localhost:8086/masterdata/v1.0/';
  LANGUAGE_CODE = 'ENG';
  DISTANCE = 2000;

  AVAILABILITY_URL = 'http://localhost:9094/v0.1/pre-registration/booking/availability';
  BOOKING_URL = 'http://localhost:9094/v0.1/pre-registration/booking/book';

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
      params: new HttpParams().append('preRegId', '92386049015826')
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
    // console.log(JSON.stringify(obj)); 0 - sent, 1 - upload , 3-download

    // const req = new HttpRequest('POST', this.BASE_URL, obj, {
    //   reportProgress: true
    // });
    // return this.httpClient.request(req);
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
