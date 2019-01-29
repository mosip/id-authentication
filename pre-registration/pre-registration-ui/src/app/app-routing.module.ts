import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { FaqComponent } from './core/faq/faq.component';
import { AboutUsComponent } from './core/about-us/about-us.component';
import { ContactComponent } from './core/contact/contact.component';
import { ErrorComponent } from './shared/error/error.component';
import { ParentComponent } from './shared/parent/parent.component';
import { PreviewComponent } from './feature/components/preview/preview.component';
import { AcknowledgementComponent } from './feature/components/acknowledgement/acknowledgement.component';
// import { DashBoardComponent } from './registration/dashboard/dashboard.component';

const appRoutes: Routes = [
  // { path: 'dashboard/:id', component: DashBoardComponent },
  { path: 'about-us', component: AboutUsComponent },
  { path: 'faq', component: FaqComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'error', component: ErrorComponent },

  {
    path: 'pre-registration/:id',
    component: ParentComponent,
    children: [
      //   { path: '', pathMatch: 'full', redirectTo: '/' },
      //   { path: 'demographic', component: DemographicComponent },
      // { path: 'file-upload', component: FileUploadComponent },
      { path: 'preview', component: PreviewComponent },
      // { path: 'pick-center', component: CenterSelectionComponent },
      // { path: 'pick-time', component: TimeSelectionComponent },
      { path: 'acknowledgement', component: AcknowledgementComponent }
    ]
  }
  // { path: '**', redirectTo: '/' }
];

@NgModule({
  imports: [RouterModule.forRoot(appRoutes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
