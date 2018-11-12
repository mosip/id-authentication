import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';

import { Applicant } from './dashboard.modal';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {

  constructor(private httpClient: HttpClient) { }


  getUsers() {
     return this.httpClient.get<Applicant[]>(
      'https://pre-reg-df354.firebaseio.com/applications.json', {
      observe: 'body',
      responseType: 'json'
    });
  }

}
