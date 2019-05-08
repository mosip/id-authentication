import { async, ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { MapComponent } from './map.component';
import { BookingService } from '../booking.service';

describe('MapComponent', () => {
  let component: MapComponent;
  let fixture: ComponentFixture<MapComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MapComponent]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', inject([BookingService], (service: BookingService) => {
    service.listOfCenters([{ id: '1001', latitude: 11.111, longitude: 11.11 }]);
    service.changeCoordinates([11.111, 11.11]);
    fixture.detectChanges();
    expect(component).toBeTruthy();
  }));

  it('should test ngOnInit', inject([BookingService], (service: BookingService) => {
    component.mapProvider = 'OSM';
    service.listOfCenters([{ id: '1001', latitude: 11.111, longitude: 11.11 }]);
    service.changeCoordinates([11.111, 11.11]);
    component.ngOnInit();
    fixture.detectChanges();
    expect(component.url).toBe(component.OSM_URL);

    component.mapProvider = 'GMAPS';
    service.listOfCenters([{ id: '1001', latitude: 11.111, longitude: 11.11 }]);
    service.changeCoordinates([11.111, 11.11]);
    component.ngOnInit();
    fixture.detectChanges();
    expect(component.url).toBe(component.googleMapsUrl);
  }));
});
