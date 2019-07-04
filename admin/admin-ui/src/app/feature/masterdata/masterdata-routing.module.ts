import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { MasterdataComponent } from './masterdata/masterdata.component';
import { CategoriesComponent } from './categories/categories.component';

const routes: Routes = [
  { path: '', redirectTo: 'categories', pathMatch: 'full' },
  { path: 'masterdata/:id', component: MasterdataComponent },
  { path: 'categories', component: CategoriesComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MasterdataRoutingModule { }
