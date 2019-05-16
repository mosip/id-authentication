import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';

/**
 * @description This service provides the method for account management viz. Sending OTP, Getting userID, Password Management etc.
 * @author Sagar Mahapatra
 *
 * @export
 * @class AccountManagementService
 */
@Injectable()
export class AccountManagementService {

  forwardSlash = '/';
  baseURL = 'https://dev.mosip.io/v1/admin/accountmanagement/';
  baseAuthUrl = 'https://dev.mosip.io/v1/authmanager/authenticate/';

  constructor(private httpClient: HttpClient) { }
  /**
   * @description Method to get username from mobile number.
   *
   * @param phoneNumber phone Number for which the user name needs to be retrieved.
   * @returns {Observable<any>} the observable response
   * @memberof AccountManagementService
   */
  getUserNameFromPhoneNumber(phoneNumber: number): Observable<any> {
    return this.httpClient.get<any>(this.baseURL + 'username' + this.forwardSlash + phoneNumber)
    .catch(this.errorHandler);
  }

  /**
   * @description Method to send OTP through different channels(SMS, E-mail).
   *
   * @param sendOTPRequest the request object.
   * @returns {Observable<any>} the observable response
   * @memberof AccountManagementService
   */
  sendOTP(sendOTPRequest: any): Observable<any> {
    return this.httpClient.post(this.baseAuthUrl + 'sendotp', sendOTPRequest).catch(this.errorHandler);
  }

  /**
   * @description Method for OTP Authentication(Validation).
   *
   * @param validateOTPRequest the request object.
   * @returns {Observable<any>} the observable response
   * @memberof AccountManagementService
   */
  validateOTP(validateOTPRequest: any): Observable<any> {
    return this.httpClient.post(this.baseAuthUrl + 'useridOTP', validateOTPRequest).catch(this.errorHandler);
  }

  /**
   * @description Method to get username based on user ID.
   *
   * @param userId the user ID for which username needs to be retrieved.
   * @returns {Observable<any>} the observable response
   * @memberof AccountManagementService
   */
  forgotUserName(userId: string): Observable<any> {
    return this.httpClient.get<any>(this.baseURL + '?userId=' + userId).catch(this.errorHandler);

  }

  /**
   * @description Method to change password.
   *
   * @param changePasswordRequest the request object.
   * @returns {Observable<any>} the observable response
   * @memberof AccountManagementService
   */
  changePassword(changePasswordRequest: any): Observable<any> {
    return this.httpClient.post('', changePasswordRequest).catch(this.errorHandler);
  }

  /**
   * @description Method to reset password.
   *
   * @param resetPasswordRequest the request object.
   * @returns {Observable<any>} the observable response
   * @memberof AccountManagementService
   */
  resetPassword(resetPasswordRequest: any): Observable<any> {
    return this.httpClient.post(this.baseURL + 'resetpassword', resetPasswordRequest).catch(this.errorHandler);
  }

  /**
   * @description Method to handle HTTP Errors.
   *
   * @param error the error.
   * @returns {Observable<any>} the observable error message else 'Server Error'.
   * @memberof AccountManagementService
   */
  errorHandler(error: HttpErrorResponse): Observable<any> {
    return Observable.throw(error.message || 'Server Error').catch(this.errorHandler);
  }
}
