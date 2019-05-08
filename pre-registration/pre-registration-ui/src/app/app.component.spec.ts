import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { AppComponent } from './app.component';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientModule } from '@angular/common/http';
import { DataStorageService } from './core/services/data-storage.service';
import { SharedModule } from './shared/shared.module';

class MockService {
  use() {}
  url = 'some/url/here';
}

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  beforeEach(() => {
    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [AppComponent],
      imports: [RouterTestingModule, HttpClientModule, SharedModule],
      providers: [{ provide: DataStorageService, useClass: MockService }]
    });
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });
  it('can load instance', () => {
    expect(component).toBeTruthy();
  });
  it('title defaults to: pre-registration', () => {
    expect(component.title).toEqual('pre-registration');
  });
});
