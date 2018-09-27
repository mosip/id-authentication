import { Routes } from '@angular/router';
import { LoginpageComponent } from './loginpage/loginpage.component';
import { HomepageComponent } from './homepage/homepage.component';
import {OtpComponent} from './otp/otp.component';

export const ROUTES: Routes = [
    {
       path : '',
       redirectTo : 'login',
       pathMatch : 'full'
    },
    {
        path : 'login',
        component : LoginpageComponent
    },
    {
        path : 'otp',
        component : OtpComponent
    },
    {
        path : 'home',
        component : HomepageComponent
    }
];
