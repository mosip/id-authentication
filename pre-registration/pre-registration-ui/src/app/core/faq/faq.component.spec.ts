import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FaqComponent } from './faq.component';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpLoaderFactory } from 'src/app/i18n.module';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { MaterialModule } from 'src/app/material.module';
import { DataStorageService } from '../services/data-storage.service';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';

class MockService {
  use() {}
  url = 'some/url/here';

  getSecondaryLanguageLabels() {
    return of({});
  }
}

describe('FaqComponent', () => {
  let component: FaqComponent;
  let fixture: ComponentFixture<FaqComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FaqComponent],
      schemas: [NO_ERRORS_SCHEMA],
      imports: [
        TranslateModule.forRoot({
          loader: {
            provide: TranslateLoader,
            useFactory: HttpLoaderFactory,
            deps: [HttpClient]
          }
        }),
        HttpClientModule,
        RouterTestingModule,
        MaterialModule
      ],
      providers: [{ provide: DataStorageService, useValue: MockService }]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FaqComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
