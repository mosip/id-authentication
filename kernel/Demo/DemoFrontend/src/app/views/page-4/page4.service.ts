import { Injectable } from '@angular/core';
import { Headers, Http } from '@angular/http';
import 'rxjs/add/operator/toPromise';
import { environment } from '../../../environments/environment';

@Injectable()
export class Page4Service {
  private headers = new Headers({ 'Content-Type': 'application/json' });
  private Url = environment.API_URL;
  constructor(private http: Http) { }
  submit(enroll): Promise<any> {
    return this.http.post(this.Url+"/registrations",enroll)
      .toPromise()
      .then(response => response.json())
      .catch(this.handleError);
  }

  getCenters(): Promise<any> {
    return this.http.get(this.Url+"enrolmentcenters")
      .toPromise()
      .then(response => response.json())
      .catch(this.handleError);
  }

  generateOTP(otp): Promise<any> {
    return this.http.post(this.Url+"/getOtp",otp)
      .toPromise()
      .then(response => response.json())
      .catch(this.handleError);
  }

  validateOTP(key,otp): Promise<any> {
    return this.http.get(this.Url+"/validateOtp?key="+key+"&otp="+otp)
      .toPromise()
      .then(response => response.json())
      .catch(this.handleError);
  }
  securityDemo(): Promise<any> {
    return this.http.get(this.Url+"/securitydemo")
      .toPromise()
      .then(response => response.json())
      .catch(this.handleError);
  }
  zipDemo(): Promise<any> {
    return this.http.get(this.Url+"/zipdemo")
      .toPromise()
      .then(response => response.json())
      .catch(this.handleError);
  }
  jsonDemo(): Promise<any> {
    return this.http.get(this.Url+"/jsondemo")
      .toPromise()
      .then(response => response.json())
      .catch(this.handleError);
  }
  daoDemo(): Promise<any> {
    return this.http.get(this.Url+"/dataaccessdemo")
      .toPromise()
      .then(response => response.json())
      .catch(this.handleError);
  }
  private handleError(error: any): Promise<any> {
    return Promise.reject(error.message || error);
  }
}
