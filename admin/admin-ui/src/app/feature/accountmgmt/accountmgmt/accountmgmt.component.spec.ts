import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountmgmtComponent } from './accountmgmt.component';

describe('AccountmgmtComponent', () => {
  let component: AccountmgmtComponent;
  let fixture: ComponentFixture<AccountmgmtComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AccountmgmtComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountmgmtComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
