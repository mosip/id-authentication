import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxPrintModule } from 'ngx-print';

import { SummaryRoutingModule } from './summary-routing.module';
import { AcknowledgementComponent } from './acknowledgement/acknowledgement.component';
import { PreviewComponent } from './preview/preview.component';
import { SharedModule } from 'src/app/shared/shared.module';

@NgModule({
  declarations: [
    AcknowledgementComponent,
    PreviewComponent
  ],
  imports: [
    CommonModule,
    SummaryRoutingModule,
    SharedModule,
    NgxPrintModule
  ]
})
export class SummaryModule { }
