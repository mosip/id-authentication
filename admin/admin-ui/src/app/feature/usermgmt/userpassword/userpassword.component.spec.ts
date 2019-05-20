import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserpasswordComponent } from './userpassword.component';

describe('UserpasswordComponent', () => {
  let component: UserpasswordComponent;
  let fixture: ComponentFixture<UserpasswordComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserpasswordComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserpasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
