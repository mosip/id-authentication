import { Injectable, Injector } from '@angular/core';
import { AccountManagementService } from './account-management.service';
import { GetContactService } from './get-contact.service';
import { Observable } from 'rxjs/Observable';

/**
 * @description This service aggregates all Angular services related to Account Management inside FacadeService
 *  and resolve their instances from Angular DI using Injector.
 * @author Sagar Mahapatra
 *
 * @export
 * @class FacadeService
 */
@Injectable()
export class FacadeService {

  constructor(private injector: Injector) { }

  private getAccountManagementService: AccountManagementService;
  private getContactService: GetContactService;

  /**
   * @description This method returns an instance of GetContactService.
   * @memberof FacadeService
   * @returns GetContactService instance
   */
  public get contactService(): GetContactService {
    if (!this.getContactService) {
      this.getContactService = this.injector.get(GetContactService);
    }
    return this.getContactService;
  }

  /**
   * @description This method returns an instance of AccountManagementService.
   * @memberof FacadeService
   * @returns AccountManagementService instance
   */
  public get accountManagementService(): AccountManagementService {
    if (!this.getAccountManagementService) {
      this.getAccountManagementService = this.injector.get(AccountManagementService);
    }
    return this.getAccountManagementService;
  }

  /**
   * @description Getter for contact number.
   * @memberof FacadeService
   * @returns the contact number.
   */
  getContact() {
    return this.contactService.getContactNumber();
  }

  /**
   * @description Setter for contact number.
   * @memberof FacadeService
   * @param contactNumber the contact number to be set
   */
  setContact(contactNumber: number) {
    this.contactService.setContactNumber(contactNumber);
  }

  /**
   * @description Getter for user ID.
   * @memberof FacadeService
   */
  getUserID() {
    return this.contactService.getUserId();
  }

  /**
   * @description Setter for user ID.
   * @memberof FacadeService
   * @param userID the user ID to be set
   */
  setUserID(userID: string) {
    this.contactService.setUserId(userID);
  }

  /**
   * @description Getter for user name based on phone number.
   * @memberof FacadeService
   * @param phoneNumber the phone number to be set
   */
  getUserNameFromPhoneNumber(phoneNumber: number): Observable<any> {
    return this.accountManagementService.getUserNameFromPhoneNumber(phoneNumber);
  }

  /**
   * @description Method to send OTP through different channels(SMS, E-mail).
   *
   * @param sendOTPRequest the request object.
   * @returns {Observable<any>} the observable response
   * @memberof FacadeService
   */
  sendOTP(sendOTPRequest: any): Observable <any> {
    return this.accountManagementService.sendOTP(sendOTPRequest);
  }

  /**
   * @description Method for OTP Authentication(Validation).
   *
   * @param validateOTPRequest the request object.
   * @returns {Observable<any>} the observable response
   * @memberof FacadeService
   */
  validateOTP(validateOTPRequest: any): Observable <any> {
    return this.accountManagementService.validateOTP(validateOTPRequest);
  }

  /**
   * @description Method to reset password.
   *
   * @param resetPasswordRequest the request object.
   * @returns {Observable<any>} the observable response
   * @memberof FacadeService
   */
  resetPassword(resetPasswordRequest: any): Observable <any> {
    return this.accountManagementService.resetPassword(resetPasswordRequest);
  }
}
