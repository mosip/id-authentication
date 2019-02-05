import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  constructor(public authService: AuthService, private router: Router, private translate: TranslateService) {
    // this.translate.use(localStorage.getItem('langCode'));
  }

  ngOnInit() {}

  onLogout() {
    console.log('Logging out');
  }

  onHome() {
    let homeURL = '';
    const route_parts = this.router.url.split('/');
    if (route_parts[2]) {
      homeURL = 'dashboard/' + route_parts[2];
    } else {
      homeURL = '/';
    }
    this.router.navigate([homeURL]);
  }

  removeToken() {
    localStorage.setItem('loggedIn', 'false');
  }

  doLogout() {
    localStorage.setItem('loggedIn', 'false');
    this.router.navigate(['/']);
  }
}
