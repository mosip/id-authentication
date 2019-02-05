import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CenterSelectionComponent } from './center-selection/center-selection.component';
import { TimeSelectionComponent } from './time-selection/time-selection.component';

const routes: Routes = [
  { path: 'pick-center', component: CenterSelectionComponent },
  { path: 'pick-time', component: TimeSelectionComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BookingRoutingModule {}
