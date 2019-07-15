import { Injectable, Injector } from '@angular/core';
import { UnloadDeactivateGuardService } from '../unload-guard/unload-deactivate-guard.service';
import { AuthService } from 'src/app/auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export abstract class BookingDeactivateGuardService extends UnloadDeactivateGuardService {
  abstract get canDeactivateFlag(): boolean;
  canDeactivate(): boolean {
    if (!this.canDeactivateFlag) return true;
    else return false;
  }
}
