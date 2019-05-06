import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UserregistrationComponent } from './userregistration/userregistration.component';

const routes: Routes = [

  { path: '', redirectTo: 'userregistration', pathMatch: 'full' },
  { path: 'userregistration', component: UserregistrationComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UsermgmtRoutingModule { }
