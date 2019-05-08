import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DemographicComponent } from './demographic/demographic.component';

const routes: Routes = [{ path: '', component: DemographicComponent }];

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
