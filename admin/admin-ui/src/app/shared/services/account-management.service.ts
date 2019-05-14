import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';

@Injectable()
export class AccountManagementService {

   forwardSlash = '/';
   baseURL = 'https://dev.mosip.io/v1/admin/accountmanagement/';
// baseURL = 'https://localhost:8900/v1/admin/accountmanagement/';

  constructor(private httpClient: HttpClient) { }
  /*
   * Method to get username from mobile number.
   */
  getUserNameFromPhoneNumber(phoneNumber: number): Observable<any> {
    return this.httpClient.get<any>(this.baseURL + 'username' + this.forwardSlash + phoneNumber)
    .catch(this.errorHandler);
  }

  /*
   * Method to get username based on user ID.
   */
  forgotUserName(userId: string): Observable<any> {
    return this.httpClient.get<any>(this.baseURL + '?userId=' + userId);

  }

  /*
   * Method to change password.
   */
  changePassword(changePasswordRequest: any): Observable<any> {
    return this.httpClient.post('', changePasswordRequest);
  }

  /*
   * Method to reset password.
   */
  resetPassword(resetPasswordRequest: any): Observable<any> {
    return this.httpClient.post('', resetPasswordRequest);
  }

  /*
   * Method to handle HTTP Errors.
   */
  errorHandler(error: HttpErrorResponse) {
    return Observable.throw(error.message || 'Server Error');
  }
}
