import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { MasterdataComponent } from './masterdata/masterdata.component';

const routes: Routes = [
  { path: '', redirectTo: 'masterdata', pathMatch: 'full' },
  { path: 'masterdata', component: MasterdataComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MasterdataRoutingModule { }
