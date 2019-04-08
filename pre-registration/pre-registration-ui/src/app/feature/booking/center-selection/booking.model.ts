export class BookingModel {
  constructor(
    private preRegistrationId: string, 
    private registration_center_id: string,
    private appointment_date: string,
    private time_slot_from: string,
    private time_slot_to: string
  ) {}
}
