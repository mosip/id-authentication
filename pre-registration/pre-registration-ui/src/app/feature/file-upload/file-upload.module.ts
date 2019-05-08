import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from 'src/app/shared/shared.module';
import { FileUploadRoutingModule } from './file-upload-routing.module';
import { FileUploadComponent } from './file-upload/file-upload.component';

/**
 * @description THis module is responsible for uploading the documents in specified format. This is an optional module.
 *
 * @author Shashank Agrawal
 *
 * @export
 * @class FileUploadModule
 */
@NgModule({
  declarations: [FileUploadComponent],
  imports: [CommonModule, FileUploadRoutingModule, SharedModule]
})
export class FileUploadModule {}
