import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UinmgmtRoutingModule } from './uinmgmt-routing.module';
import { UinComponent } from './uin/uin.component';
import { ReactiveFormsModule,FormsModule } from '@angular/forms';

@NgModule({
  imports: [
    CommonModule,
    UinmgmtRoutingModule,
     FormsModule ,ReactiveFormsModule
  ],
  declarations: [UinComponent]
})
export class UinmgmtModule { }
