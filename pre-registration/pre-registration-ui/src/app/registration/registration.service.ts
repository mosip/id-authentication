import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpParams } from '@angular/common/http';
import { Applicant } from './dashboard/dashboard.modal';
import { IdentityModel } from './demographic/identity.model';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  constructor(private httpClient: HttpClient) {}

  BASE_URL = 'http://preregistration.southindia.cloudapp.azure.com/dev-demographic/v0.1/pre-registration/applications';
  // obj: JSON;  yyyy-MM-ddTHH:mm:ss.SSS+000
  // https://pre-reg-df354.firebaseio.com/applications.json
  getUsers(value) {
    value = 'mosip.pre-registration.demographic.create';
    return this.httpClient.get<Applicant[]>('http://A2ML27085:9092/v0.1/pre-registration/applications', {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append('userId', value)
    });
  }

  addUser(identity: any) {
    // http://preregistration.southindia.cloudapp.azure.com/dev-demographic/

    const obj = {
      id: 'mosip.pre-registration.demographic.create',
      ver: '1.0',
      reqTime: '2018-10-17T07:22:57.086+0000',
      request: identity
    };

    // const req = new HttpRequest('POST', 'http://A2ML27085:9092/v0.1/pre-registration/applications', obj, {
    //   reportProgress: true
    // });
    // return this.httpClient.request(req);
    return this.httpClient.post('http://A2ML21989:9092/v0.1/pre-registration/applications', obj);
  }
}
