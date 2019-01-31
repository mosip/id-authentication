import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CenterSelectionComponent } from './center-selection/center-selection.component';
import { MapComponent } from './map/map.component';
import { TimeSelectionComponent } from './time-selection/time-selection.component';
import { SharedModule } from 'src/app/shared/shared.module';
import { I18nModule } from 'src/app/i18n.module';
import { MaterialModule } from 'src/app/material.module';

@NgModule({
  declarations: [CenterSelectionComponent, MapComponent, TimeSelectionComponent],
  imports: [
    CommonModule,
    SharedModule
  ]
})
export class BookingModule { }
