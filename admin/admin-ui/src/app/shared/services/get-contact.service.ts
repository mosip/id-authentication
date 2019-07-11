import { Injectable } from '@angular/core';

/**
 * @description This service provides setter and getter methods for handling
 * phoneNumber entered by user and UserID for that specific phone number.
 * @author Sagar Mahapatra
 *
 * @export
 * @class GetContactService
 */
@Injectable()
export class GetContactService {

  private contactNumber: number;
  private userId: string;

  constructor() { }

  /**
   * @description Setter for contact number.
   *
   * @param contactNumber the number to be set.
   * @memberof GetContactService
   */
  setContactNumber(contactNumber: number): void {
    this.contactNumber = contactNumber;
  }

  /**
   * @description Getter for contact number.
   * @memberof GetContactService
   */
  getContactNumber(): number {
    return this.contactNumber;
  }

  /**
   * @description Getter for user ID.
   * @memberof GetContactService
   */
  getUserId(): string {
    return this.userId;
  }

  /**
   * @description Setter for user ID.
   *
   * @param userId the user ID to be set.
   * @memberof GetContactService
   */
  setUserId(userId: string) {
    this.userId = userId;
  }
}

