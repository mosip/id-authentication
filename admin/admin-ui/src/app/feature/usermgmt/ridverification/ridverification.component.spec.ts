import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RidverificationComponent } from './ridverification.component';

describe('RidverificationComponent', () => {
  let component: RidverificationComponent;
  let fixture: ComponentFixture<RidverificationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RidverificationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RidverificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
