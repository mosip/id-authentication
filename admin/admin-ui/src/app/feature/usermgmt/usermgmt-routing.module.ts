import { UserpasswordComponent } from './userpassword/userpassword.component';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UserregistrationComponent } from './userregistration/userregistration.component';
import { RidverificationComponent } from './ridverification/ridverification.component';
import { OtpvalidatorComponent } from './otpvalidator/otpvalidator.component';

const routes: Routes = [

  { path: '', redirectTo: 'userregistration', pathMatch: 'full' },
  { path: 'userregistration', component: UserregistrationComponent },
  { path: 'ridverification', component: RidverificationComponent },
  { path: 'validateotp', component: OtpvalidatorComponent },
  { path: 'createpassword', component: UserpasswordComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UsermgmtRoutingModule { }
