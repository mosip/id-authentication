import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { loginURL } from '../../app.constants';
@Injectable()
export class LoginServiceService {
  userId: string;

  constructor(private http: HttpClient) {}

  login(userId) {
    this.userId = userId;
    return this.http.get(loginURL.userRole + `${this.userId}`);
  }
}
