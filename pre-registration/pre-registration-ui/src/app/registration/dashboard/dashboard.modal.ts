export class Applicant {
  applicationID: string;
  name: string;
  appointmentDateTime: string;
  status: string;

  constructor(applicationID: string, name: string, appointmentDateTime: string, status: string) {
    this.applicationID = applicationID;
    this.name = name;
    this.appointmentDateTime = appointmentDateTime;
    this.status = status;
  }
}
