import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { DataStorageService } from '../services/data-storage.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  constructor(public authService: AuthService, private translate: TranslateService, private router: Router) {
    this.translate.use(localStorage.getItem('langCode'));
  }

  ngOnInit() {}

  // onLogout() {
  //   console.log('Logging out');
  // }

  onLogoClick() {
    if (this.authService.isAuthenticated()) {
      console.log('IF LOGO');
      this.router.navigate(['dashboard']);
      // this.doLogout();
    } else {
      // window.location.reload();
      this.router.navigate(['/']);
    }
  }

  onHome() {
    let homeURL = '';
    // const route_parts = this.router.url.split('/');
    // if (route_parts[2]) {
    homeURL = 'dashboard';
    // } else {
    // homeURL = '/';
    // }
    this.router.navigate([homeURL]);
  }

  // removeToken() {
  //   localStorage.setItem('loggedIn', 'false');
  // }

  doLogout() {
    this.authService.onLogout();
  }
}
