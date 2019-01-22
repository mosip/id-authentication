import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DemographicComponent } from './demographic/demographic.component';
import { FileUploadComponent } from './file-upload/file-upload.component';
import { ParentComponent } from './parent/parent.component';
import { AcknowledgementComponent } from '../acknowledgement/acknowledgement.component';
import { PreviewComponent } from './preview/preview.component';
import { CenterSelectionComponent } from './booking/center-selection/center-selection.component';
import { TimeSelectionComponent } from './booking/time-selection/time-selection.component';

const registrationRoutes: Routes = [
  {
    path: 'pre-registration/:id',
    component: ParentComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: '/' },
      { path: 'demographic', component: DemographicComponent },
      { path: 'file-upload', component: FileUploadComponent },
      { path: 'preview', component: PreviewComponent },
      { path: 'pick-center', component: CenterSelectionComponent },
      { path: 'pick-time', component: TimeSelectionComponent },
      { path: 'acknowledgement', component: AcknowledgementComponent }
    ]
  },
  { path: '**', redirectTo: '/' }
];

@NgModule({
  imports: [RouterModule.forChild(registrationRoutes)],
  exports: [RouterModule]
})
export class RegistrationRoutingModule {}
