import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { UserIdleService } from 'angular-user-idle';
import { DataStorageService } from '../core/services/data-storage.service';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  myProp = new BehaviorSubject<boolean>(false);
  constructor(
    private router: Router,
    private dataStorageService: DataStorageService,
    private userIdle: UserIdleService
  ) {}

  myProp$ = this.myProp.asObservable();
  token: string;

  setToken() {
    this.token = 'settingToken';
    this.myProp.next(true);
  }

  removeToken() {
    this.token = null;
    this.myProp.next(false);
  }

  isAuthenticated() {
    return this.token != null;
  }

  onLogout() {
    localStorage.setItem('loggedIn', 'false');
    localStorage.setItem('loggedOut', 'true');
    this.removeToken();
    this.dataStorageService.onLogout().subscribe();
    this.router.navigate(['/']);
    this.userIdle.stopWatching();
  }
}
