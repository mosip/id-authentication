import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing'
import { ParentComponent } from './parent.component';
import { StepperComponent } from '../stepper/stepper.component';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpLoaderFactory } from 'src/app/i18n.module';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';

describe('ParentComponent', () => {
  let component: ParentComponent;
  let fixture: ComponentFixture<ParentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ParentComponent, StepperComponent ],
      imports: [ 
        TranslateModule.forRoot({
        loader: {
            provide: TranslateLoader,
            useFactory: HttpLoaderFactory,
            deps: [HttpClient]
        }
    }),
    HttpClientModule,
    RouterTestingModule
  ],
  providers: [{
    provide: Router,
    useValue: {
      url: '/path'
   } 
  }]
})
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ParentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return demographic based on url', () => {
    const router = TestBed.get(Router);
    router.url = '/demographic';
    component.onActivate({});
    expect(component.componentName).toBe('DemographicComponent');
  });

  it('should return file Upload based on url', () => {
    const router = TestBed.get(Router);
    router.url = '/file-upload';
    component.onActivate({});
    expect(component.componentName).toBe('FileUploadComponent');
  });

  it('should return Center Selection based on url', () => {
    const router = TestBed.get(Router);
    router.url = '/pick-center';
    component.onActivate({});
    expect(component.componentName).toBe('CenterSelectionComponent');
  });

  it('should return Time Selection based on url', () => {
    const router = TestBed.get(Router);
    router.url = '/pick-time';
    component.onActivate({});
    expect(component.componentName).toBe('TimeSelectionComponent');
  });

  it('should return Acknowledgement based on url', () => {
    const router = TestBed.get(Router);
    router.url = '/acknowledgement';
    component.onActivate({});
    expect(component.componentName).toBe('AcknowledgementComponent');
  });

  it('should return Preview based on url', () => {
    const router = TestBed.get(Router);
    router.url = '/preview';
    component.onActivate({});
    expect(component.componentName).toBe('PreviewComponent');
  });

});
