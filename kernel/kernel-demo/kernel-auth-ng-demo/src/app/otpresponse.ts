export class OtpResponse {
    status: string;
    otp: string;

    constructor(status: string,  otp: string ) {
        this.status = status;
        this.otp = otp;
    }
}