import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
@Injectable()
export class UserregistrationService {

  constructor(private http: HttpClient) { }

  getGenderTypes() {
    return this.http.get('https://qa.mosip.io/v1/masterdata/gendertypes');
  }

  registerUser(requestDTO: any) {
    return this.http.post('http://localhost:8098/v1/admin/usermgmt/register', requestDTO);
  }

  ridVerification(requestDTO: any) {
    return this.http.post('http://localhost:8098/v1/admin/usermgmt/rid', requestDTO);
  }

  otpValidator(requestDTO: any) {
    return this.http.post('http://localhost:8091/v1/authmanager/authenticate/useridOTP', requestDTO);
  }
}
