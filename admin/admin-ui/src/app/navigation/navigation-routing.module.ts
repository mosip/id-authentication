import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { AuthenticationComponent } from './authentication/authentication.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { OtpAuthenticationComponent } from './otp-authentication/otp-authentication.component';
import {  AuthGuard } from '../shared/auth-guard.guard';

const routes: Routes = [
  {
    path: 'login', component: LoginComponent
  },
  {
    path: 'authenticate/:userId', component: AuthenticationComponent , canActivate: [AuthGuard]
  },
  {
    path: 'forgotpassword', component: ForgotPasswordComponent
  },
  {
    path: 'resetpassword', component: ResetPasswordComponent
  },
  {
    path: 'otpauthentication', component: OtpAuthenticationComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers:[AuthGuard]
})
export class NavigationRoutingModule { }
