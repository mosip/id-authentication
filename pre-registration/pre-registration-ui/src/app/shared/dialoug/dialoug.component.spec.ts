import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialougComponent } from './dialoug.component';

describe('DialougComponent', () => {
  let component: DialougComponent;
  let fixture: ComponentFixture<DialougComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialougComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialougComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
