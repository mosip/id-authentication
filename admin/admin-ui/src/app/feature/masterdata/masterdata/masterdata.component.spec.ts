import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MasterdataComponent } from './masterdata.component';

describe('MasterdataComponent', () => {
  let component: MasterdataComponent;
  let fixture: ComponentFixture<MasterdataComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MasterdataComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MasterdataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
