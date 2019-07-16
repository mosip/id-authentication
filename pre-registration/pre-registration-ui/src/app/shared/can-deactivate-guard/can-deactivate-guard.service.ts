import { Injectable } from '@angular/core';
import { UnloadDeactivateGuardService } from './unload-guard/unload-deactivate-guard.service';
import { CanDeactivate } from '@angular/router';
import { AuthService } from 'src/app/auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class CanDeactivateGuardService implements CanDeactivate<UnloadDeactivateGuardService> {
  constructor(private authService: AuthService) {}

  canDeactivate(component: UnloadDeactivateGuardService): boolean {
    if (this.authService.isAuthenticated() && !component.canDeactivate()) {
      // if (confirm('You have unsaved changes! If you leave, your changes will be lost.')) {
      //   return true;
      // } else {
      //   return false;
      // }
    }
    return true;
  }
}
