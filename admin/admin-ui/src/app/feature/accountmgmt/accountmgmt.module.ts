import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AccountmgmtRoutingModule } from './accountmgmt-routing.module';
import { AccountmgmtComponent } from './accountmgmt/accountmgmt.component';

@NgModule({
  imports: [
    CommonModule,
    AccountmgmtRoutingModule
  ],
  declarations: [AccountmgmtComponent]
})
export class AccountmgmtModule { }
