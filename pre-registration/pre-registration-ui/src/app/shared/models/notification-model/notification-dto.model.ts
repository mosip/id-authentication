export class NotificationDtoModel {
    constructor(
        public name: string,
        public preId: string,
        public appointmentDate: string,
        public appointmentTime: string,
        public mobNum: string,
        public emailID: string
    ) { }
}