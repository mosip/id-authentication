export class BookingModel {

  constructor(
    private pre_registration_id: string,
    private registration_center_id: string,
    private reg_date: string,
    private time_slot_from: string,
    private time_slot_to: string
  ) { }
}
