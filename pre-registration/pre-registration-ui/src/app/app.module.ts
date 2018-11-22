import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';


import { AppComponent } from './app.component';

import { AppRoutingModule } from './app-routing.module';
import { AuthService } from './auth/auth.service';
import { RegistrationModule } from './registration/registration.module';
import { CoreModule } from './core/core.module';
import { AuthModule } from './auth/auth.module';
import { DraggableDirective } from './registration/file-upload/draggable.directive';

@NgModule({
  declarations: [
    AppComponent,
    DraggableDirective
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    RegistrationModule,
    CoreModule,
    AuthModule
  ],
  providers: [AuthService],
  bootstrap: [AppComponent]
})
export class AppModule { }
