import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FileUploadRoutingModule } from './file-upload-routing.module';
import { FileUploadComponent } from './file-upload/file-upload.component';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
  declarations: [FileUploadComponent],
  imports: [CommonModule, FileUploadRoutingModule, SharedModule]
})
export class FileUploadModule {}
