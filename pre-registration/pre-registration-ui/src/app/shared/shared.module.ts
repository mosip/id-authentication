import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from '../material.module';

import { DialougComponent } from './dialoug/dialoug.component';
import { ErrorComponent } from './error/error.component';
import { I18nModule } from '../i18n.module';
import { StepperComponent } from './stepper/stepper.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MaterialModule,
    I18nModule
  ],
  declarations: [DialougComponent, ErrorComponent, StepperComponent],
  exports: [DialougComponent, StepperComponent, MaterialModule, I18nModule, FormsModule, ReactiveFormsModule]
})
export class SharedModule { }
