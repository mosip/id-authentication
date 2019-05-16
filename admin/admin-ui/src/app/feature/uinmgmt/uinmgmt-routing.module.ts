import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UinComponent } from './uin/uin.component';

const routes: Routes = [
  { path: '', redirectTo: 'uinstatus', pathMatch: 'full' },
  { path: 'uinstatus', component: UinComponent }
];



@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UinmgmtRoutingModule { }
