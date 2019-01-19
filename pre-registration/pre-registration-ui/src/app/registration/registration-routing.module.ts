import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashBoardComponent } from './dashboard/dashboard.component';
import { DemographicComponent } from './demographic/demographic.component';
import { FileUploadComponent } from './file-upload/file-upload.component';
import { CenterSelectionComponent } from './center-selection/center-selection.component';
import { ParentComponent } from './parent/parent.component';
import { AcknowledgementComponent } from '../acknowledgement/acknowledgement.component';
import { TimeSelectionComponent } from './time-selection/time-selection.component';
import { PreviewComponent } from './preview/preview.component';

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
