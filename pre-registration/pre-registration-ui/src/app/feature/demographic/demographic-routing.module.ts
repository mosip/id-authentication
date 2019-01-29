import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DemographicComponent } from './demographic/demographic.component';
import { ParentComponent } from 'src/app/shared/parent/parent.component';

const routes: Routes = [
  {
    path: 'pre-registration/:id',
    component: ParentComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: '/' },
      { path: 'demographic', component: DemographicComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DemographicRoutingModule {}
