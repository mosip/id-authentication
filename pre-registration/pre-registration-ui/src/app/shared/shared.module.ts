import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from '../material.module';

import { MapComponent } from './map/map.component';
import { DialougComponent } from './dialoug/dialoug.component';
import { ErrorComponent } from './error/error.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MaterialModule,
  ],
<<<<<<< HEAD
  declarations: [MapComponent, DialougComponent],
  exports: [MapComponent, DialougComponent,MaterialModule]
=======
  declarations: [MapComponent, DialougComponent, ErrorComponent],
  exports: [MapComponent, DialougComponent]
>>>>>>> 5c0fc594fbf3ef1cad35d35a6afd3bfcd7bf5dd5
})
export class SharedModule { }
