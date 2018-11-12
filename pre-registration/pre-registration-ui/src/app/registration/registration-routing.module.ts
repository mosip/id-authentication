import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashBoardComponent } from './dashboard/dashboard.component';
import { DemographicComponent } from './demographic/demographic.component';
import { FileUploadComponent } from './file-upload/file-upload.component';
import { TimeSelectionComponent } from './time-selection/time-selection.component';
import { CenterSelectionComponent } from './center-selection/center-selection.component';

const registrationRoutes: Routes = [
    {
        path: 'registration/:id', component: DashBoardComponent
    },
    {
        path: 'registration', component: DashBoardComponent,  children: [
            { path: 'demographic/:id', component: DemographicComponent },
            { path: 'file-upload', component: FileUploadComponent },
            { path: 'pick-time', component: TimeSelectionComponent },
            { path: 'pick-center', component: CenterSelectionComponent },
        ]
    }];

@NgModule({
    imports: [RouterModule.forChild(registrationRoutes)],
    exports: [RouterModule]
})
export class RegistrationRoutingModule {

}
