import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NavigationRoutingModule } from './navigation-routing.module';
import { LoginComponent } from './login/login.component';
import { AuthenticationComponent } from './authentication/authentication.component';
import { MaterialModule } from '../material.module';
import { AppRoutingModule } from '../app-routing.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { OtpAuthenticationComponent } from './otp-authentication/otp-authentication.component';

@NgModule({
  imports: [
    CommonModule,
    NavigationRoutingModule,
    MaterialModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule
  ],
  declarations: [LoginComponent, AuthenticationComponent, ForgotPasswordComponent, ResetPasswordComponent, OtpAuthenticationComponent],
})
export class NavigationModule { }
