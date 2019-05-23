import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpHeaders,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { loginURL, admin_base_url, logoutUrl } from '../../app.constants';
import { RequestModel } from '../models/request-model';
import { Router } from '@angular/router';
const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
};
@Injectable()
export class LoginServiceService {
  username: string;
  authtype: string[];
  constructor(private http: HttpClient, private router: Router) {}

  setAuthTypes(authType: string[]) {
    this.authtype = authType;
  }

  getAuthTypes() {
    return this.authtype;
  }
  setUserName(username: string) {
    this.username = username;
  }
  getUserName() {
    return this.username;
  }
  login() {
    return this.http
      .get(loginURL.userRole + `${this.username}`)
      .catch(this.errorHandler);
  }
  validateUserIdPassword(dto: RequestModel) {
    return this.http
      .post(loginURL.userIdpasswd, dto, httpOptions)
      .catch(this.errorHandler);
  }
  sendOtp(dto: RequestModel) {
    return this.http
      .post(loginURL.sendOtp, dto, httpOptions)
      .catch(this.errorHandler);
  }
  verifyOtp(dto: RequestModel) {
    return this.http
      .post(loginURL.verifyOtp, dto, httpOptions)
      .catch(this.errorHandler);
  }
  logout() {
    return this.http.post(logoutUrl, '');
  }
  errorHandler(error: HttpErrorResponse): Observable<any> {
    return Observable.throw(error.message || 'Server Error').catch(
      this.errorHandler
    );
  }
}
