import { BrowserModule } from '@angular/platform-browser';
import { NgModule, APP_INITIALIZER } from '@angular/core';

import { AppComponent } from './app.component';

import { AppRoutingModule } from './app-routing.module';
import { AuthService } from './auth/auth.service';
import { RegistrationModule } from './registration/registration.module';
import { CoreModule } from './core/core.module';
import { AuthModule } from './auth/auth.module';
import { SharedModule } from './shared/shared.module';
import { SharedService } from './registration/booking/booking.service';
import { AcknowledgementComponent } from './acknowledgement/acknowledgement.component';
import { AppConfigService } from './app-config.service';

const appInitialization = (appConfig: AppConfigService) => {
  return () => {
    return appConfig.loadAppConfig();
  };
};

@NgModule({
  declarations: [AppComponent, AcknowledgementComponent],
  imports: [BrowserModule, AppRoutingModule, RegistrationModule, CoreModule, AuthModule, SharedModule],
  providers: [
    AuthService,
    SharedService,
    AppConfigService,
    {
      provide: APP_INITIALIZER,
      useFactory: appInitialization,
      multi: true,
      deps: [AppConfigService]
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
