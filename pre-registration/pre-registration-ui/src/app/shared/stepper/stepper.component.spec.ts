import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StepperComponent } from './stepper.component';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpLoaderFactory } from 'src/app/i18n.module';
import { HttpClient, HttpClientModule } from '@angular/common/http';

describe('StepperComponent', () => {
  let component: StepperComponent;
  let fixture: ComponentFixture<StepperComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StepperComponent ],
      imports: [
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient]
            }
        }),
        HttpClientModule
    ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StepperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call ngOnChanges lifecycle hook for time selection', () => {

    component.componentName = 'TimeSelectionComponent';
    component.ngOnChanges();
    fixture.detectChanges();
    expect(component.classes.step3.p[0]).toBe('active');   
    
  });
  
  it('should call ngOnChanges lifecycle hook for demographic', () => {
    component.componentName = 'DemographicComponent';
    component.ngOnChanges();
    fixture.detectChanges();
    expect(component.classes.step1.p[0]).toBe('active');
  });

  it('should call ngOnChanges lifecycle hook for File Upload', () => {
    component.componentName = 'FileUploadComponent';
    component.ngOnChanges();
    fixture.detectChanges();
    expect(component.classes.step2.p[0]).toBe('active');
  });

  it('should call ngOnChanges lifecycle hook for AcknowledgementComponent', () => {
    component.componentName = 'AcknowledgementComponent';
    component.ngOnChanges();
    fixture.detectChanges();
    expect(component.classes.step4.p[0]).toBe('complete');
  });

});
