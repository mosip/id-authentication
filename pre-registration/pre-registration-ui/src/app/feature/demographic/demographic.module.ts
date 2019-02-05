import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DemographicRoutingModule } from './demographic-routing.module';
import { DemographicComponent } from './demographic/demographic.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [DemographicComponent],
  imports: [CommonModule, DemographicRoutingModule, ReactiveFormsModule, SharedModule]
})
export class DemographicModule {}
