import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DashBoardComponent } from './dashboard/dashboard.component';
import { AuthGuardService } from 'src/app/auth/auth-guard.service';

const routes: Routes = [
  {
    path: '',
    component: DashBoardComponent,
    canActivate: [AuthGuardService]
  }
];

/**
 * @description This module defines the route path for the dashboard feature.
 * @author Shashank Agrawal
 *
 * @export
 * @class DashboardRoutingModule
 */
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule {}
