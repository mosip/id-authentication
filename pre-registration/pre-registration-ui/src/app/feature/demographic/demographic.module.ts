import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatKeyboardModule } from 'ngx7-material-keyboard';

import { DemographicRoutingModule } from './demographic-routing.module';
import { SharedModule } from 'src/app/shared/shared.module';
import { DemographicComponent } from './demographic/demographic.component';

@NgModule({
  declarations: [DemographicComponent],
  imports: [CommonModule, DemographicRoutingModule, ReactiveFormsModule, SharedModule, MatKeyboardModule]
})
export class DemographicModule {}
