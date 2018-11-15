import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest } from '@angular/common/http';
import { Applicant } from './dashboard/dashboard.modal';
import { IdentityModel } from './demographic/identity.model';

@Injectable({
  providedIn: 'root'
})
export class RegistrationService {
  constructor(private httpClient: HttpClient) {}

  // obj: JSON;  yyyy-MM-ddTHH:mm:ss.SSS+000
  obj = {
    identity: {
      FullName: [
        {
          language: 'ar',
          label: 'الاسم الاول',
          value: 'ابراهيم'
        },
        {
          language: 'fr',
          label: 'Prénom',
          value: 'Ibrahim'
        }
      ],
      dateOfBirth: [
        {
          language: 'ar',
          label: 'تاريخ الميلاد',
          value: '18/03/1995'
        },
        {
          language: 'fr',
          label: 'date de naissance',
          value: '18/03/1995'
        }
      ],
      gender: [
        {
          language: 'ar',
          label: 'جنس',
          value: 'الذكر'
        },
        {
          language: 'fr',
          label: 'Le sexe',
          value: 'Male'
        }
      ],
      addressLine1: [
        {
          language: 'ar',
          label: 'العنوان السطر 1',
          value: 'عالمي'
        },
        {
          language: 'fr',
          label: 'Adresse 1',
          value: 'Global'
        }
      ],
      addressLine2: [
        {
          language: 'ar',
          label: 'سطر العنوان 2',
          value: 'قرية'
        },
        {
          language: 'fr',
          label: 'Adresse 2',
          value: 'Village'
        }
      ],
      addressLine3: [
        {
          language: 'ar',
          label: 'العنوان الخط 3',
          value: 'ابراهيم'
        },
        {
          language: 'fr',
          label: 'Adresse 3',
          value: 'Mindtree'
        }
      ],
      region: [
        {
          language: 'ar',
          label: 'منطقة',
          value: 'أودوبي'
        },
        {
          language: 'fr',
          label: 'Prénom',
          value: 'Udupi'
        }
      ],
      province: [
        {
          language: 'ar',
          label: 'المحافظة',
          value: 'كوب'
        },
        {
          language: 'fr',
          label: 'Prénom',
          value: 'Koppa'
        }
      ],
      city: [
        {
          language: 'ar',
          label: 'مدينة',
          value: 'مانجالور'
        },
        {
          language: 'fr',
          label: 'Prénom',
          value: 'Mangalore'
        }
      ],
      postalcode: [
        {
          language: 'ar',
          label: 'الكود البريدى',
          value: '675123'
        },
        {
          language: 'fr',
          label: 'Prénom',
          value: '675123'
        }
      ],
      localAdministrativeAuthority: [
        {
          language: 'ar',
          label: 'السلطة الإدارية المحلية',
          value: 'إدارة'
        },
        {
          language: 'fr',
          label: 'Prénom',
          value: 'La gestion'
        }
      ],
      emailId: [
        {
          language: 'ar',
          label: 'البريد الإلكتروني',
          value: 'ram@gmail.com'
        },
        {
          language: 'fr',
          label: 'Email',
          value: 'ram@gmail.com'
        }
      ],
      mobileNumber: [
        {
          language: 'ar',
          label: 'رقم الهاتف المحمول',
          value: '984839347'
        },
        {
          language: 'fr',
          label: 'Numéro de portable',
          value: '984839347'
        }
      ],
      CNEOrPINNumber: [
        {
          language: 'ar',
          label: 'الرقم السري',
          value: '45667677'
        },
        {
          language: 'fr',
          label: 'Code PIN',
          value: '45667677'
        }
      ],
      age: [
        {
          language: 'ar',
          label: 'عمر',
          value: '35'
        },
        {
          language: 'fr',
          label: 'Âge',
          value: '35'
        }
      ]
    }
  };

  getUsers() {
    return this.httpClient.get<Applicant[]>('https://pre-reg-df354.firebaseio.com/applications.json', {
      observe: 'body',
      responseType: 'json'
    });
  }

  addUser(identity: any) {
    // http://preregistration.southindia.cloudapp.azure.com/dev-demographic/
    console.log('from Service ', identity);

    const req = new HttpRequest(
      'POSt',
      'http://preregistration.southindia.cloudapp.azure.com/dev-demographic/v0.1/pre-registration/applications',
      this.obj,
      {
        reportProgress: true
      }
    );
    return this.httpClient.request(req);
  }
}
