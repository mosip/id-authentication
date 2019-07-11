import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import * as appConstants from '../../app.constants';

@Injectable()
export class UserregistrationService {

  constructor(private http: HttpClient) { }

  getGenderTypes() {
    return this.http.get(`https://${appConstants.base_url}/v1/masterdata/gendertypes`);
  }

  registerUser(requestDTO: any) {
    return this.http.post('https://dev.mosip.io/v1/admin/usermgmt/register', requestDTO);
  }

  ridVerification(requestDTO: any) {
    return this.http.post('https://dev.mosip.io/v1/admin/usermgmt/rid', requestDTO);
  }

  otpValidator(requestDTO: any) {
    return this.http.post('https://dev.mosip.io/v1/authmanager/authenticate/useridOTP', requestDTO);
  }
  passwordCreation(requestDTO: any) {
    return this.http.post('https://dev.mosip.io/v1/admin/usermgmt/password', requestDTO);
  }
}
