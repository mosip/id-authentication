import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';

import { AppRoutingModule } from './app-routing.module';
import { AuthService } from './auth/auth.service';
import { RegistrationModule } from './registration/registration.module';
import { CoreModule } from './core/core.module';
import { AuthModule } from './auth/auth.module';
import { SharedModule } from './shared/shared.module';
import { SharedService } from './shared/shared.service';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RegistrationModule,
    CoreModule,
    AuthModule,
    SharedModule
  ],
  providers: [AuthService, SharedService],
  bootstrap: [AppComponent]
})
export class AppModule {}
