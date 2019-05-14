import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MasterdataRoutingModule } from './masterdata-routing.module';
import { MasterdataComponent } from './masterdata/masterdata.component';
import { SharedModule } from '../../shared/shared.module';

@NgModule({
  imports: [
    CommonModule,
    MasterdataRoutingModule,
    SharedModule
  ],
  declarations: [MasterdataComponent]
})
export class MasterdataModule { }
