import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../material.module';

import { DialougComponent } from './dialoug/dialoug.component';
import { ErrorComponent } from './error/error.component';
import { I18nModule } from '../i18n.module';
import { StepperComponent } from './stepper/stepper.component';
import { ParentComponent } from './parent/parent.component';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MaterialModule, I18nModule, RouterModule],
  declarations: [DialougComponent, ErrorComponent, StepperComponent, ParentComponent],
  exports: [DialougComponent, StepperComponent, MaterialModule, I18nModule, ParentComponent, StepperComponent],
  entryComponents: [DialougComponent]
})
export class SharedModule {}
