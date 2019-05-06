import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UsermgmtRoutingModule } from './usermgmt-routing.module';
import { UserregistrationComponent } from './userregistration/userregistration.component';
import { MaterialModule } from '../../material.module';

@NgModule({
  imports: [
    CommonModule,
    UsermgmtRoutingModule,
    MaterialModule
  ],
  declarations: [UserregistrationComponent]
})
export class UsermgmtModule { }
