import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { AssetmgmtComponent } from "./assetmgmt/assetmgmt.component";
import { MachineComponent } from "./machine/machine.component";
import { DevicesComponent } from "./devices/devices.component";
import { UsersComponent } from "./users/users.component";

const routes: Routes = [
  {
    path: "assetmanagement",
    component: AssetmgmtComponent,
    children: [
      { path: "", redirectTo: "machine", pathMatch: "full" },
      { path: "machine", component: MachineComponent },
      { path: "device", component: DevicesComponent },
      { path: "user", component: UsersComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AssetmgmtRoutingModule {}
