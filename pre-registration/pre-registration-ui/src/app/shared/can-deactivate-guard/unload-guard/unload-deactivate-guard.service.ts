import { Injectable, HostListener } from '@angular/core';
import { AuthService } from 'src/app/auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export abstract class UnloadDeactivateGuardService {
  abstract canDeactivate(): boolean;

  @HostListener('window:beforeunload', ['$event'])
  unloadNotification($event: any) {
    console.log('$event', $event);
    if (!this.canDeactivate()) {
      $event.returnValue = true;
    }
  }
}
