import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CenterSelectionComponent } from './center-selection/center-selection.component';
import { TimeSelectionComponent } from './time-selection/time-selection.component';
import { CanDeactivateGuardService } from 'src/app/shared/can-deactivate-guard/can-deactivate-guard.service';

const routes: Routes = [
  { path: 'pick-center', component: CenterSelectionComponent, canDeactivate: [CanDeactivateGuardService] },
  { path: 'pick-time', component: TimeSelectionComponent, canDeactivate: [CanDeactivateGuardService] }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BookingRoutingModule {}
