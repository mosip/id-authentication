import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { UserIdleService } from 'angular-user-idle';
import { DataStorageService } from '../core/services/data-storage.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  constructor(
    private router: Router,
    private dataStorageService: DataStorageService,
    private userIdle: UserIdleService
  ) {}

  token: string;

  setToken() {
    this.token = 'settingToken';
  }

  removeToken() {
    this.token = null;
  }

  isAuthenticated() {
    return this.token != null;
    // if (localStorage.getItem('loggedIn') && localStorage.getItem('loggedIn') === 'true') return true;
    // else return false;
  }

  onLogout() {
    localStorage.setItem('loggedIn', 'false');
    localStorage.setItem('loggedOut', 'true');
    this.removeToken();
    this.dataStorageService.onLogout().subscribe(res => console.log(res));
    this.router.navigate(['/']);
    this.userIdle.stopWatching();
  }
}
