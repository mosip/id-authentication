import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashBoardComponent } from './dashboard/dashboard.component';
import { DemographicComponent } from './demographic/demographic.component';
import { FileUploadComponent } from './file-upload/file-upload.component';
import { CenterSelectionComponent } from './center-selection/center-selection.component';
import { ParentComponent } from './parent/parent.component';

const registrationRoutes: Routes = [
  {
    path: 'registration/:id',
    component: ParentComponent,
    children: [
      { path: '', component: DashBoardComponent },
      { path: 'demographic/:id', component: DemographicComponent },
      { path: 'file-upload', component: FileUploadComponent },
      { path: 'pick-center', component: CenterSelectionComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(registrationRoutes)],
  exports: [RouterModule]
})
export class RegistrationRoutingModule {}
