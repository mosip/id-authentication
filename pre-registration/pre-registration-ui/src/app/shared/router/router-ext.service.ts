import { Injectable, OnDestroy } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { Subscription } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RouterExtService implements OnDestroy {
  private previousUrl: string = undefined;
  private currentUrl: string = undefined;
  subscription: Subscription;

  constructor(private router: Router) {
    this.currentUrl = this.router.url;
    this.subscription = router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.previousUrl = this.currentUrl;
        this.currentUrl = event.url;
      }
    });
  }

  public getPreviousUrl(): string {
    return this.previousUrl;
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
