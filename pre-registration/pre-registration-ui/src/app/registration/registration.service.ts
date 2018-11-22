import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Applicant } from './dashboard/dashboard.modal';
import { UserModel } from './demographic/user.model';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  private users: UserModel[] = [];
  usersChanged = new Subject<UserModel[]>();

  constructor(private httpClient: HttpClient) {}

  SEND_FILE_URL = 'http://preregistration-intgra.southindia.cloudapp.azure.com/int-demographic/v0.1/pre-registration/registration/documents';
  BASE_URL = 'http://preregistration-intgra.southindia.cloudapp.azure.com/int-demographic/v0.1/pre-registration/applications';
  // obj: JSON;  yyyy-MM-ddTHH:mm:ss.SSS+000
  // https://pre-reg-df354.firebaseio.com/applications.json
  getUsers(value) {
    value = 'User1';
    return this.httpClient.get<Applicant[]>(this.BASE_URL, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append('userId', value)
    });
  }

  addUser(identity: any) {
    // http://preregistration.southindia.cloudapp.azure.com/dev-demographic/

    const obj = {
      id: 'mosip.pre-registration.demographic.create',
      ver: '1.0',
      reqTime: '2018-10-17T07:22:57.086+0000',
      request: identity
    };

    // const req = new HttpRequest('POST', 'http://A2ML27085:9092/v0.1/pre-registration/applications', obj, {
    //   reportProgress: true // A2ML21989
    // }); // A2ML27085
    // return this.httpClient.request(req);
    return this.httpClient.post(this.BASE_URL, obj);
  }

  sendFile(formdata: FormData) {
    return this.httpClient.post(this.SEND_FILE_URL, formdata);
    // console.log('servvice called', formdata);
  }

  deleteRegistration(preId: string) {
    return this.httpClient.delete(this.BASE_URL, {
      observe: 'body',
      responseType: 'json',
      params: new HttpParams().append('preId', preId)
    });
  }

  setUser(user: UserModel) {
    this.users.push(user);
    this.usersChanged.next(this.users.slice());
  }

  setUsers(users: UserModel[]) {
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

  getUsersUI() {
    return this.users.slice();
  }

  getUsersByIndex(index: number) {
    return this.users[index];
  }
}
