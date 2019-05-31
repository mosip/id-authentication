import { Injectable } from '@angular/core';
import { LoginServiceService } from './login-service.service';

@Injectable()
export class AuthService {
  constructor(private loginService: LoginServiceService) {}

  isAuthenticated() {
    if (this.loginService.getAuthTypes() != null) {
      return true;
    } else {
      return false;
    }
  }
}
