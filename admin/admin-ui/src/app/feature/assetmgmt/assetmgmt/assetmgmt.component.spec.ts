import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AssetmgmtComponent } from './assetmgmt.component';

describe('AssetmgmtComponent', () => {
  let component: AssetmgmtComponent;
  let fixture: ComponentFixture<AssetmgmtComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AssetmgmtComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AssetmgmtComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
