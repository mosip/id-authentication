import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UinComponent } from './uin.component';

describe('UinComponent', () => {
  let component: UinComponent;
  let fixture: ComponentFixture<UinComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UinComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UinComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
