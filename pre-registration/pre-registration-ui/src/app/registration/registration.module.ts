import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { RegistrationComponent } from './registration.component';
import { DemographicComponent } from './demographic/demographic.component';
import { FileUploadComponent } from './file-upload/file-upload.component';
import { TimeSelectionComponent } from './time-selection/time-selection.component';
import { CenterSelectionComponent } from './center-selection/center-selection.component';
import { RegistrationRoutingModule } from './registration-routing.module';
import { DialougComponent } from './dialoug/dialoug.component';
import { FormsModule } from '@angular/forms';
import { MaterialModule } from '../material.module';
import { HttpModule } from '@angular/http';
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    RegistrationRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    HttpClientModule
  ],
  exports: [
  ],
  entryComponents: [DialougComponent],
  declarations: [
    RegistrationComponent,
    DemographicComponent,
    FileUploadComponent,
    TimeSelectionComponent,
    CenterSelectionComponent,
    DialougComponent
  ]
})
export class RegistrationModule { }
