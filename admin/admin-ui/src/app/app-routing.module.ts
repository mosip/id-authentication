import { NgModule } from '@angular/core';
import { Routes, RouterModule, PreloadAllModules } from '@angular/router';
import { ParentComponent } from './shared/parent/parent.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadChildren: './navigation/navigation.module#NavigationModule'
  },
  {
    path: 'admin',
    component: ParentComponent,
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', loadChildren: './feature/masterdata/masterdata.module#MasterdataModule' },
      { path: 'usermgmt', loadChildren: './feature/usermgmt/usermgmt.module#UsermgmtModule' },
      { path: 'assetmgmt', loadChildren: './feature/assetmgmt/assetmgmt.module#AssetmgmtModule' },
      { path: 'uinmgmt', loadChildren: './feature/uinmgmt/uinmgmt.module#UinmgmtModule' }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true, preloadingStrategy: PreloadAllModules })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
