import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import { CoreModule } from './core/core.module';
import { NavigationModule } from './navigation/navigation.module';
import { SharedModule } from './shared/shared.module';
import { MaterialModule } from './material.module';
import { UsermgmtModule } from './feature/usermgmt/usermgmt.module';
import { AssetmgmtModule } from './feature/assetmgmt/assetmgmt.module';
import { UserregistrationService } from './shared/services/userregistration.service';
import { GetContactService } from '../app/shared/services/get-contact.service';
import { AccountManagementService } from '../app/shared/services/account-management.service';
import { HttpClientModule } from '@angular/common/http';
import { LoginServiceService } from './shared/services/login-service.service';
import { FacadeService } from './shared/services/facade.service';
import { DataStorageService } from './shared/services/data-storage.service';
import { AuthService } from './shared/services/auth-service.service';
@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    CoreModule,
    NavigationModule,
    SharedModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    UsermgmtModule,
    AssetmgmtModule,
    HttpClientModule
  ],
  providers: [
    UserregistrationService,
    GetContactService,
    AccountManagementService,
    FacadeService,
    LoginServiceService,
    DataStorageService,
    AuthService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
