import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ParentComponent } from './parent/parent.component';
import { CoreModule } from '../core/core.module';
import { RouterModule } from '@angular/router';
import { TableComponent } from './table/table.component';

@NgModule({
  imports: [
    CommonModule,
    CoreModule,
    RouterModule
  ],
  declarations: [ParentComponent, TableComponent],
  exports: [ParentComponent, TableComponent]
})
export class SharedModule { }
