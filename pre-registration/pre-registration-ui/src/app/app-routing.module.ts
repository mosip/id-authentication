import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { FaqComponent } from './core/faq/faq.component';
import { AboutUsComponent } from './core/about-us/about-us.component';
import { ContactComponent } from './core/contact/contact.component';
import { ErrorComponent } from './shared/error/error.component';
import { ParentComponent } from './shared/parent/parent.component';

const appRoutes: Routes = [
  { path: 'dashboard/:id', loadChildren: './feature/dashboard/dashboard.module#DashboardModule' },
  { path: 'about-us', component: AboutUsComponent },
  { path: 'faq', component: FaqComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'error', component: ErrorComponent },

  {
    path: 'pre-registration/:id',
    component: ParentComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: '/' },
      { path: 'demographic', loadChildren: './feature/demographic/demographic.module#DemographicModule' },
      { path: 'file-upload', loadChildren: './feature/file-upload/file-upload.module#FileUploadModule' },
      { path: 'summary', loadChildren: './feature/summary/summary.module#SummaryModule' },
      { path: 'booking', loadChildren: './feature/booking/booking.module#BookingModule' }
    ]
  }
  // { path: '**', redirectTo: '/' }
];

@NgModule({
  imports: [RouterModule.forRoot(appRoutes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
