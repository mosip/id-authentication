import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DemographicComponent } from './demographic/demographic.component';
import { CanDeactivateGuardService } from 'src/app/shared/can-deactivate-guard/can-deactivate-guard.service';

const routes: Routes = [{ path: '', component: DemographicComponent, canDeactivate: [CanDeactivateGuardService] }];

/**
 * @description This module defines the route path for the demographic module.
 * @author Shashank Agrawal
 *
 * @export
 * @class DemographicRoutingModule
 */
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DemographicRoutingModule {}
