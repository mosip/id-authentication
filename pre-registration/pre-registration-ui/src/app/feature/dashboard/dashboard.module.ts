import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashBoardComponent } from './dashboard/dashboard.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [DashBoardComponent],
  imports: [CommonModule, DashboardRoutingModule, FormsModule, ReactiveFormsModule, SharedModule]
})
export class DashboardModule {}
