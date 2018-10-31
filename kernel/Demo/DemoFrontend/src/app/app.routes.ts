import {Routes} from '@angular/router';

import {blankComponent} from './components/common/layouts/blank.component';
import {basicComponent} from './components/common/layouts/basic.component';

import {Page4Component} from './views/page-4/page4.component';

export const ROUTES : Routes = [
  // Main redirect
  {
    path: '',
    redirectTo: 'enroll',
    pathMatch: 'full'
  }, {
    path: '',
    component: basicComponent,
    children: [

      {
        path: 'enroll',
        component: Page4Component
      }
    ]
  }, {
    path: '',
    component: blankComponent,
    children: [
 {
        path: '**',
        redirectTo: 'enroll'
      }
    ]
  }
];
