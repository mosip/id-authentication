import { Injectable } from '@angular/core';
import { Subject, BehaviorSubject } from 'rxjs';
import { UserModel } from '../../shared/models/demographic-model/user.modal';
import { DocumentTypeModel } from '../../shared/models/document-type.modal';

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


  getMessage() {
    return this.messageSource.asObservable();
  }



  changeMessage(message: Object) {
    this.messageSource.next(message);
  }

  setLoginId(id: string) {
    this.loginId = id;
  }

  getLoginId() {
    return this.loginId;
  }

  flushUsers() {
    this.users.length = 0;
  }

  getUser(index: number) {
    return this.users[index];
  }

  getUsers() {
    return this.users.slice();
  }

  addUser(user: UserModel) {
    this.users.push(user);
    this.usersChanged.next(this.users.slice());
  }

  addUsers(users: UserModel[]) {
    this.users = users;
    this.usersChanged.next(this.users.slice());
  }

  updateUser(index: number, newUser: UserModel) {
    this.users[index] = newUser;
    this.usersChanged.next(this.users.slice());
  }

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
