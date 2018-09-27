import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LanguageSelectionComponent } from './language-selection.component';
import { MaterialModule } from '../material';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('LanguageSelectionComponent', () => {
  let component: LanguageSelectionComponent;
  let fixture: ComponentFixture<LanguageSelectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MaterialModule, BrowserAnimationsModule],
      declarations: [LanguageSelectionComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LanguageSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // Mock sessionStorage
  beforeEach(() => {
    var store = {};

    spyOn(sessionStorage, 'getItem').and.callFake((key: string): string => {
      return store[key] || null;
    });
    spyOn(sessionStorage, 'removeItem').and.callFake((key: string): void => {
      delete store[key];
    });
    spyOn(sessionStorage, 'setItem').and.callFake((key: string, value: string): string => {
      return store[key] = <string>value;
    });
    spyOn(sessionStorage, 'clear').and.callFake(() => {
      store = {};
    });
  });

  it('should create the component', async(() => {
    sessionStorage.setItem('lan', 'en');
    expect(component).toBeTruthy();
  }));

  it('should create the component', async(() => {
    sessionStorage.setItem('lan', 'en');
    component.ngOnInit();
  }));

  it('should create the component', async(() => {
    sessionStorage.setItem('lan', null);
    component.ngOnInit();
  }));

  it('should check func languageChange', async(() => {
    const event = {
      value: 'en'
    }
    component.languageChange(event);
  }));

});
