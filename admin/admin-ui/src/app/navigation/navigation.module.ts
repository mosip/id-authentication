import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NavigationRoutingModule } from './navigation-routing.module';
import { LoginComponent } from './login/login.component';
import { AuthenticationComponent } from './authentication/authentication.component';
import { MaterialModule } from '../material.module';
import { AppRoutingModule } from '../app-routing.module';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';

@NgModule({
  imports: [
    CommonModule,
    NavigationRoutingModule,
    MaterialModule,
    AppRoutingModule
  ],
  declarations: [LoginComponent, AuthenticationComponent, ForgotPasswordComponent, ResetPasswordComponent]
})
export class NavigationModule { }
