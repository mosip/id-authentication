import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpRequest } from '@angular/common/http';

import { Applicant } from '../registration/dashboard/dashboard.modal';

@Injectable({
  providedIn: 'root'
})
export class DataStorageService {
  constructor(private httpClient: HttpClient) {}

  SEND_FILE_URL =
    'http://preregistration-intgra.southindia.cloudapp.azure.com/int-demographic/v0.1/pre-registration/registration/documents';
  BASE_URL2 = 'http://a2ml27511:9092/v0.1/pre-registration/applicationData';
  BASE_URL =
    'http://preregistration-intgra.southindia.cloudapp.azure.com/int-demographic/v0.1/pre-registration/applications';
  // // obj: JSON;  yyyy-MM-ddTHH:mm:ss.SSS+000
  // https://pre-reg-df354.firebaseio.com/applications.json
  MASTER_DATA_URL = 'http://localhost:8086/';
  LANGUAGE_CODE = 'ENG';
  DISTANCE = 2000;

  AVAILABILITY_URL = 'http://localhost:9094/v0.1/pre-registration/booking/availability';

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
    return this.httpClient.get(this.MASTER_DATA_URL + 'getcoordinatespecificregistrationcenters/' +
    this.LANGUAGE_CODE + '/' + coords.longitude + '/' + coords.latitude + '/' + this.DISTANCE);
  }

  getRegistrationCentersByName(locType: string, text: string) {
    return this.httpClient.get(this.MASTER_DATA_URL + 'registrationcenters/' + this.LANGUAGE_CODE + '/' + locType + '/' + text);
  }

  getAvailabilityData(registrationCenterId) {
    return this.httpClient.get(this.AVAILABILITY_URL, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append('RegCenterId', registrationCenterId)
    });
  }
}
