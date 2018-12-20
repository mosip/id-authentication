export interface RegistrationCentre {
    id: string;
    name: string;
    addressLine1: string;
    addressLine2: string;
    addressLine3: string;
    centerEndTime: string;
    centerStartTime: string;
    holidayLocationCode: string;
    contactPhone: string;
    centerTypeCode: string;
    isActive: boolean;
    languageCode: string;
    latitude: number;
    locationCode: string;
    longitude: number;
    lunchEndTime: string;
    lunchStartTime: string;
    numberOfKiosks: number;
    numberOfStations: number;
    perKioskProcessTime: string;
    timeZone: string;
    workingHours: string;
    contactPerson: string;
    lonLat: number[];
}
