import { BookingModel } from './booking.model';

export class BookingModelRequest {
    id = 'mosip.pre-registration.booking.book';
    ver = '1.0';
    reqTime = new Date().toDateString();

    constructor(private request: BookingModel[]) {}
}

