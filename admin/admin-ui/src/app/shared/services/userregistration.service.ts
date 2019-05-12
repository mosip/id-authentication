import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
@Injectable()
export class UserregistrationService {

  constructor(private http: HttpClient) { }

  getGenderTypes(){
    return this.http.get('https://qa.mosip.io/v1/masterdata/gendertypes');
  }
}
