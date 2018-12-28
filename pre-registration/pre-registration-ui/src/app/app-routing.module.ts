import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { FaqComponent } from './core/faq/faq.component';
import { AboutUsComponent } from './core/about-us/about-us.component';
import { ContactComponent } from './core/contact/contact.component';
<<<<<<< HEAD
import { AcknowledgementComponent } from './acknowledgement/acknowledgement.component';

=======
import { ErrorComponent } from './shared/error/error.component';
>>>>>>> 5c0fc594fbf3ef1cad35d35a6afd3bfcd7bf5dd5

const appRoutes: Routes = [
  { path: 'about-us', component: AboutUsComponent },
  { path: 'faq', component: FaqComponent },
  { path: 'contact', component: ContactComponent },
<<<<<<< HEAD
  {path: 'acknowledgement', component: AcknowledgementComponent}
=======
  { path: 'error', component: ErrorComponent }
>>>>>>> 5c0fc594fbf3ef1cad35d35a6afd3bfcd7bf5dd5
];

@NgModule({
  imports: [RouterModule.forRoot(appRoutes, { useHash: true })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
