import { BrowserModule } from '@angular/platform-browser';
import { NgModule, APP_INITIALIZER } from '@angular/core';
import { NgxPrintModule } from 'ngx-print';

import { AppComponent } from './app.component';

import { AppRoutingModule } from './app-routing.module';
import { AuthService } from './auth/auth.service';

import { CoreModule } from './core/core.module';
import { AuthModule } from './auth/auth.module';
import { SharedModule } from './shared/shared.module';

import { AppConfigService } from './app-config.service';
// import { DashboardModule } from './feature/dashboard/dashboard.module';
// import { FileUploadModule } from './feature/file-upload/file-upload.module';
// import { DemographicModule } from './feature/demographic/demographic.module';
import { BookingModule } from './feature/booking/booking.module';
import { SharedService } from './feature/booking/booking.service';
import { AcknowledgementComponent } from './feature/components/acknowledgement/acknowledgement.component';
import { PreviewComponent } from './feature/components/preview/preview.component';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

const appInitialization = (appConfig: AppConfigService) => {
  return () => {
    return appConfig.loadAppConfig();
  };
};

@NgModule({
  declarations: [AppComponent, AcknowledgementComponent, PreviewComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    CoreModule,
    AuthModule,
    SharedModule,
    // DashboardModule,
    // FileUploadModule,
    // DemographicModule,
    BookingModule,
    NgxPrintModule
  ],
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
