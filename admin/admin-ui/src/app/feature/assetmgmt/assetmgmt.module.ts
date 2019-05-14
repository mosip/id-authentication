import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AssetmgmtRoutingModule } from './assetmgmt-routing.module';
import { MachineComponent } from './machine/machine.component';
import { DevicesComponent } from './devices/devices.component';
import { UsersComponent } from './users/users.component';
import { AssetmgmtComponent } from './assetmgmt/assetmgmt.component';

@NgModule({
  imports: [
    CommonModule,
    AssetmgmtRoutingModule
  ],
  declarations: [MachineComponent, DevicesComponent, UsersComponent, AssetmgmtComponent]
})
export class AssetmgmtModule { }
