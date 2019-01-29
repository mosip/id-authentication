import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { FileUploadComponent } from './file-upload/file-upload.component';
import { ParentComponent } from 'src/app/shared/parent/parent.component';

const routes: Routes = [
  {
    path: 'pre-registration/:id',
    component: ParentComponent,
    children: [{ path: 'file-upload', component: FileUploadComponent }]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class FileUploadRoutingModule {}
