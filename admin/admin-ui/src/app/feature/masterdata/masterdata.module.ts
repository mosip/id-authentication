import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MasterdataRoutingModule } from './masterdata-routing.module';
import { MasterdataComponent } from './masterdata/masterdata.component';
import { SharedModule } from '../../shared/shared.module';
import { CategoriesComponent } from './categories/categories.component';
import { MaterialModule } from '../../material.module';

@NgModule({
  imports: [
    CommonModule,
    MasterdataRoutingModule,
    SharedModule,
    MaterialModule
  ],
  declarations: [MasterdataComponent, CategoriesComponent]
})
export class MasterdataModule { }
