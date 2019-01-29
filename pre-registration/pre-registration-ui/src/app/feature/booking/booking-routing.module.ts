import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CenterSelectionComponent } from './center-selection/center-selection.component';
import { TimeSelectionComponent } from './time-selection/time-selection.component';
import { ParentComponent } from 'src/app/shared/parent/parent.component';

const routes: Routes = [
  {
    path: 'pre-registration/:id',
    component: ParentComponent,
    children: [
      { path: 'pick-center', component: CenterSelectionComponent },
      { path: 'pick-time', component: TimeSelectionComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BookingRoutingModule {}
