import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { DemographicComponent } from './demographic/demographic.component';
import { FileUploadComponent } from './file-upload/file-upload.component';
import { TimeSelectionComponent } from './time-selection/time-selection.component';
import { CenterSelectionComponent } from './center-selection/center-selection.component';
import { RegistrationRoutingModule } from './registration-routing.module';
import { DialougComponent } from './dialoug/dialoug.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../material.module';
import { HttpClientModule } from '@angular/common/http';
import { DashBoardComponent } from './dashboard/dashboard.component';
import { ParentComponent } from './parent/parent.component';
import { EscapeHtmlPipe } from '../shared/keep-html.pipe';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RegistrationRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    HttpClientModule
  ],
  exports: [
  ],
  entryComponents: [DialougComponent],
  declarations: [
    DemographicComponent,
    FileUploadComponent,
    TimeSelectionComponent,
    CenterSelectionComponent,
    DialougComponent,
    DashBoardComponent,
    ParentComponent,
    EscapeHtmlPipe
  ]
})
export class RegistrationModule { }
