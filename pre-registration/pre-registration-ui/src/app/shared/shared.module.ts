import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { I18nModule } from '../i18n.module';
import { MaterialModule } from '../material.module';
import { DialougComponent } from './dialoug/dialoug.component';
import { ErrorComponent } from './error/error.component';
import { StepperComponent } from './stepper/stepper.component';
import { ParentComponent } from './parent/parent.component';

/**
 * @description This is the shared module, which comprises of all the components which are used in multiple modules and components.
 * @author Shashank Agrawal
 *
 * @export
 * @class SharedModule
 */
@NgModule({
  imports: [CommonModule, FormsModule, ReactiveFormsModule, MaterialModule, I18nModule, RouterModule],
  declarations: [DialougComponent, ErrorComponent, StepperComponent, ParentComponent],
  exports: [DialougComponent, StepperComponent, MaterialModule, I18nModule, ParentComponent, StepperComponent],
  entryComponents: [DialougComponent]
})
export class SharedModule {}
