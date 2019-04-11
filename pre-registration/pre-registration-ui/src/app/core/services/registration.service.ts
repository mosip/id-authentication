import { Injectable } from '@angular/core';
import { Subject, BehaviorSubject } from 'rxjs';
import { UserModel } from '../../shared/models/demographic-model/user.modal';
import { DocumentTypeModel } from '../../shared/models/document-type.modal';

/**
 * @author Shashank Agrawal
 * @description This service is to perform multiple operations like add, update, delete on the user array.
 *
 * @export
 * @class RegistrationService
 */
@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  private loginId: string;
  usersChanged = new Subject<UserModel[]>();
  private messageSource = new BehaviorSubject({});
  currentMessage = this.messageSource.asObservable();
  private users: UserModel[] = [];
  private regCenterId: string;
  sameAs = '';
  documentCategories: DocumentTypeModel[];

  /**
   * @description This method return the message source
   *
   * @returns an `Observable` of the body as an `Object`
   * @memberof RegistrationService
   */
  getMessage() {
    return this.messageSource.asObservable();
  }

  /**
   * @description This method update the message of the messageSource
   *
   * @param {Object} message
   * @memberof RegistrationService
   */
  changeMessage(message: Object) {
    this.messageSource.next(message);
  }

  /**
   * @description This method sets the login id for the appliction.
   *
   * @param {string} id login id
   * @memberof RegistrationService
   */
  setLoginId(id: string) {
    this.loginId = id;
  }

  /**
   * @description This method return the login id that has been set in setLoginId()
   *
   * @returns the login id
   * @memberof RegistrationService
   */
  getLoginId() {
    return this.loginId;
  }

  /**
   * @description This method makes the users array empty.
   *
   * @memberof RegistrationService
   */
  flushUsers() {
    this.users.length = 0;
  }

  /**
   * @description This method return the user from list of array for the given index.
   *
   * @param {number} index
   * @returns the user from the list of array for the provided index.
   * @memberof RegistrationService
   */
  getUser(index: number) {
    return this.users[index];
  }

  /**
   * @description This method return the list of users.
   *
   * @returns the copy of list of users.
   * @memberof RegistrationService
   */
  getUsers() {
    return this.users.slice();
  }

  /**
   * @description This method add the user to the user array.
   *
   * @param {UserModel} user
   * @memberof RegistrationService
   */
  addUser(user: UserModel) {
    this.users.push(user);
    this.usersChanged.next(this.users.slice());
  }

  /**
   * @description This method add the list of users to the user array.
   *
   * @param {UserModel[]} users
   * @memberof RegistrationService
   */
  addUsers(users: UserModel[]) {
    this.users = users;
    this.usersChanged.next(this.users.slice());
  }

  /**
   * @description This method update the user in the given index with the new user data.
   *
   * @param {number} index
   * @param {UserModel} newUser
   * @memberof RegistrationService
   */
  updateUser(index: number, newUser: UserModel) {
    this.users[index] = newUser;
    this.usersChanged.next(this.users.slice());
  }

  /**
   * @description This method deletes the user for the given index.
   *
   * @param {number} index
   * @memberof RegistrationService
   */
  deleteUser(index: number) {
    this.users.splice(index, 1);
    this.usersChanged.next(this.users.slice());
  }

  getUserFiles(index: number) {
    return this.users[index].files.slice();
  }

  setRegCenterId(id: string) {
    this.regCenterId = id;
  }

  getRegCenterId() {
    return this.regCenterId;
  }

  setSameAs(value) {
    this.sameAs = value;
  }

  getSameAs() {
    return this.sameAs;
  }

  setDocumentCategories(documentCategories) {
    this.documentCategories = documentCategories;
    // console.log('document categories', this.documentCategories);
  }

  getDocumentCategories() {
    return this.documentCategories;
  }
}
