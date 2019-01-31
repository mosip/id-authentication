import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CenterSelectionComponent } from './center-selection.component';

describe('CenterSelectionComponent', () => {
  let component: CenterSelectionComponent;
  let fixture: ComponentFixture<CenterSelectionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CenterSelectionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CenterSelectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
