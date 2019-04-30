import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MasterdataRoutingModule } from './masterdata-routing.module';
import { MasterdataComponent } from './masterdata/masterdata.component';

@NgModule({
  imports: [
    CommonModule,
    MasterdataRoutingModule
  ],
  declarations: [MasterdataComponent]
})
export class MasterdataModule { }
