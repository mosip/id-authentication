import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

import { DemographicRoutingModule } from './demographic-routing.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { DemographicComponent } from './demographic/demographic.component';

@NgModule({
  declarations: [DemographicComponent],
  imports: [CommonModule, DemographicRoutingModule, ReactiveFormsModule, SharedModule]
})
export class DemographicModule {}
