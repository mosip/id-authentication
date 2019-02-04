import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AboutUsComponent } from './about-us/about-us.component';
import { FaqComponent } from './faq/faq.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { ContactComponent } from './contact/contact.component';
import { AppRoutingModule } from '../app-routing.module';

import { SharedModule } from '../shared/shared.module';

@NgModule({
  imports: [CommonModule, AppRoutingModule, SharedModule],
  declarations: [HeaderComponent, FooterComponent, AboutUsComponent, FaqComponent, ContactComponent],
  exports: [HeaderComponent, FooterComponent, SharedModule]
})
export class CoreModule {}
