import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoginServiceService } from '../../shared/services/login-service.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  username = '';

  constructor(private router: Router, private loginService: LoginServiceService) { }

  ngOnInit() {
    this.username = this.loginService.getUserName();
  }

  logOut() {
   this.router.navigateByUrl('login');
  }

}
